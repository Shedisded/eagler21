# Multiplayer over `wss://` (EaglerXServer) — Design

Date: 2026-07-15
Status: Approved approach; spec for review.

## Goal

Make the TeaVM/browser build of Minecraft 1.21.11 connect to a remote **EaglerXServer** over a
browser WebSocket (`wss://`), speaking the **exact** current Eagler handshake + transport protocol, so the
existing "Multiplayer" UI in MC can join a real server and render its world. Singleplayer is deprioritized.
Performance work follows once multiplayer is playable.

Non-goals (this spec): EAG extras (skins/capes/voice/webview), server-side changes (the user runs their own
EaglerXServer), and the perf pass.

## Context / constraints

- MC 1.21.11 networking is **netty-based**. `Connection` *is* a `ChannelHandler`; `Connection.connect(addr,
  eventLoopGroup, conn)` builds a `Bootstrap.group(elg).channel(channelCls).connect(addr, port)` with the
  pipeline: `timeout → splitter(Varint21FrameDecoder) → FlowControlHandler → decoder → prepender
  (Varint21LengthFieldPrepender) → encoder → packet_handler(Connection)`. So MC frames packets as a
  **varint21 length-prefixed byte stream** (standard TCP MC protocol).
- The teavm shadow `net.minecraft.server.network.EventLoopGroupHolder` currently hardcodes
  `NioSocketChannel` — which cannot work in a browser (no TCP, no real selectors).
- Browsers can only open `WebSocket`. Binary WS frames are discrete messages (each frame has an implicit
  length), not a continuous byte stream.
- The repo already contains the Eagler client stack (1.8-era, reference): wss transport
  (`src/teavm/.../internal/teavm/TeaVMWebSocketClient` — wraps browser `WebSocket`, `binaryType=arraybuffer`,
  `send(byte[])`/`send(String)`/`isOpen()`/`close()` + open/message/close handlers) and the handshake state
  machine (`src/main/.../socket/protocol/handshake/HandshakerV3/V4/V5`, `HandshakePacketTypes`, `AuthTypes`).
- The user's **EaglerXServer** source is in `eaglerxserver/` (modern lax1dude Java server, Bukkit/Bungee/
  Velocity). Server supports handshake **V1–V5**. Server-side handshake wire format
  (`eaglerxserver/core/.../base/handshake/HandshakePacketTypes.java`, `HandshakerInstance.handlePacketInit`):
  - Packet ids: `CLIENT_VERSION=0x01`, `SERVER_VERSION=0x02`, `VERSION_MISMATCH=0x03`,
    `CLIENT_REQUEST_LOGIN=0x04`, `SERVER_ALLOW_LOGIN=0x05`, `SERVER_DENY_LOGIN=0x06`,
    `CLIENT_PROFILE_DATA=0x07`, `CLIENT_FINISH_LOGIN=0x08`, `SERVER_FINISH_LOGIN=0x09`,
    `SERVER_REDIRECT_TO=0x0A`, `SERVER_ERROR=0xFF`.
  - `CLIENT_VERSION(0x01)` carries `{eaglerBrand:String, eaglerVersionString:String, minecraftVersion:int,
    auth:boolean, authUsername:byte[]}`. Server tracks `handshakeProtocol` (V1–V5), `gameProtocol`
    (`getFinalVersion()`), `minecraftProtocol = minecraftVersion`.
  - States: `OPENED→CLIENT_VERSION→CLIENT_LOGIN→CLIENT_COMPLETE`.
- **The exact target handshake/game-protocol version must be read from `eaglerxserver/` during
  implementation** (the repo client is V5; confirm the server accepts V5 and what `gameProtocol` it maps
  1.21.11 to). Test target: user has a **live EaglerXServer at a `wss://` URL** (URL provided at test time).

## Architecture: a netty `Channel` over the browser WebSocket

Do **not** bypass netty. `Connection` and the whole client pipeline (framing, compression, packet codecs)
are netty handlers; reusing them is far less code and matches how Eagler's own netty stack works. Instead,
provide a custom **transport channel** and **event loop**, leaving MC's `Connection` untouched.

### New components (browser build; `teavm/src/main` unless noted)

1. **`EaglerWebSocketChannel` (netty `AbstractChannel`)** — the transport.
   - Backed by a browser `WebSocket` (reuse/port `TeaVMWebSocketClient`'s JSO wrapper into the 1.21.11
     package, or call it directly).
   - `doWrite(ChannelOutboundBuffer)`: drain outbound `ByteBuf`s and send as WS **binary** frames, applying
     the game-phase framing EaglerXServer expects (see "Framing").
   - Inbound WS binary message → `pipeline().fireChannelRead(Unpooled.wrappedBuffer(bytes))`.
   - `doConnect`: open the WebSocket to the `wss://` URL; complete the connect promise on WS `open`.
   - Config/metadata: minimal `ChannelConfig` (TCP_NODELAY is a no-op catch already handled by MC).
2. **`EaglerClientEventLoopGroup` / single-thread event loop** — netty needs an `EventLoop` to run the
   channel. Provide a `SingleThreadEventLoop` (or `DefaultEventLoop`) whose task queue is pumped by the
   TeaVM green-thread / browser event loop rather than a blocking `select()`. Runs channel tasks + the
   `ReadTimeoutHandler` scheduler. Ceiling: cooperative, no true parallelism (fine — one connection).
3. **`EaglerHandshakeHandler` (netty inbound/outbound handler)** — runs FIRST in the pipeline.
   - On channel active: send `CLIENT_VERSION(0x01)` with `{brand, versionString, mc protocol=1.21.11, auth,
     username}` per the repo `HandshakerV5` + server format.
   - Drive `0x02 → 0x04 → 0x05/0x06 → 0x07 → 0x08/0x09` (login/profile). On success, **remove itself** and
     let MC's normal client pipeline handle the raw stream from there.
   - Handle `VERSION_MISMATCH(0x03)`, `SERVER_DENY_LOGIN(0x06)`, `SERVER_ERROR(0xFF)` → disconnect with the
     server message (surfaced to MC's `ConnectScreen`).

### Modified components

4. **`EventLoopGroupHolder` (teavm shadow)** — a new holder variant (e.g. `WEBSOCKET`) returning the Eagler
   event loop + `EaglerWebSocketChannel.class`; make the client connect path use it for `wss` targets.
5. **Address plumbing** — MC connects by `InetSocketAddress` (host+port). The browser needs the full
   `wss://host:port/path`. Carry the WS URL from the "Add Server"/Direct-Connect input through to the
   channel. `EaglerWebSocketChannel.doConnect` reads the URL (from a thread-local/holder set when the user's
   server address is a `wss://`/`ws://` URL) rather than doing DNS/TCP.

### Framing (the one detail to confirm from `eaglerxserver/`)

MC produces a varint21 length-prefixed stream. Two possibilities for the game phase over WS, to be confirmed
from EaglerXServer's game-phase codec (`eaglerxserver/protocol-game` / core pipeline):
- (a) EaglerXServer expects **each WS binary frame = one length-prefixed chunk of the MC stream** → the
  channel forwards MC's ByteBufs as-is (simplest; keep the `splitter`/`prepender` in MC's pipeline).
- (b) EaglerXServer expects **each WS frame = one raw MC packet (no varint21)** → the channel strips/omits
  the varint21 prefix and MC's `splitter`/`prepender` are removed for the WS transport.
This choice is isolated to `EaglerWebSocketChannel` + whether the varint handlers stay in the pipeline;
confirm empirically against the live server in Phase 2.

## Data flow

1. User picks a server (`wss://…`) → MC `ConnectScreen.connect` → `Connection.connect(addr, WEBSOCKET holder,
   conn)`.
2. `Bootstrap` opens `EaglerWebSocketChannel` on the Eagler event loop → WebSocket connects to the URL.
3. `EaglerHandshakeHandler` runs the Eagler handshake to completion, then removes itself.
4. MC's normal client login → configuration → play packets flow over the WS transport; the world loads and
   renders (reusing all the client render fixes from prior sessions).

## Phasing (each phase = its own verify, batched relinks)

- **P1 — transport + event loop:** `EaglerWebSocketChannel` + event loop link and open a WS connection to
  the live server; raw bytes round-trip (log inbound/outbound). No handshake logic yet. Verify: connection
  opens, server's first handshake byte(s) arrive.
- **P2 — handshake:** implement `EaglerHandshakeHandler` (0x01–0x09) matching `eaglerxserver/` exactly;
  reach "handshake complete → MC login starts". Nail the framing (a vs b) here.
- **P3 — login→play:** MC login/encryption(offline)/configuration/play over the transport; world renders.
  Reuse offline-auth + render fixes already in the tree.
- **P4 (later):** EAG extras, reconnect/errors polish. Then the performance pass.

## Testing

- **Live:** point `chromium-run.js` at the multiplayer flow (drive the Multiplayer UI → Direct Connect →
  the user's `wss://` URL) and screenshot; read the page console for handshake/login logs. Needs the user's
  server URL at test time.
- **Local unit checks (no relink):** the handshake encode/decode is pure byte logic — add a small
  `demo()`/`assert` self-check comparing our `CLIENT_VERSION`/login bytes against the field order in
  `eaglerxserver/.../HandshakerV5` before relinking (per validate-before-relink).
- **Diagnostics:** reuse the JS hot-patch + console techniques for post-relink stalls/crashes.

## Risks / open questions

- **Framing (a vs b)** — resolved empirically in P2 from the live server; isolated to the channel.
- **netty event loop in TeaVM** — netty `SingleThreadEventLoop` assumes a run loop; may need a thin custom
  `EventLoop` that drains its task queue on the green thread instead of blocking. Contained to component #2.
- **Handshake version drift** — repo client is V5; confirm the server's accepted `handshakeProtocol` /
  `gameProtocol` and the `minecraftVersion` code for 1.21.11 from `eaglerxserver/` before P2.
- **Auth/online-mode** — target offline mode (`MSG_ONLINE_MODE` path avoided); server must allow it.
- **Relink cost** — ~30 min each; batch confirmed fixes, validate byte logic locally first.

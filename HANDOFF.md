# port-fable handoff — 2026-07-14 (TITLE / ONBOARDING SCREEN RENDERS PERFECTLY ✅ — link29)

Reply in Chinese only.

## LATEST (2026-07-14, link30): INPUT WORKS — click advances onboarding -> TitleScreen. See "link30" at bottom.
link29 got the onboarding screen rendering pixel-correct. link30 = Stage D input: mouse clicks now activate
buttons (verified: click Continue -> TitleScreen loads). Two fixes: (1) GLFW shim scales DOM CSS coords ->
canvas backing pixels; (2) `--offlineDeveloperMode` so Realms doesn't crash the TitleScreen. Full detail in
the "link30" section at the very bottom. Next: verify link30 screenshot of TitleScreen (panorama+menu),
then keyboard-driven flows, then in-world (deferred FaceBakery precision + ICU data).

## Session 6b (2026-07-13) — chromium harness (REAL WebGL2) + GL shim fixes; render-init path
User chose "C" (both phases in parallel toward a visible title screen). Progress:
- **CHROMIUM HARNESS WORKS** (`teavm/run/chromium-run.js` + `package.json`; `npm i` puppeteer done, chromium
  downloaded). It starts a static server (serves index.html + the 144MB classes.js + classes.js.map), launches
  headless chromium with a REAL WebGL2 context via swiftshader (`--enable-unsafe-swiftshader --use-gl=angle
  --use-angle=swiftshader --ignore-gpu-blocklist`), runs main([]), streams the page console, screenshots
  `#gameCanvas` → `teavm/run/title-screen.png`. Run: `node teavm/run/chromium-run.js [classes.js] [seconds]`.
  This gets PAST every node-boot fake-WebGL2 artifact — the GL errors it shows now are REAL shim bugs.
- **GL shim fixes (teavm/src/main/java/org/lwjgl/opengl/GL11.java), confirmed via chromium:**
  - `glEnable`/`glDisable` now filter to the WebGL2-legal capability set (BLEND/CULL_FACE/DEPTH_TEST/DITHER/
    POLYGON_OFFSET_FILL/SAMPLE_ALPHA_TO_COVERAGE/SAMPLE_COVERAGE/SCISSOR_TEST/STENCIL_TEST/RASTERIZER_DISCARD)
    and no-op the rest. GlDevice init calls glEnable(0x884F GL_TEXTURE_CUBE_MAP_SEAMLESS) + glEnable(0x8642
    GL_PROGRAM_POINT_SIZE) which WebGL2 lacks (always-on) → were throwing INVALID_ENUM.
  - `glTexImage2D`: (a) no-op target GL_PROXY_TEXTURE_2D (0x8064) — MC's max-texture-size probe; our
    glGetTexLevelParameteri returns 0 so the probe already falls back to GL_MAX_TEXTURE_SIZE; the proxy call
    only spammed INVALID_ENUM. (b) map internalformat GL_DEPTH_COMPONENT32 (0x81A7) → GL_DEPTH_COMPONENT32F
    (0x8CAC) — WebGL2 has no DEPTH_COMPONENT32; MC's MainTarget depth texture is FLOAT so needs the F variant.
    This was the `OpenGL error 1281` crash. (link11 bakes these.)
  - **HOW THESE WERE FOUND (fast, reusable):** inject `console.log` into the GL wrapper in classes.js (the
    generated `olo_GL11_glEnable` / the `.texImage2D(...)` call sites) to dump the exact enum/format args, run
    chromium, read them off the page console. No relink. (⚠️ escape carefully — a bad JS injection makes
    classes.js fail to parse → `main is not defined`; keep a `.bak` and a relink restores it.)
- **FIXED — the `subarray` crash was a CORE zero-copy bug in NativeHeap (@JSByRef).** Stack (via
  puppeteer `msg.stackTrace()`): DynamicTexture.upload → GlCommandEncoder.writeToTexture → GLShim.view0 →
  NativeHeap.view → subarray. The generated `NativeHeap.view` was allocating a FRESH 256MB Int8Array and
  copying HEAP byte-by-byte on EVERY call, then the `@JSBody script "array.data.subarray(...)"` hit `.data`
  on that raw Int8Array (undefined) → crash. Root cause: TeaVM marshals a plain `byte[]` @JSBody param by
  COPYING it to a JS typed array. Fix: annotate the param `@JSByRef` (org.teavm.jso.JSByRef) so TeaVM passes
  the byte[]'s underlying Int8Array BY REFERENCE (true zero-copy), and change the script to `array.subarray(
  begin, begin+len)` (no `.data`). This unblocks ALL heap→WebGL texture/buffer uploads and removes a 256MB
  copy-per-upload. (Earlier textures had null pixels so view() was never exercised until DynamicTexture.)
  CONFIRMED via classes.js hot-patch: boot advanced past texture upload into real ReadPixels ("GPU stall due
  to ReadPixels" from swiftshader = actual rendering).
- **FIXED — TimeUnit.convert(Duration) returned 0 (stub).** MC's realms `DataFetcher.createTask` does
  `resolution.convert(period)` and rejects any task with `i <= 0`: "Period of PT1M too short for selected
  resolution of MILLISECONDS" (60s → should be 60000ms). `PeriodicNotificationManager` (Minecraft init)
  triggers it. Implemented TTimeUnit.convert(Duration) = `convert(secs, SECONDS) + convert(nano, NANOSECONDS)`
  reusing the existing unit-to-unit convert. NOTE: TeaVM inlined the `return 0` stub as a constant at the call
  site, so this could NOT be hot-patched in classes.js — needed the relink (link12).
- **link12 bakes both fixes.** Chromium is now the driver of choice (real WebGL2); node-boot is retired for
  the GL path. Keep hot-patching classes.js to discover batches of shim bugs, then one relink per batch —
  BUT beware TeaVM inlines constant-returning stubs (can't hot-patch those; they need the relink).
- **link12 result = RENDER-INIT COMPLETE, now purely MISSING ASSETS.** Chromium boot passed ALL GL device/
  render init and reached MC's resource load: `FileNotFoundException minecraft:shaders/core/gui.vsh` (+fsh,
  position_tex_color, ...), missing title textures (minecraft.png/panorama/mojangstudios/edition), and finally
  `IllegalStateException: Failed to load grass color texture` (grass.png) → crash. This is the assets gate.

### ASSETS PIPELINE — IMPLEMENTED this session (link13 tests it)
- **Asset SOURCE solved:** `tools/setup_assets.sh` already exists and documents it — 1.21.11 IS a real Mojang
  version. Ran its core: downloaded the client jar (`game/client-1.21.11.jar`, piston-data sha1
  ba2df812...) and `unzip -x '*.class' 'META-INF/*'` into **`src/game/resources`** → full vanilla resources
  WITH `.mcassetsroot` in both assets/ and data/ (93 shaders, 3682 textures, font/default.json, en_us.json,
  grass.png, title textures — everything the title screen needs). ~16MB of actual content.
  (setup_assets.sh ALSO fetches the asset-index-29 object store to game/assets — sounds/extra langs — NOT
  done yet; not needed for the title screen. Run it if sounds/other locales are needed later.)
- **Browser DELIVERY mechanism (why not classpath):** MC's VanillaPackResources WALKS a real FS
  (Files.walk/newDirectoryStream+newInputStream); TeaVM's Class.getResource returns null and embedded
  getResourceAsStream can't be listed → classpath packs are impossible. TeaVM's default FS IS a walkable
  in-memory VFS, so: unzip the vanilla resources into the VFS and point the pack there. Pieces:
  1. `teavm/run/vanilla-assets.zip` = ZIP_STORED of src/game/resources/{assets,data} (17865 entries, 16MB),
     served by `chromium-run.js` (.zip MIME already handled).
  2. Page harness (`teavm/run/index.html`) fetches `vanilla-assets.zip` → `window.__EAGLER_ZIP` (Int8Array)
     BEFORE `main([])` (avoids sync-XHR binary pain; async fetch then main in .finally()).
  3. `teavm/src/main/.../boot/EaglerAssets.install()` (called from EaglerClientMain BEFORE Main.main) reads
     the global via `@JSBody`, copies Int8Array→byte[], and unzips via ZipInputStream → `Files.createDirectories`
     + `Files.write` into VFS `/eagler-assets/{assets,data}/...`.
  4. `ClientPackSource.createVanillaPackSource` (src/game) now calls
     `.pushUniversalPath(Paths.get("/eagler-assets"))` beside the (empty-on-TeaVM) pushJarResources.
- **ASSET PIPELINE WORKS ✅ (link14 chromium):** `[EaglerAssets] installed 17865 vanilla resource files into
  /eagler-assets in 1038ms` — TeaVM's in-memory VFS fully supports createDirectories/write (unzip) AND the
  pack reader's newDirectoryStream/walk/newInputStream/exists/isDirectory (reads). ALL the shader/grass.png/
  title-texture FileNotFoundExceptions are GONE. classes.js stayed ~137MB (TeaVM did NOT auto-embed
  src/game/resources — good, no bloat; delivery is purely via the zip). Int8Array→byte[] 16M copy + unzip of
  17865 files = ~1s, fine.
  - ⚠️ GOTCHA that cost a cycle: after a `:jar` failure (stray `.class` files in src/game/resources — the
    `unzip -x '*.class'` in setup_assets DIDN'T exclude them; had to `rm -rf src/game/resources/{com,net}` +
    `find -name '*.class' -delete`), `:teavm:generateJavaScript` came back UP-TO-DATE and served a STALE
    classes.js (older than the recompiled classes). FIX: `rm classes.js` before generateJavaScript to force
    regen (check `stat` mtime of classes.js vs the boot .class if in doubt).
- **link16 fixes — shader GLSL translation + Uint8Array texture uploads (getting to pixels):**
  With regex fixed, MC parsed shaders and tried to COMPILE them (real WebGL2), surfacing two real shim bugs:
  1. **GLSL version:** MC core shaders are desktop `#version 330`; WebGL2 needs GLSL ES 3.00 → `'' : invalid
     version directive` + cascade `'layout' : syntax error`. Fix in `org/lwjgl/opengl/GL20C.nglShaderSource`:
     `translateToEs300()` strips the original #version line and prepends `#version 300 es` + `precision highp
     float;/int;/sampler2D;` (ES fragment shaders require explicit float precision). MC's in/out/layout(std140)/
     texture() syntax is already ES-300 compatible. (If specific shaders still fail on other GLSL-330-isms,
     extend this translator.)
  2. **Texture upload type:** `texSubImage2D: type UNSIGNED_BYTE but ArrayBufferView not Uint8Array` — our
     zero-copy heap view returned an Int8Array (signed); WebGL2 texImage2D/texSubImage2D/readPixels with
     UNSIGNED_BYTE require a Uint8Array. Fixed `NativeHeap.subarray` @JSBody to
     `new Uint8Array(array.buffer, array.byteOffset+begin, len)` — same ArrayBuffer, unsigned view (still
     zero-copy, still writes back to HEAP for readPixels; bufferData accepts it too).
  These are batched into link16. Remaining non-fatal-looking errors to re-check after: `null reading '$read18'`
  (mojangstudios.png texture LOAD, maybe PNG/stream), and the fatal `null reading '$next2'` during
  "Reloading ResourceManager: vanilla" — may cascade-resolve once shaders compile + textures upload, else
  diagnose with the classes.js console.trace technique on the link16 build.
- **regex blocker (link15 fixes): TeaVM regex engine lacks `\h` `\v` `\z`.** With assets present, MC parses
  shaders and threw `PatternSyntaxException` at the `\h` in GlslPreprocessor's `#moj_import`/`#version` regexes.
  TeaVM's regex has no `\h` (horizontal ws), `\v` (vertical ws), or `\z` (absolute end). Patched the 2 MC
  files that use them (only 2 in the whole tree): `com/mojang/blaze3d/preprocessor/GlslPreprocessor.java`
  (\h→`[ \t]`, \v→`[\n\r]`, \z→`$`) and `net/minecraft/util/StringUtil.java` (\v→`[\n\x0B\f\r]`). Desktop-
  equivalent (shaders/text use ASCII ws). If more regex features surface later, consider a TPattern.compile
  classlibOverride that rewrites `\h`/`\v`/`\z` globally instead of per-file.
- **ORDERING INSIGHT for the two phases:** MC tolerates the EMPTY vanilla pack through `new Minecraft()`
  init (resource reload logs errors but continues) — the title SCREEN render needs font/GUI/logo textures,
  which happens AFTER init completes and the render loop starts. So finish the GL/render-init fixes FIRST
  (subarray + whatever follows), get init to complete, THEN the assets pipeline matters.
- **ASSETS PIPELINE — scoped (do after render-init works):** redirect point is `ClientPackSource.
  createVanillaPackSource` (src/game) — add `.pushUniversalPath(Paths.get("/eagler-assets"))` beside the
  (empty-on-TeaVM) `.pushJarResources()`. Requires: (1) assets unzipped into the TeaVM VFS at
  `/eagler-assets/{assets,data}/...` at boot (fetch a zip over HTTP in the chromium harness → unzip via
  java.util.zip → write through TFiles, which delegates to TeaVM's in-memory VFS FileSystemProvider —
  newInputStream/newOutputStream/newDirectoryStream/createDirectory all present, so read/write/list should
  work; VERIFY walk). (2) ⚠️ ASSET SOURCING: `desktopRuntime/resources/` is an EaglercraftX **1.8-era** pack
  (v1_8 refs, mcpatcher/optifine dirs) — likely NOT valid 1.21.11 resources (shaders/GUI/font changed a lot).
  A real title screen probably needs the actual 1.21.11 client assets (client jar `assets/` + the asset-index
  object store from Mojang). This is the big open question for the assets phase.


**Stage C is DONE**: `:teavm:generateJavaScript` produces a 144MB `teavm/build/generated/teavm/js/classes.js`
with 0 not-found / 0 missing / 0 native-method errors. Then drove the RUNTIME (node-boot) from "won't start"
to deep inside `new Minecraft()` render-init, fixing ~9 real bugs. See the two detailed sections below
("Session 5 (link tail)" and this one) for specifics. CURRENT STATE:
- node-boot now runs through: arg-parse → Bootstrap (1166 blocks/1505 items) → resource/pack setup →
  offline auth → window/GLFW → GL device init (GraphicsWorkarounds, capabilities) → GPU buffer creation.
- **node-boot has hit its CEILING**: remaining errors are all fake-WebGL2 artifacts (the Proxy in
  `teavm/run/node-boot.js` returns 0/undefined where a real WebGL2 returns sizes/typed-arrays). Latest:
  `Buffer size must be greater than zero` (fixed by mock returning sane GL limits), then `TypeError: ...
  undefined (reading 'subarray')` in the GL buffer-upload path. These will NOT occur under real WebGL2.
- **THE TWO REMAINING PHASES (both large, mostly environment-gated):**
  1. **Assets pipeline** (NEXT STEPS #3) — boot logs `/assets/.mcassetsroot does not exist in classpath`,
     `indexes/29.json` missing; `VanillaPackResourcesBuilder` (src/game) finds vanilla resources via
     `getResourceAsStream("/assets/.mcassetsroot")`. Vanilla pack loads EMPTY → no font/GUI/shaders → no
     title screen. Source material: `desktopRuntime/resources/` (19MB EaglercraftX pack: pack.mcmeta +
     assets/minecraft/{blockstates,font,lang,models,textures,sounds}) but NO `.mcassetsroot`, NO `data/` —
     needs adapting to MC's classpath/pack layout (bundle into TeaVM resources, or HTTP-fetch into the VFS).
  2. **Real WebGL2 via chromium/puppeteer** (swiftshader) — needed for a working GpuDevice + actual pixels.
     `npm i puppeteer`; drive `teavm/run/index.html`. The 144MB module is heavy but loadable. Only here can
     the GL upload paths (subarray etc.) and the title-screen render actually run.
- **DEBUG TECHNIQUE that carried this session (reuse it):** TeaVM exceptions carry NO Java stack frames, so
  MC crash reports show only the message. classes.js is a plain 144MB JS file — inject `console.trace()` at
  the throw/suspect site and re-run node-boot to get the REAL JS caller stack, WITHOUT a 20-min relink.
  Back up first (`cp classes.js classes.js.bak`); a relink regenerates it. Used to pin the guava-TypeToken
  origin (DFU Cocartesian), the FileChannel caller (DownloadQueue), and the toLowerCase-on-number
  (GraphicsWorkarounds reading a mock GL string).
- **src/game patches this session (all desktop-equivalent, shared source):** Main.java (skip
  DataFixers.optimize), Options.java + SoundManager.java (anonymous gson TypeToken → getParameterized),
  Minecraft.java (force offline YggdrasilAuthenticationService.createOffline + UserApiService.OFFLINE).
  RenderSystem.getDevice() probe was added then REMOVED. `:compileJava` stays green.



## Session 5 (2026-07-12) — wave-3 not-found CLEARED (0), then 2 gates past it
Picked up at link8's tail. Fast pre-check (`compileClasslibOverrides/UnsafeStub/Java`) all green — every
A–F item from Session 4 had landed. Ran full links (each ~19-22 min; ONE at a time, no javap/subagents).
- **link1 (this session)**: 3 not-found — `SelectableChannel.register(Selector,int,Object)SelectionKey`,
  `PrivateKey.getAlgorithm()`/`getEncoded()`. Fixed: added `register` to TSelectableChannel; declared
  both methods on TPrivateKey.
- **link2**: not-found = **0**. New error class surfaced: **"implements X which is missing in the
  classpath"** (was masked under the not-found before) — `PrivateKey implements java.security.Key` and
  netty `PromiseTask implements java.util.concurrent.RunnableFuture`. Root: java.security.* /
  java.util.concurrent.* are java.* (dontFallback → map to T*, never fall back to the real class), and
  TeaVM ships **no TKey / TRunnableFuture**. Created both as classlibOverrides interfaces (TKey extends
  TSerializable w/ getAlgorithm/getFormat/getEncoded; TRunnableFuture extends TRunnable+TFuture). NOTE:
  TPublicKey also `extends java.security.Key` — TKey fixes it too.
- **link3**: not-found=0, missing=0. New error class: **"Native method ... has no implementation"** — 75
  refs, ALL netty JNI native transports `io.netty.channel.{kqueue(30),epoll(23),unix(22)}`. Reachability
  root (from the TeaVM stack trace) is a SINGLE MC class: `net.minecraft.server.network.
  EventLoopGroupHolder` — its static EPOLL/KQUEUE holder fields' initializers construct Epoll*/KQueue*
  channels+IoHandlers, and `remote()` calls `KQueue.isAvailable()/Epoll.isAvailable()`. Fix: **shadowed
  EventLoopGroupHolder in teavm/src/main** (drop EPOLL/KQUEUE fields+imports, remote() always returns NIO;
  everything else identical so descriptors resolve). Kept NIO + LOCAL (pure-Java netty).
  ⚠️ comment gotcha: `Epoll*/KQueue*` inside a block comment closes it early; em-dash is an illegal char —
  use ASCII line comments.
- **link4 running** — expected green or a tiny native/reachability tail exposed by dropping epoll/kqueue.
- **Error-class ladder past not-found (NEW this session, watch for these):** once `was not found` hits 0,
  TeaVM still fails the build on (a) "implements/extends X which is missing in the classpath" — create the
  missing T* super, and (b) "Native method X has no implementation" — cut its reachability root (shadow the
  MC/lib class that drags it in) rather than stubbing every leaf. `grep "which is missing"` and
  `grep "has no implementation"` on the link log alongside the `was not found` grep.
- **link6 (fixes above) = GREEN. STAGE C LINK COMPLETE ✅** — 144MB `teavm/build/generated/teavm/js/
  classes.js` generated, 0 not-found/missing/native errors. Full MC client links to JS.

### RUNTIME PHASE (node-boot) — started this session
Loop: `node teavm/run/node-boot.js teavm/build/generated/teavm/js/classes.js` (needs
`NODE_OPTIONS=--max-old-space-size=8192` for the 144MB module; pass the classes.js path — a plain relative
path fails `require`, use Resolve-Path/absolute). Fixes so far, each a real bug (not a mock artifact):
- **joptsimple File/Integer converter** (1st runtime error): MC `Main.main` uses `.ofType(File.class)`
  (gameDir/assetsDir/resourcePackDir) + `.ofType(Integer.class)` (width/height/proxyPort...). joptsimple's
  `Reflection.findConverter` discovers converters reflectively AND the resulting converters invoke reflectively
  at convert time; TeaVM has no reflective metadata for File/Integer → `IllegalArgumentException: class
  java.io.File is not a value type` at runtime. Fix: **shadowed `joptsimple.internal.Reflection`** in
  teavm/src/main — hardcoded reflection-free File/Integer ValueConverters at the top of findConverter, kept
  the rest verbatim (link-preserved). All joptsimple classes already LINK (this was purely runtime).
- **TFile.toPath()** was a `throw new UnsupportedOperationException` stub → broke `Minecraft.crash` →
  `saveReport` (writes the crash report file). Implemented via `TPaths.get(getPath())` (TeaVM ships TPath/TPaths).
- **TeaVM 0.15.0 BUG in `org.teavm.classlib.impl.console.JsConsolePrintStream.flush()`** — upstream does
  `flushAction = null; flushAction.run();` (nulls the field then calls run() on null) → browser
  `TypeError: Cannot read properties of null (reading '$_03702')` inside flush → **kills every slf4j log line
  and hides the real crash**. Fixed by copying the class into classlibOverrides (`org/teavm/classlib/impl/
  console/`) with flush() stashing the Runnable in a local first. NOTE: patchedClasslib EXCLUDE strategy lets
  classlibOverrides override ANY classlib class, not just T* java.* mappings — useful lever for TeaVM-internal bugs.
- **TypeToken / generic-reflection wall (link8) — the real primary crash, now root-caused + worked around.**
  With logging fixed, link7's crash report finally showed the primary: `IllegalArgumentException: class
  com.google.common.reflect.TypeToken isn't parameterized`, Description "Argument parsing".
  - **ROOT CAUSE (fundamental TeaVM 0.15 limitation):** `Class.getGenericSuperclass()` is a stub — generated
    JS `jl_Class_getGenericSuperclass = $this => jl_Class_getSuperclass($this)` returns the RAW superclass.
    Both guava `com.google.common.reflect.TypeToken` and gson `com.google.gson.reflect.TypeToken` capture
    their type param via `getClass().getGenericSuperclass()` expecting a ParameterizedType; they get a raw
    Class → guava throws "isn't parameterized", gson throws "Missing type parameter." TeaVM's reflection
    metadata (`classInfo.reflection()`: fieldCount/methodCount/typeParameterCount only) does NOT retain the
    generic-superclass signature, so implementing getGenericSuperclass properly needs a TeaVM compiler change
    (retain+expose the Signature attr) — multi-day, out of scope. TeaVM DOES ship TParameterizedType(Impl)/
    TTypeVariable(Impl), just doesn't populate genericSuperclass.
  - **KEY DEBUG TECHNIQUE (no relink!):** classes.js is a plain 144MB JS file — you can edit it directly to
    inject `console.trace()` and re-run node-boot to get the real JS call stack (TeaVM exceptions carry NO
    Java stack frames, so crash reports show only the message). Injected a trace into `cgcr_TypeCapture_capture`
    right where it detects the non-ParameterizedType; the stack named the exact caller:
    `com.mojang.datafixers.optics.Cocartesian$Mu.<clinit>` → via `DataFixers.optimize` → TypeRewriteRule$All.
    (Back up the file first; `mv .bak` to restore; a relink regenerates it anyway.)
  - **WORKAROUND (spot-fixes, all in src/game, desktop-equivalent):** MC's own source has only TWO anonymous
    `new TypeToken<...>(){}` (verified `grep -rn "new TypeToken"`): Options.LIST_OF_STRINGS_TYPE and
    SoundManager.SOUND_EVENT_REGISTRATION_TYPE (both gson) — rewrote to `TypeToken.getParameterized(raw, args)`
    (builds the same ParameterizedType reflection-free, works on TeaVM, identical on desktop). The guava/DFU
    one is library code — avoided by skipping `DataFixers.optimize()` in Main.java (returns
    `CompletableFuture.completedFuture(null)`; optimize is a pure startup perf pre-compute, the DFU optics
    graph is only needed for actual data-fixing = loading old worlds, never at the title screen).
  - ⚠️ WHY THIS IS BOUNDED, not pervasive: MC 1.21's main data path is `com.mojang.serialization.Codec`,
    which does NOT use TypeToken per-op — proof: `Bootstrap.bootStrap()` (registers all 1166 blocks/1505 items
    via codecs) PASSED before this crash. Only DFU optics (type rewriting) + those 2 gson spots use anonymous
    TypeToken on the boot path. Watch for more library-internal TypeTokens (gson adapters) as boot proceeds;
    fix each by the same console.trace-locate → getParameterized/skip pattern.
- **link9 — in-memory FileChannel (unblocked DownloadQueue), then offline-auth (link10).**
  - `FileChannel.open` threw (Stage-D stub). console.trace-located to `Minecraft` ctor → `DownloadedPackSource`
    → `DownloadQueue` → `JsonEventLog.open` (server-pack download event log). Rather than shadow DownloadQueue,
    gave `TFileChannel` a real **in-memory growable-byte[] backing** (open/read(at)/write/write(at)/size/
    truncate/force/position/tryLock/close; map() still throws — no mmap) + a dummy always-valid TFileLock.
    Single-session, not yet VFS-persistent (Stage-D save concern), but unblocks all title-path FileChannel use.
    This jumped node-boot from ~57 lines to **8198** — huge progress into the Minecraft constructor.
  - Next primary: `RangeError: Maximum call stack size exceeded`. The 8198 lines were **2714 identical**
    `IOException: URL.openConnection(Proxy) not supported` — authlib's `YggdrasilServicesKeyInfo` fetching
    `https://api.minecraftservices.com/publickeys` (services key set for signature validation), which our URL
    stub throws on, and authlib **retries recursively → stack overflow**. (These were NOT missing-asset errors.)
    Fix: force offline auth in `Minecraft` ctor — `YggdrasilAuthenticationService.createOffline(proxy)` always
    (line ~415, was `new YggdrasilAuthenticationService(proxy)` when !offlineDeveloperMode), and
    `createUserApiService` always returns `UserApiService.OFFLINE` (~line 825). createOffline uses an empty
    local ServicesKeySet — never touches the network. Browser build is inherently offline (no Mojang auth).
  - Removed the temporary getDevice() printStackTrace probe from RenderSystem (served its purpose).
- **NEXT after link10 (expected):** with auth offline, boot proceeds past the services setup and should hit
  the REAL assets gap — `VanillaPackResourcesBuilder` (src/game) looks for `/assets/.mcassetsroot` +
  `/data/.mcassetsroot` via getResourceAsStream; boot8 already logged "does not exist in classpath", so the
  vanilla pack is EMPTY. Building the title screen needs the default font + GUI textures + shaders. THE ASSETS
  PIPELINE (NEXT STEPS #3) is now the gating milestone. Source material: `desktopRuntime/resources/` is a 19MB
  EaglercraftX-style resource PACK (pack.mcmeta at root; assets/minecraft/{blockstates,font,lang,models,
  textures,sounds,...}) — but NO `.mcassetsroot` and NO `data/`, so it needs adapting to MC's classpath/pack
  layout. Also: node-boot's Proxy WebGL2 is fake — a real title screen needs chromium/puppeteer (swiftshader)
  for a working GpuDevice + pixels; node-boot's value from here is limited to surfacing non-render errors.
- **STALE getDevice() probe REMOVED** (was: `src/game/.../RenderSystem.getDevice()` temporary
  `new Throwable(...).printStackTrace()` before the throw. It only fires from crash-report gen (secondary,
  harmless), was useful to prove getDevice-null wasn't the primary. REMOVE once the title screen renders.
- **SUPERSEDED BLOCKER (was the red herring):** `new Minecraft(gameconfig)` throws a PRIMARY exception
  (NOT yet identified — DEVICE stays null, so `new GlDevice()` at RenderSystem.initRenderer likely threw).
  A temporary probe added in `src/game/.../RenderSystem.getDevice()` (`new Throwable(...).printStackTrace()`
  before the throw — DESKTOP-SAFE, REMOVE LATER) confirmed the only null-getDevice call is SECONDARY, from
  crash-report gen (`Minecraft.fillSystemReport` → "Render Extensions" detail → getDevice). With logging now
  fixed, link7's node-boot run should finally print the real primary stack. GlDevice ctor (blaze3d/opengl/
  GlDevice.java:64) starts `GLFW.glfwMakeContextCurrent` + `GL.createCapabilities()` then GL queries — suspect
  our WebGL/GLFW shim returns something that makes a helper (GlDebugLabel/VertexArrayCache/BufferStorage/
  DirectStateAccess/GraphicsWorkarounds .create()) throw. ⚠️ node's Proxy WebGL2 is fake — if the primary
  error is genuinely a GL-value problem, may need puppeteer/chromium (swiftshader) for a real WebGL2 context.

## Goal
Port Minecraft 1.21.11 to the browser via TeaVM + an Eaglercraft-style platform layer.
- **Stage A** ✅ Desktop LWJGL3 runtime (`./gradlew runClient` → world loads)
- **Stage B** ✅ LWJGL→WebGL2/stub shim family complete (`:teavm:compileJava` green)
- **Stage C** 🔄 Full-client TeaVM link — **in progress**, iterating not-found waves
- **Stage D** ⬜ Input / playable

## Session 2 (2026-07-04b) — commits d48b9f9f, 09059d41
- **Shim family DONE**: all of `org/lwjgl/{opengl,stb,openal,util/freetype,util/tinyfd}`
  written; real pure-Java PNG decoder in `stb/STBImage`. teavm module compiles.
- **generateJavaScript OOM fixed**: full client needs >4G. `gradle.properties`
  now `-Xmx12G`. With 12G the link finishes analysis in ~4 min.
- **First not-found wave cleared** (all verified gone): JvmProfiler shadow
  (kills getModule + FlightRecorder), TClass.getSigners, TReentrantReadWriteLock
  read/writeLock+counts, TRuntime.addShutdownHook, TProxy + client Main proxy
  patch, IdentifierPattern asPredicate patch.
- **Second wave mapped & root-caused** — see NEXT STEPS. Big finding: the ~11
  "ICU cluster" refs (Locale.*, Collections.unmodifiableSorted*,
  StringCharacterIterator, ObjectStreamField, FileChannel$MapMode.READ_ONLY,
  FileInputStream.getChannel, Thread.dumpStack) are all dragged in by the real
  **icu4j-77.1** dependency, reachable from exactly 4 MC classes:
  `client/gui/Font`, `renderer/item/properties/select/LocalTime`,
  `resources/language/FormattedBidiReorder`, `gui/screens/CreateBuffetWorldScreen`.
  Font is too central to shadow → provide java.* link-stubs (ICU won't actually
  execute the number/break paths at the title screen; ICU *data* resources are a
  later runtime concern if it does).

## Session 3 (2026-07-04c) — wave 2 CLEARED, wave 3 mapped
- **Wave 2 (23 refs) DONE** — 24 files, `compileClasslibOverrides/UnsafeStub/Java` all green.
  - 8 TeaVM classes source-extracted into `classlibOverrides` + method added (whole-class
    override forces full copy): TLocale/TCollections/TSpliterators/TThread/TFileInputStream/
    TSystem/TFile/TURL.
  - Method-adds: TClass.getResource→null, TStackWalker(getInstance(Set,int)+walk+StackFrame),
    TCompletableFuture.exceptionallyComposeAsync.
  - New stubs: TStringCharacterIterator, TObjectStreamField, TPathMatcher, TFileChannel(+MapMode),
    TInetSocketAddress, TIDN, TManagementFactory + TRuntimeMXBean/TThreadMXBean/TThreadInfo/
    TGarbageCollectorMXBean.
  - **Shadow win**: `net/minecraft/util/monitoring/jmx/MinecraftServerStatistics` → no-op
    `registerJmxMonitoring`. It was the ONLY class touching `javax.management` (14 types) and the
    only caller of `ManagementFactory.getPlatformMBeanServer`. Shadowing it deletes the whole JMX
    surface from the link, so TManagementFactory deliberately OMITS getPlatformMBeanServer.
- **Wave 2 → wave 3 EXPLOSION**: satisfying the gateway classes made their transitive users
  reachable → ~100 new not-found refs, and the link now runs **~19 min** (deeper analysis; keep
  `compile*Java` as the fast 7s pre-check before every `generateJavaScript`).
- **This session cleared & VERIFIED (link3, wave3 unique 100→82):** `MemoryStack.getPointer()`
  long→**int** (descriptor bug), `PointerBuffer.limit()I`, unsafeStubs `jdk.jfr.Event` (killed all 18
  netty `*Event.commit/isEnabled/shouldCommit` refs) + `jdk.jfr.FlightRecorder` +
  `javax.sound.sampled.AudioFormat`(+Encoding.PCM_SIGNED, +getEncoding).
- **Also fixed in-tree AFTER link3 (pending next link, 5 refs):** TLocale `getExtensionKeys/getISO3Country`,
  TThreadInfo `getThreadName`, TAtomicLongArray `lazySet(I,J)` (descriptor bug int→long),
  TAtomicReferenceArray `compareAndExchange`.

## Session 4 (2026-07-05) — netty invoke gate CLEARED; guava + full stub batch landed
Commits this session, in order: `246a63f1` (wave-2/3 batch de-staged), `23d75993`
(cleaners+VHBBA+TVarHandle), `0290eb26` (PD0+ARCBB), `897fbf74` (guava AbstractFutureState +
AdaptivePoolingAllocator shadows + 31 java.* stubs), `327adc1d` (extract+add method stubs),
`ec393b99` (VHF+PlatformDependent shadows close the invoke gate + 2nd-order stub methods),
`758a2e1d` (Selector cluster + net/process/security tails), `02235b36` (SelectionKey/Scanner/
Process/Socket 5th-order tails).
- **LINK TRAJECTORY (unique not-found)**: link4 **82** → link5 **72** (netty invokeExact all gone)
  → link6 **57** → link7 **24** (invoke cluster **0** — netty+guava fully cleared) → link8 **15**
  → link9 running (expected green or a tiny tail). Each wave after the invoke gate is just shallow
  Nth-order stub methods on the newly-resolved java.* classes (return null/false/0/throw — Stage D).
  ⚠️ Verify method-adds by RE-LINK, not grep: a wrong descriptor is a silent not-found, and a grep
  for the method name can false-positive on doc comments (bit me once with StringJoiner.add).
- **NEXT PHASE once link9 is green**: `node teavm/run/node-boot.js teavm/build/generated/teavm/js/classes.js`
  (harness mocks DOM/WebGL2, calls main([])). Fix runtime `<clinit>` errors (see Hard-won fact #3:
  an exception mid-clinit leaves silent nulls) until it reaches the render loop; then chromium/
  puppeteer for real title-screen pixels. Then the assets pipeline (NEXT STEPS #3) and Stage D input.
- **Verified deltas**: link4 82→78, link5 (PD0/ARCBB) 78→72 (all netty invokeExact GONE),
  link6 (guava + 31 stubs + extract-adds) 72→57 in 15m37s on a quiet machine. link6's remaining
  57 were almost all **2nd-order**: methods on the now-resolved stub classes (Socket/ServerSocket/
  NetworkInterface/Semaphore/CountDownLatch/StampedLock/FileChannel.map/etc.) + the LAST 2 netty
  invoke refs (`VarHandleFactory.lookup()`, `PlatformDependent.publicLookup()`).
- **netty invoke gate now FULLY closed** (post-link6 fixes, uncommitted at this note): shadowed
  `VarHandleFactory` (report unsupported, no lookup) and `PlatformDependent` (1889L copy; patched
  only `estimateMaxDirectMemory()` to drop the publicLookup/findStatic/findVirtual reflection →
  Runtime.maxMemory() fallback; `findVarHandleOfIntField` is now callerless → dropped). That plus
  a wave of stub method-adds (net/concurrent/nio/lang, 3 subagents + main thread) is the batch
  going into link7. Also: `TMappedByteBuffer` (abstract extends TByteBuffer) for FileChannel.map,
  `STBIWriteCallback.free()` (NativeImage$WriteCallback extends it — javap-confirmed).
- **⚠️ LINK PERF**: run `generateJavaScript` on a QUIET machine. link5 took **1h25m** (vs the
  usual ~16min) because 3 subagents + javap JVMs were running concurrently and thrashed the 12G
  heap. Do NOT spawn subagents / run javap during a link.
- **Subagent playbook that worked** (user asked to parallelize): dispatch background
  `general-purpose` agents to WRITE stub/extract files with NO gradle (forbid it explicitly, or a
  concurrent link dies), each owning disjoint files; ship-check via `unzip`/`javap`; then the main
  thread does ONE central `compile*` (7s) to catch errors + ONE link. 3 agents wrote the 31
  create-stubs; 1 wrote 7 nio/simple extracts. Note: a global session-limit reset mid-run killed
  one agent with zero output — re-dispatch is cheap and idempotent (ship-check skips existing).
- **The invoke gate, root-caused precisely**: netty AND guava both use the "VarHandle/Unsafe
  fast path + pure-Java fallback" idiom. TeaVM links ALL branches (dead fast paths too), so each
  such class reports `invokeExact` / `VarHandle.get-set` / `MethodHandles.lookup()` not-found.
  `invokeExact` + VarHandle access are **signature-polymorphic** → CANNOT be stubbed on
  MethodHandle/VarHandle. `MethodHandles.lookup()` CANNOT be added to TMethodHandles either — it
  just spawns `findVarHandle/findVirtual` → polymorphic invoke (dead end, DO NOT try). **Only fix
  = shadow each holder class so the fast-path bytecode is gone.**
- **DONE this session**:
  - Shadow `io/netty/util/internal/CleanerJava6/9/24Linker/25` → unsupported (isSupported=false,
    allocate throws, free no-op). Killed 5 invokeExact + the `Class VarHandle` ref (link4 verified).
  - Shadow `io/netty/buffer/VarHandleByteBufferAccess` → manual byte-order (self-check passed vs
    java.nio). Drops `PlatformDependent.*View()` + VarHandleFactory reachability.
  - New classlibOverride `TVarHandle` (java.*→T): **static fences only** (storeStoreFence etc.), NO
    polymorphic methods on purpose → forces every remaining VarHandle instance-user to surface.
  - Shadow `PlatformDependent0` (1135→674): clinit → all-unavailable (`UNSAFE=null`), the 5
    MethodHandle methods → plain-Java/throw, drop 4 MH fields. Kills its 9 invoke refs. Unsafe
    mem-ops kept verbatim (link vs `unsafeStub` sun.misc.Unsafe; runtime `hasUnsafe()=false` guards).
  - Shadow `AbstractReferenceCountedByteBuf` clinit → hardcode the AtomicIntegerFieldUpdater path
    (drop the Unsafe + VarHandle refcount-updater switch cases).
- **NEW second-order find**: once `TVarHandle` made the type resolve, **guava**
  `com.google.common.util.concurrent.AbstractFutureState$VarHandleAtomicHelper` surfaced (9 refs:
  VarHandle compareAndSet/getAndSet/setRelease + `Lookup.findVarHandle`). Guava's future state
  machine — same idiom, needs the same treatment.
- **WAVE 3 invoke tail — batch-3 (the hard remainder)**:
  1. **guava `AbstractFutureState`** (889 lines, ~5 nested helpers; src at `/tmp/gsrc`): shadow to
     keep ONLY the `AtomicReferenceFieldUpdaterAtomicHelper` path, drop VarHandle+Unsafe helpers.
     Delicate — it is the ListenableFuture core, used everywhere; get the state machine right.
     Tactics: clinit cascade is `VarHandleAtomicHelperMaker.tryMake…() → new UnsafeAtomicHelper()
     → new AtomicReferenceFieldUpdaterAtomicHelper() → new SynchronizedHelper()`. Replace the whole
     clinit with `ATOMIC_HELPER = new AtomicReferenceFieldUpdaterAtomicHelper();` and DELETE the
     nested `VarHandleAtomicHelper`, `VarHandleAtomicHelperMaker`, `UnsafeAtomicHelper` classes +
     the MethodHandles/VarHandle/sun.misc.Unsafe imports. TeaVM DOES ship
     `TAtomicReferenceFieldUpdater` (+ reflection-based variant) and `TAtomicIntegerFieldUpdater`
     (confirmed in classlib 0.15 jar — this is also why the committed ARCBB shadow is safe), so the
     ARFU path should link; keep `new SynchronizedHelper()` only as a defensive fallback.
  2. **netty `AdaptivePoolingAllocator$Chunk`** (enclosing file 1964 lines): same lookup()/VarHandle
     refcount idiom → shadow clinit to AtomicIntegerFieldUpdater (like ARCBB). BIG file to copy.
  3. Post-link5 recheck: `VarHandleFactory`(1) and `PlatformDependent`(1 publicLookup) leftovers —
     may vanish once the above land or need a small PD shadow/patch.
- **NON-invoke remainder (62, the mechanical grind — categories A/B/C/D/F below)**: nio.file(11) +
  nio.channels(14) + net(7) + concurrent(9) + lang(7 incl Thread 3) + misc. All new T-class stubs
  or extract+add methods — NO polymorphic issues. Regenerate the exact list from the latest link:
  `grep "was not found" /tmp/link5.log | sed -E 's/ was not found.*//' | sort -u`.
- **Shadow pattern proven for the invoke gate** (reuse for batch-3): `cp` the netty/guava source
  into `teavm/src/main/java/<pkg>/`, then a small python `str.replace` script to swap the clinit /
  fast-path methods (see `/tmp/pd0_surgery.py`, `/tmp/arcbb_surgery.py`). Our class wins the task
  classpath. Keep everything else verbatim so the full method surface still links.

### WAVE 3 REMAINING (categorized, ~77 refs after the pending fixes land) — pick up here
(Session 4 above supersedes item E — the netty cleaner/PD0 shadows are DONE. A/B/C/D/F still apply.)
A. **Method-add to existing files** (exact descriptors in /tmp/teavm-link3.log):
   - ✅ DONE this session: TLocale `getExtensionKeys()/getISO3Country()`, TThreadInfo `getThreadName()`,
     TAtomicLongArray `lazySet(I,J)` (was a **descriptor bug**: existing sig took int, long-array needs long),
     TAtomicReferenceArray `compareAndExchange(I,Object,Object)`
   - ⬜ FIDDLY (need new nested/enum types TeaVM lacks — verified MISS): TThread `getState()Thread$State`
     (create TThread$State enum), `<init>(ThreadGroup,Runnable,String)`, `setContextClassLoader(ClassLoader)`;
     TSpliterators `iterator(Spliterator$OfInt)PrimitiveIterator$OfInt` / `iterator(Spliterator)Iterator` /
     `spliterator([DIII)Spliterator$OfDouble` (+nested `AbstractSpliterator`, `Spliterator$OfDouble`,
     `PrimitiveIterator$OfInt` all MISS)
   - ⬜ overrides needing read+add: THexFormat `withPrefix(String)`, TExecutors `newScheduledThreadPool(I,ThreadFactory)`
B. **Extract-and-add** (TeaVM HAS class → copy source jar → add): TMethodHandles `lookup()/publicLookup()`,
   TBase64 `getMimeDecoder()/getMimeEncoder(I,[B)`, TConcurrentHashMap `newKeySet()`
C. **New minimal stub classes** (resolve Class-not-found; methods surface next wave):
   - java.net: Socket, ServerSocket, SocketAddress (make TInetSocketAddress EXTEND it), DatagramSocket,
     DatagramPacket, Inet6Address (extends TInetAddress), NetworkInterface
   - java.nio.channels: Channels, Selector, FileLock, spi.SelectorProvider, UnsupportedAddressTypeException
   - java.nio.file: FileStore, ProviderMismatchException; attribute: DosFileAttributeView,
     PosixFileAttributes, PosixFilePermission (enum)
   - j.u.concurrent: CountDownLatch, Semaphore, locks.StampedLock, atomic.LongAdder,
     AbstractExecutorService, RejectedExecutionException
   - misc: java.util.StringJoiner, java.lang.invoke.VarHandle, java.lang.Process/ProcessBuilder,
     java.lang.ExceptionInInitializerError/UnsatisfiedLinkError, java.io.FileDescriptor,
     java.security.KeyPairGenerator/PublicKey
D. **FileChannel + Files expansion** (MC region-file I/O via LevelStorageSource): TFileChannel needs
   open/read/write/close/force/size/truncate/tryLock/map; Files needs getFileStore/getPosixFilePermissions/
   newByteChannel/setLastModifiedTime/setPosixFilePermissions; FileSystem.getPathMatcher. Back with the
   TeaVM in-memory VFS or throw (Stage 3 asset/save work decides).
E. **netty MethodHandle/Cleaner cluster — THE CRUX (~14 refs, gate to green link). SCOPED, tractable:**
   `MethodHandle.invokeExact` is signature-polymorphic → CANNOT stub on MethodHandle. Fix = **shadow the
   leaf netty classes that call it** in `teavm/src/main` (real names, our classes win the classpath).
   Exact call-site inventory (javap of netty-common 4.2.7 `io/netty/util/internal`):
   - `Cleaner` = interface `{ CleanableDirectBuffer allocate(int); void freeDirectBuffer(ByteBuffer); }`
   - invokeExact counts: `CleanerJava6`(2), `CleanerJava9`(1), `CleanerJava24Linker`(4), `CleanerJava25`(1),
     `VarHandleFactory`(14), `PlatformDependent0$8/$9`(2 — lambdas INSIDE PlatformDependent0)
   - `PlatformDependent.<clinit>` statically `new`s all 4 cleaners (isSupported-gated at runtime, but ALL
     linked) → all 4 must be shadowed. Shadow each: implement `Cleaner`, `isSupported()`→(one true rest
     false or all false), `allocate()`→**throw** (Stage-D; direct buffers aren't allocated at the title
     screen so it's never called), `freeDirectBuffer`→no-op, NO MethodHandle/Unsafe. Also need
     `CleanableDirectBuffer` shadow (+ `CleanerJava25$CleanableDirectBufferImpl` is one invokeExact return).
   - `VarHandleFactory` → shadow to unsupported (kills 14). Its 14 invokeExact are the bulk.
   - `PlatformDependent0$8/$9`: the 2 remaining are lambdas in PlatformDependent0 → either shadow the
     specific PD0 method(s) holding them, or shadow PD0 (89 UNSAFE refs — but sun.misc.Unsafe unsafeStub
     already resolves those; only the 2 invokeExact are the blocker). Check which PD0 methods host $8/$9.
   - `MethodHandles.lookup()/publicLookup()` + `MethodHandle.bindTo` → add to TMethodHandles (extract);
     harmless once nothing chains invokeExact off them.
F. **misc method refs**: BufferedReader.transferTo, HttpURLConnection.getContentLengthLong,
   TimeUnit.convert(Duration), Array.newInstance(Class,[I), MethodHandle.bindTo.
- **Possible wave 4**: jdk.jfr annotations (Name/Label/Category) if netty events carry them;
  FileChannel/socket instance methods once their classes resolve.

## Milestone 1 ✅ (2026-07-03, commit 929d0022)
`:teavm:generateJavaScript` compiled the DATA layer, node smoke test printed
`EaglerBoot: 1166 blocks, 1505 items registered`. See git history + the
"Hard-won TeaVM facts" below (still all valid).

---

# THE STRATEGY (Stage B/C) — read this first

Instead of rewriting MC's rendering onto a new abstraction, we **shadow
`org.lwjgl.*` in `teavm/src/main`** and back it with WebGL2 / canvas / a fake
native heap. TeaVM's task classpath puts `teavm/src/main` output BEFORE the
LWJGL jars (see `teavm/build.gradle.kts` `TeaVMTask.classpath.setFrom`), so our
shims win at link time **for the browser build only**. Desktop `runClient`
never sees `teavm/src/main`, so it keeps real LWJGL. Precompiled MC bytecode
calls specific methods → **every shim method must match the exact LWJGL
descriptor** or the TeaVM linker says "method not found".

Authoritative requirement lists live in `teavm/lwjgl-refs/` (generated from MC
bytecode — see "regenerate" below). Each line is `owner.name:descriptor`
(J=long I=int Z=bool D=double F=float V=void B=byte `[I`=int[]). These are the
exact method sets each shim must satisfy. `lwjgl-refs.txt` = all; split per
package into `gl-refs.txt glfw-refs.txt stb-refs.txt al-refs.txt ft-refs.txt tinyfd-refs.txt`.

To regenerate (if lists lost): build classes (`./gradlew :compileJava`), then
javap-scan — batched, NOT one JVM per class:
```bash
JP=~/.sdkman/candidates/java/21.0.11-tem/bin/javap
CLS=build/classes/java/main
cd $CLS && find com/mojang/blaze3d net/minecraft/client -name '*.class' \
  | sed 's|\.class$||; s|/|.|g' | xargs $JP -p -c -classpath $CLS 2>/dev/null \
  | grep -oE '// (Method|InterfaceMethod|Field) org/lwjgl/[^ ]+' \
  | sed -E 's#// (Method|InterfaceMethod|Field) ##' | sort -u
```
Real LWJGL classes are unzipped at `/tmp/gl-cls` (re-unzip the lwjgl jars if
gone) for `javap` descriptor checks.

### KEY INSIGHT: constants are already inlined
javac inlines every `static final` primitive (all `GL_*`, `GLFW_*` ints) into
MC bytecode as literals → **you do NOT define GL/GLFW int constants**. Only
methods and non-final fields remain (e.g. `GLCapabilities.GL_KHR_debug` is a
non-final boolean field → must exist).

---

# SHIM LAYER INVENTORY (all committed through d48b9f9f — `:teavm:compileJava` green)

## Core memory layer ✅ (hand-written, in `teavm/src/main/java/`)
- `net/lax1dude/eaglercraft/v1_21/platform/webgl/NativeHeap.java` — fixed 256MB
  `byte[]` arena, first-fit alloc+coalesce; `view(addr,len)` → zero-copy
  `Int8Array` over the heap for WebGL uploads. Address 0 = NULL.
- `.../webgl/GLObjectMap.java` — int-name ↔ `WebGLObject` registry (1-based, free list).
- `.../webgl/GLContext.java` — holds the live `WebGL2RenderingContext`
  (`GLContext.gl()`, `GLContext.CURRENT`) + registries: buffers, textures,
  framebuffers, renderbuffers, programs, shaders, vertexArrays, samplers,
  queries, syncs, uniforms.
- `org/lwjgl/system/MemoryUtil.java` (+ nested `MemoryAllocator`), `MemoryStack.java`,
  `Pointer.java`; `org/lwjgl/PointerBuffer.java`, `BufferUtils.java`, `Version.java`
  — heap-backed, identity buffer→address registry, exact descriptors.
- `com/mojang/blaze3d/platform/DebugMemoryUntracker.java` — **shadow** of the MC
  class (kills its reflection/MethodHandle allocator probe). No-op.

## GLFW shim ✅ (subagent, `teavm/src/main/java/org/lwjgl/glfw/`, 34 files)
GLFW + all callback classes/interfaces + GLFWVidMode + GLFWImage + Callbacks.
`glfwCreateWindow` grabs canvas `#gameCanvas`, gets a `webgl2` context, sets
`GLContext.CURRENT`, returns fake handle 1L. Input callbacks are stored (static
fields) for Stage D wiring. I added `glfwRawMouseMotionSupported()` +
`GLFW_RAW_MOUSE_MOTION` (InputConstants now calls them directly).
**VERIFY**: skim GLFW.java for TODO/throw stubs; confirm every `glfw-refs.txt`
descriptor exists.

## OpenGL shim ✅ DONE (`teavm/src/main/java/org/lwjgl/opengl/`)
GL11/11C/13/14/15/20/20C/30/31/32/32C/33C + GLShim (session 1), plus (session 2):
`GL` (createCapabilities/getCapabilities singleton), `GLCapabilities` (7 public
`final boolean` caps ALL false → MC core/compat path), `KHRDebug`, `EXTDebugLabel`,
`ARBBufferStorage`, `ARBDirectStateAccess`, `ARBVertexAttribBinding`,
`ARBTimerQuery`, `ARBDebugOutput`, `GLDebugMessageCallback(+I)`,
`GLDebugMessageARBCallback(+I)`. ARB/KHR/EXT bodies are inert (no-op / return
0/null) — reachable-but-dead because caps=false; descriptors match gl-refs.txt.
Debug callbacks implement `org.lwjgl.system.Pointer` (so `GLX.make(T, untrack)`
type-checks). Every gl-refs.txt descriptor now satisfied (0 gl not-found).

## Stub family ✅ DONE (`teavm/src/main/java/org/lwjgl/`)
- `stb/STBImage.java` — **real pure-Java PNG→RGBA decoder**: IHDR/PLTE/tRNS/IDAT,
  Inflater, all 5 unfilters (Paeth incl.), gray/rgb/palette/gray+alpha/rgba,
  honors `desired` channels (luminance weights for downchannel). 8-bit
  non-interlaced only (fails w/ reason otherwise). Result via `MemoryUtil.memAlloc`.
- `stb/STBImageResize.java` — bilinear `nstbir_resize_uint8` over heap bytes.
- `stb/STBImageWrite.java` (returns 0) + `stb/STBIWriteCallback.java` (abstract;
  `getData` must exist — javac emits it on the MC subclass owner, not in refs).
- `openal/*` — AL10/ALC10/ALC11/AL/ALC/ALUtil silent no-op; caps
  `OpenALC11`/`AL_EXT_source_distance_model`/`AL_EXT_LINEAR_DISTANCE` = **true**
  (Library.init throws otherwise), `ALC_SOFT_HRTF` false; alcOpenDevice→1L.
- `util/freetype/*` — FT_Init→0 (writes fake lib ptr) so font system boots,
  FT_New_Memory_Face→1 (error) so MC skips TrueType → bitmap/unihex fonts. Glyph
  structs (FT_Face/GlyphSlot/Bitmap/Vector/Matrix) link-only.
- `util/tinyfd/TinyFileDialogs.java` — no-op.

## Client entry ✅
- `teavm/src/main/java/net/lax1dude/eaglercraft/v1_21/boot/EaglerClientMain.java`
  — sets slf4j cacheOutputStream, calls `net.minecraft.client.main.Main.main`
  with synthetic args (`/game`, assetIndex 29, offline user). Pulls the whole
  client into the reachable set.
- `teavm/build.gradle.kts` `mainClass` = EaglerClientMain (was EaglerBootMain).

## Desktop-safe source patch ✅
- `src/game/java/com/mojang/blaze3d/platform/InputConstants.java` — replaced the
  MethodHandle raw-mouse-motion probe with a direct
  `GLFW.glfwRawMouseMotionSupported()` call. **Desktop `:compileJava` reverified
  green** (real LWJGL 3.3.3 has both). Unused MethodHandle imports remain (harmless).

## Test harnesses ✅ (`teavm/run/`)
- `index.html` — `<canvas id=gameCanvas>` + loads classes.js + `main([])`.
- `node-boot.js` — mocks navigator/document/canvas + a Proxy WebGL2 (all no-op)
  to drive boot past window/GL init and surface non-render runtime errors fast
  in node. Real pixels need chromium (none installed; `npm i puppeteer` to get
  swiftshader WebGL2).

---

# NEXT STEPS (in order)
1. **Continue the wave-3 link loop** (Stage C). Wave 1+2 DONE; wave 3 in progress
   (~77 unique refs left — full categorized list A–F is in the **Session 3 →
   WAVE 3 REMAINING** section above; the **netty cluster E is the gate to a green
   link**, do it first or in parallel). Loop:
   ```bash
   ./gradlew :teavm:compileClasslibOverridesJava :teavm:compileUnsafeStubJava :teavm:compileJava   # 7s pre-check, ALWAYS run first
   ./gradlew :teavm:generateJavaScript > /tmp/link.log 2>&1   # ~19 min now (deep analysis)
   grep -E "was not found" /tmp/link.log | sed -E 's/ was not found.*//' | sort -u
   ```
   Resolution rules (all proven this port):
   - classlibOverrides **fully replace** the vanilla class (patchedClasslib Jar,
     EXCLUDE, overrides first). To add a method to a class TeaVM already ships,
     extract its source from `teavm-classlib-0.15.0-sources.jar` into
     classlibOverrides and add the method (don't half-stub — you'd drop the rest).
     Check ship-vs-missing: `unzip -l <classlib.jar> | grep org/teavm/classlib/java/<pkg>/T<Name>.class`.
   - `java.*`/`javax.xml` map to `T*`; everything else (`jdk.*`, `javax.sound`,
     `javax.management`, netty, guava) falls back to the plain classpath → stub under
     REAL names in `teavm/src/unsafeStub` (`--release 8`) or shadow in `teavm/src/main`.
   - hostile/desktop-only MC calls → shadow the class in `teavm/src/main` (our output
     wins the task classpath) or patch `src/game` desktop-safe (keep `:compileJava` green).
   - **Watch for descriptor bugs** in our own shims: a wrong param/return type compiles
     fine but stays "not found" (this session found `MemoryStack.getPointer` long→int and
     `AtomicLongArray.lazySet` int→long). Match the exact `owner.name:descriptor` from the log.
2. Run `node teavm/run/node-boot.js` → fix runtime `<clinit>` errors until it
   reaches the render loop. Then chromium (puppeteer) for actual title-screen pixels.
3. **Assets pipeline** (needed before anything renders): MC needs vanilla
   resources (shaders, font, GUI textures, lang) via its pack system. Plan:
   gradle task zips `src/game/resources` + downloaded assets → fetch at runtime
   over HTTP → unzip into TeaVM in-memory VFS (`org.teavm...InMemoryVirtualFileSystem`
   is the default FS) → point VanillaPackResources at it. Deferred intentionally
   until the compile is green and runtime shows the resource errors.
4. Stage D: wire stored GLFW input callbacks to real canvas DOM events
   (keydown/mousemove/click/wheel); drive the game loop via requestAnimationFrame
   (glfwSwapBuffers currently no-op — MC's blocking loop must yield to rAF).

---

# Hard-won TeaVM facts (from milestone 1 — still all true)
1. **Task classpath ordering** — TeaVM plugin does NOT prefer project classes
   over dep jars; `teavm/build.gradle.kts` fixes it explicitly (project output →
   patchedClasslib → `configurations["teavmClasspath"]` (MUST stay — loads
   MetadataGenerators) → runtimeClasspath). This is what makes shadowing work.
2. **Classlib mapping scope** (`ClasslibSubstitutionPolicy`): only `java.*`
   (dontFallback) and `javax.xml` map to `org.teavm.classlib.T*`; `java.time`→
   threeten-bp. Everything else falls back to the plain classpath → stub such
   classes under REAL names in `teavm/src/unsafeStub` (compiled `--release 8`,
   dodges JPMS split-package). Done for `javax.security.cert.*`.
3. **clinit erase**: `$callClinit` is erased BEFORE the body runs; an exception
   mid-clinit leaves the class half-initialized with silent nulls (no
   NoClassDefFoundError). Debug by sed-ing console.log into suspect `_clinit_`
   functions in classes.js and running under node.
4. **guava `MapMaker().weakValues()` returns null from computeIfAbsent on TeaVM**
   — fixed `ResourceKey.VALUES`→ConcurrentHashMap. Suspect `CacheBuilder` uses
   (SkinManager etc.) if later runtime nulls appear.
5. **slf4j-simple + `Bootstrap.wrapStreams()` = infinite recursion** unless
   `org.slf4j.simpleLogger.cacheOutputStream=true` set before first log line
   (set in EaglerClientMain AND runClient jvmArgs).
6. TeaVM 0.15 JS output is UMD; `main` is exported, NOT auto-run.
7. TeaVM has: IdentityHashMap, java.util.zip.Inflater/Deflater/GZIP, nio
   ByteBuffer (allocateDirect/order/asIntBuffer), WebGL2/canvas/typedarrays JSO
   (`org.teavm.jso.webgl.WebGL2RenderingContext`, `org.teavm.jso.dom.html.*`).
   `byte[]`→Int8Array via `@JSBody(params="a",script="return a.data;")`.

# Quick commands
```bash
# TeaVM compile (Stage B/C)
./gradlew :teavm:generateJavaScript 2>&1 | grep "was not found" | sort -u
# node boot (mock WebGL, fast triage)
node teavm/run/node-boot.js teavm/build/generated/teavm/js/classes.js
# desktop regression
./gradlew runClient
```

---

# link17 — INPUT wiring + two runtime NPE root-causes (2026-07-13)

link16 verified: shader ES300 translation + Uint8Array texture uploads WORK
(zero "invalid version directive", zero "not Uint8Array"; WebGL2 rendering,
ReadPixels active). Screenshot still black — game crashed during init. Two
null crashes diagnosed and fixed for link17, plus Stage-D input staged:

1. **`$read18` (texture load NPE)** — `TChannels.newChannel(InputStream)` was a
   `return null` stub. MC `TextureUtil.readResource(InputStream)` does
   `Channels.newChannel(in)` then `channel.read(buf)` → NPE on EVERY texture
   (mojangstudios.png first). Implemented real stream<->channel adapters
   (InputStreamChannel/OutputStreamChannel) in TChannels.java.

2. **`$next2` (FATAL, resource reload)** — `TFiles.findRec` (backs `Files.find`,
   used by `PathPackResources.listResources` during "Reloading ResourceManager:
   vanilla") set `result = ... : null` when the path itself didn't match the
   matcher, then `Stream.concat(result, ...)` with result==null → NPE. This fires
   walking any pack ROOT dir (matcher isRegularFile is false for the dir). Fixed:
   `Stream.empty()` instead of null; dropped the redundant double-concat.
   Diagnosed via the `$next2`+`$close` pairing = try-with-resources over a null
   stream. NOTE: TeaVM async coroutine model collapses err.stack (no Java frames)
   — hooking `$rt_createException("(JavaScript) "+err)` to log err.stack gave only
   the message. Locator that worked: grep the readable JS around the mangled call
   site for the enclosing `cmbp_/cmsr_` function name, then reason from the pair.

3. **Stage D input** — GLFW DOM input wired (was: callbacks stored, never fired;
   pollEvents empty; getKey always RELEASE). New: KeyCodes.java (browser
   KeyboardEvent.code -> GLFW key int), event queue drained synchronously in
   glfwPollEvents, DOM listeners attached in glfwCreateWindow, pointer-lock via
   glfwSetInputMode, glfwGetKey state tracking. Not needed to SEE the title
   screen; batched into link17 to save a cycle.

Still-nonfatal (deferred): `.mcassetsroot does not exist in classpath` (built-in
VanillaPackResources classpath root, harmless — VFS pack via pushUniversalPath),
`/game/assets/indexes/29.json` (online asset index), authlib/realms
`URL.openConnection(Proxy) not supported` (network, caught).

Strategy shift: BATCH many source fixes per link (link = 20min serial, 11GB heap,
one at a time). compileJava (~2s) validates before committing to generateJavaScript.
Auto-test chained AFTER generateJavaScript in the same bg job (link16 lesson:
polling for classes.js caught it mid-write at size 0 -> "main is not defined").

---

# link18 — the input reachability explosion + Realms/JDK stubs (2026-07-13)

link17 FAILED (generateJavaScript, 27min, exit 1). NOT a code bug — wiring input
made a huge swath of GUI reachable (dispatching keyCallback pulls every
Screen.keyPressed override -> button onPress lambdas -> secondary screens:
PackSelectionScreen, ServerSelectionList, Realms upload/download, LanServerDetection).
TeaVM then fails on ~20 REACHABLE-but-missing JDK APIs that link16 had dead-code
-eliminated. KEY LESSON: TeaVM only errors on REACHABLE missing refs; enabling a
feature (input) can surface a wave of them. The trace after each "X was not found"
is the REACHABILITY path (often via CompletableFuture/Executor.execute), not a real
call stack.

Fix strategy — decision rule: STUB the JDK type if the stub is small & self-contained;
GUT the MC feature (it's dead in-browser anyway) if the stub would CASCADE.
- java.net.http.* (HttpClient/Request/Response/Builders/BodyPublishers/BodyHandlers)
  CASCADES (HttpHeaders/Flow/generics) -> GUTTED FileUpload.java + FileDownload.java
  (Realms upload/download; removed HttpClient field + all HTTP method bodies, kept
  public API throwing/empty; imports are compile-only so TeaVM never sees them).
- FileOutputStream.getChannel comes from commons-compress TarArchiveOutputStream, used
  only by RealmsUploadWorldPacker -> GUTTED its pack() to throw (kills getChannel).
- Small self-contained STUBS (classlibOverrides): TMulticastSocket(extends TDatagramSocket),
  TWatchService/TWatchKey/TWatchEvent, TBasicFileAttributeView, TDosFileAttributes,
  TThreadPoolExecutor(+full ExecutorService throwing impls — a concrete subclass of
  TAbstractExecutorService must implement ALL interface methods, caught by the gate),
  TScheduledThreadPoolExecutor(int,ThreadFactory). METHOD ADDS: TDatagramPacket(byte[],int)
  +getData/getOffset/getLength/getAddress, TSocket.connect(TSocketAddress,int),
  TRuntime.exec(TString[]), TFileSystem.newWatchService(), TPosixFileAttributes.lastModifiedTime(),
  TFiles.getAttribute/getOwner.

GATE LESSON: classlibOverrides is a SEPARATE source set — validate with
`./gradlew :teavm:compileClasslibOverridesJava` (NOT :teavm:compileJava, which only
covers teavm/src/main). It caught the TThreadPoolExecutor interface gap in 4s, saving
a 20-min link. Also gate `:compileJava` for src/game guts.

link18 (generateJavaScript + auto-test) running with all of the above. If a SECOND wave
of not-founds appears (newly-linkable Realms/GUI screens referencing more missing APIs),
batch-fix the same way. The runtime NPE fixes from link17-that-never-linked (real
Channels for $read18, TFiles.findRec null->empty for $next2) are also in this build.

# link19/20 — reachability waves 2 & 3 (2026-07-13)
Wave 2 (link18 fail, 8 syms): WatchService family + Process.getErrorStream. GUTTED
PackSelectionScreen$Watcher (create()->null, bodies gutted, kept `throws IOException`
so callers' catch stays legal — gate caught that). Empty TWatch* interfaces still
satisfy linkfs's signature-only refs. Added TProcess.getErrorStream.
Wave 3 (link19 fail, 1 sym): Process.getOutputStream — sibling of getErrorStream in
Util.OS.openUri (uses getInputStream/getErrorStream/getOutputStream, all 3). Added it.
LESSON: TeaVM reports ALL reachable-missing in ONE pass (exhaustive per link), BUT a
one-method miss = a full 26-min relink. When stubbing a JDK method, add the WHOLE
sibling family at once (all 3 Process streams, all Builder methods, etc.).
link20 running with getOutputStream. Reachable set otherwise fully resolved per link19.

# link21/22 — runtime long tail: iterator + memRealloc (2026-07-13)
link20/21 GREEN. Now clearing init-sequence runtime crashes (each = one 26-min link).
- link20 crash $next2 -> already fixed (findRec). link21 crash $hasNext: TDefaultPath.iterator()
  returned null (TeaVM stub) -> Guava Joiner.join(path) NPE in PathPackResources.listPath.
  FIX: copied TDefaultPath into classlibOverrides (same impl package, package-private access OK),
  implemented iterator() over getNameCount()/getName(int). Got MUCH further (3537 log lines,
  atlas building).
- link21 crash: CompletionException -> UncheckedIOException -> IOException "Bad PNG Signature"
  (uncaught, async atlas load). ROOT CAUSE (real bug, not a stub): MemoryUtil.memRealloc
  returned a fresh ByteBuffer at position 0. TextureUtil.readResource grows the buffer mid-fill
  (channel.read; when full -> memRealloc(cap*2); keeps reading into the RETURNED buffer). A
  position-0 buffer makes the next read OVERWRITE the PNG signature at index 0 -> validateHeader
  (PngInfo, NativeImage:158) fails. Only images bigger than the initial 8192 buffer are affected
  (small sprites fit -> no realloc -> fine; mojangstudios logo + big atlas textures fail). FIX:
  memRealloc now restores position (limit stays newSize for continued filling). link22 running.

DEBUG WIN: the err.stack injection (hook `$rt_createException("(JavaScript) "+err)` to
console.log err.stack) DID yield full JS frames for $hasNext (V8 captured them) — pinpointed
Joiner->listPath->TDefaultPath instantly. Use it first for JS TypeErrors. (Java IOExceptions
like "Bad PNG Signature" have no frames — locate those by grepping the thrower in src/game.)

NON-FATAL deferred: "unsupported bit depth 2/4 (8 only)" — eagler STBImage PNG decoder only does
8-bit; low-bit palette PNGs (armor trims) show as missing textures. Cosmetic, not on the title
screen's critical path (logo/panorama/buttons are 8-bit). Fix the STBImage shim later.

# link22/23 — shaders compile, font decoder (2026-07-13)
link22 memRealloc fix landed. Next fatal was GLSL: MC core shaders are desktop GLSL 330;
WebGL2 needs ES 3.00 (no implicit int<->float, no isamplerBuffer). Fixed ~10 core shaders:
- int-vector OP float -> wrap in vec2()/vec3() (block/terrain lightmap, terrain.vsh ChunkPosition,
  item_entity/crumbling/entity_alpha texCoord2=vec2(UV2))
- float OP int-literal -> literal .0 (lightmap.fsh *16.0/15.0, clouds.fsh linear_fog_value(...,0.0,...))
- int uniform as textureLod LOD -> float(MipMapLevel) (animate_sprite blit/interpolate)
- float(gl_VertexID>>3) (animate_sprite.vsh)
- rendertype_clouds.vsh isamplerBuffer -> stubbed (WebGL2 has no buffer samplers; clouds compile,
  render empty). "Couldn't find source for X" errors after "Stopping!" are teardown cascade, ignore.

*** HUGE WORKFLOW WIN: shaders/resources live in teavm/run/vanilla-assets.zip, NOT classes.js. ***
A shader/asset-only change does NOT need the 26-min generateJavaScript relink. Just rebuild the zip
and re-run chromium: `python scratchpad/buildzip.py` (clean full rebuild from src/game/resources,
17865 files, ~6s) then `node teavm/run/chromium-run.js "" 60`. Iterated ALL shader fixes in ~1min
loops this way. ONLY Java changes (teavm/src/**) need the relink.

After shaders compiled: 0 shader errors, OpenAL sound engine started, ALL texture atlases built
(blocks/gui/items/particles...). New FATAL: "Default font failed to load" = the bit-depth-1 font PNG
the STBImage decoder rejected (was 8-bit only). FIXED STBImage.decode to unpack bit depths 1/2/4
(grayscale scaled to 0..255, palette kept as index; packed MSB-first, filter bpp=1). This ALSO fixes
the armor-trim (2/4-bit) "missing texture" spam. Java change -> link23 running (26-min relink).
next crash after this is likely at/near the title screen render itself.

err.stack injection note: it DID capture full JS frames for $hasNext this session (V8 gave them);
it's still in the current classes.js from debugging but a relink regenerates clean. Injection form
that works: single-line console.log, no embedded \n (the \n form breaks classes.js -> "main not defined").

# link24/25 — texture upload pixelStore + ASSERTIONS OFF (2026-07-13)
link23 font decoder worked. Next fatal: WebGL "texSubImage2D: ArrayBufferView not big enough"
then AssertionError. FIX 1 (link24): GL11 now tracks UNPACK_ROW_LENGTH/SKIP_ROWS/SKIP_PIXELS/
ALIGNMENT (GlCommandEncoder sets them to upload atlas sub-rects from a larger buffer) and sizes
the texSubImage2D heap view as WebGL2 reads it (rowStride*(skipRows+height-1)+(skipPixels+width)*bpp).
texSubImage errors -> 0.

But the AssertionError (RuntimeException caused by AssertionError, NO message) SURVIVED link24 —
separate root cause. *** SYSTEMIC FIX: TeaVM's TClass.desiredAssertionStatus() returned TRUE, so
ALL Java `assert` statements are compiled live and FIRE in the browser. Desktop MC runs WITHOUT -ea
(assertions off). So MC asserts crash us but never run on desktop. *** Changed TClass override to
return false (link25). This matches desktop AND preempts every future spurious assert.
Confirmed mechanism: classlib default desiredAssertionStatus()=true; Java `assert` = `if(!$assertionsDisabled && !cond) throw new AssertionError()`.

DEBUG NOTE: AssertionError ctor hooks in classes.js did NOT fire (TeaVM inlines `new AssertionError()`
and native-throws Java exceptions — no $rt_throw chokepoint). Could not get its JS stack. Located it
instead by: (a) desktop-vs-browser behavior reasoning (assertions default), (b) confirming
desiredAssertionStatus()=true in classlib. For future no-message AssertionErrors, assertions are now
off so they shouldn't recur.

Progress ladder this session: link-green -> reload -> atlas build -> shaders(ES300) -> sound -> font
(bitdepth 1/2/4 decoder) -> texture upload(pixelStore) -> assertions-off -> bidi-bypass.

## link25 result (assertions-off GREEN, new fatal = ICU Bidi)
AssertionError GONE (0 hits). Reached title-screen render (1 client tick), then FATAL:
`TypeError: reading '$limit7'` = .limit() on a null Buffer. err.stack empty in V8 for these;
FIX = inject `new Error().stack` at wrapException anchor (line ~709 classes.js) -> gives live JS frames.
Injection (single line, filters msg): logs "PONYINJ ...". Stack showed:
  UBiDiProps.<clinit> <- Bidi.<init> <- ClientLanguage.getVisualOrder <- MultiLineLabel <- MultiLineTextWidget.
Root cause: ICU4J data (ubidi.icu etc) NOT embedded in TeaVM build. TeaVM resources = Base64 blob
baked into classes.js by ClassLoaderNativeGenerator (see TClassLoader.getResourceAsStream); ubidi.icu
was never included -> getResource returns null -> null ByteBuffer -> .limit() NPE.
Two OTHER caught (non-fatal) ICU TypeErrors same root cause, during ITEM MODEL load (chest/trapped_chest):
  'length'   <- ULocale$JDKLocaleHelper.<clinit> (locale data)
  '$ordinal0'<- ZoneMeta$SystemTimeZoneCache / TimeZone.getDefault (tz data)
These are caught per-model (model fails, game continues) -> NOT on title screen -> DEFERRED.
They WILL bite in real gameplay; systemic fix later = embed ICU data OR avoid ICU ULocale/TimeZone.

## link26 (RUNNING): bidi-bypass
FIX (src/game .../resources/language/ClientLanguage.java getVisualOrder): replaced
FormattedBidiReorder.reorder(...) with the base Language default's non-ICU StringDecomposer path
(logical order). Avoids new Bidi()/UCharacter.getMirror/ArabicShaping -> no ICU touch. LTR correct;
RTL falls back to logical order until ICU data ships. Gate :compileJava GREEN. link26 = relink+test.
Next: read scratchpad/link26.log -> if Bidi fatal gone, inspect teavm/run/title-screen.png for pixels.

## link26 result: BIDI GONE + FIRST RENDERED PIXELS (milestone)
Bidi fatal gone (0 hits). title-screen.png = the Mojang Studios red loading screen renders PERFECTLY:
logo + "STUDIOS" text (font pipeline confirmed) + progress bar. Atlas+font+shaders all working E2E.
New fatal right at end of loading phase (fence, before title swap):
`IllegalStateException: Failed to complete GPU fence: 1282` +
`WebGL: INVALID_OPERATION: clientWaitSync: timeout > MAX_CLIENT_WAIT_TIMEOUT_WEBGL`.
Root cause: MC GlFence.awaitCompletion -> GlStateManager._glClientWaitSync(sync,0,timeout). Our GL32
shim did `(int) timeout`. MappableRingBuffer:39 calls awaitCompletion(Long.MAX_VALUE) -> (int)=-1 ->
huge timeout. Chromium's MAX_CLIENT_WAIT_TIMEOUT_WEBGL is 0, so ANY nonzero timeout -> INVALID_OPERATION
-> clientWaitSync returns WAIT_FAILED(37149) -> GlFence throws "Failed to complete GPU fence: "+glGetError(1282).
The 49 "Can't find vertex to swap" IllegalStateExceptions are non-fatal tessellator logs (also in link25).

## link27 (RUNNING): fence-clamp
FIX (teavm/src/main .../opengl/GL32.java glClientWaitSync): pass timeout 0 always (non-blocking poll).
WebGL main thread can't block-wait anyway; RenderSystem:261 caller already polls with 0, MappableRingBuffer
caller ignores the return. Ceiling noted in comment: no true GPU wait -> possible ring-buffer overwrite;
upgrade to bounded poll-loop if artifacts. Gate :teavm:compileJava GREEN. link27 = relink+test.
Next: read scratchpad/link27.log -> if fence gone, the title screen (buttons/panorama) should swap in.

## link27 result: FENCE GONE + INTERACTIVE GUI SCREEN RENDERS
Fence fatal gone (0). Game RUNS: "Client ticks: 30 ticks / 1.5s" (main thread alive, ticking).
title-screen.png (108KB) = a real GUI screen: giant "JAVA EDITION" logo text + working "Continue"
button (font+button+input all live). BUT garbled: logo mis-scaled huge, GUI texture atlas showing as
background (UV/scale bug). New crash is NOT on main thread - it's the Sound engine thread, 600x loop:
`IllegalStateException: Cannot park the only thread` (Description: Uncaught exception on thread: Sound engine).
Root: SoundEngineExecutor spawns "Sound engine" Thread -> run() -> waitForTasks() -> LockSupport.park().
TeaVM is single-threaded -> park on only thread throws -> uncaught handler delayCrash -> 600 reports.
Also 648x `IllegalStateException: Can't find vertex to swap` from FaceBakery.recalculateWinding
(block-model bake). findVertex uses exact float == on matrix-rotated coords; TeaVM float rounding
diverges from HotSpot -> per-face fail -> that block model fails to bake. NON-fatal (caught per model),
NOT on title screen (block models). DEFERRED - needs precision analysis (epsilon compare in findVertex?).

## link28 (RUNNING): sound-inline
FIX (src/game .../sounds/SoundEngineExecutor.java): createThread() now returns Thread.currentThread()
instead of spawning - getRunningThread()==main so BlockableEventLoop.isSameThread() is true -> every
sound task runs INLINE, no bg thread, no park. shutDown() no longer interrupt/join (was main thread).
Gate :compileJava GREEN. link28 = relink+test. Sound crash-loop may also have been starving the render
thread mid-frame -> re-check if title-screen garbling clears. If still garbled: dig GUI blit UV/scale
(giant logo + atlas-as-background points at GuiGraphics.blit or gui shader). Next: read link28.log +
title-screen.png. Remaining known-deferred: FaceBakery precision (in-world), 2 ICU model-load errors.

## link28 result: NO CRASH — interactive onboarding screen renders
Sound crash-loop gone (0 crashes). Game runs clean (no crash report). title-screen.png = the
AccessibilityOnboardingScreen (first-launch): "Welcome to Minecraft" + Narrator/Accessibility/Language/
Continue buttons ALL render correctly (font, buttons, layout, input all working). ONE visual bug remains:
the decorative gui/title/ textures (minecraft.png logo, edition.png, panorama_overlay) render as
block-atlas-like garble over the top ~half. Buttons/text/layout underneath are perfect (confirmed by
skipping gui/title/ blits at runtime -> clean onboarding screen).

## TITLE-TEXTURE GARBLE BUG — deep investigation, NOT yet root-caused (DEFERRED)
Symptom: standalone textures loaded LAZILY at title time (minecraft.png 1024x256 colortype6,
edition.png 512x64 ct6, panorama_overlay 1x1 ct4) render block-atlas-like content. mojangstudios.png
(512x512 colortype3 palette, loaded EARLY on loading screen) renders PERFECTLY via the identical path.
RULED OUT via runtime classes.js injection (all verified correct):
 - Geometry/coords (logged: minecraft x85-341 y30-74, correct small; forcing coords tiny collapses garble
   -> size IS coord-driven, no transform bug).
 - GUI ortho projection (427x240, correct = fbW/guiScale).
 - Matrix3x2f pose (identity), addVertexWith2DPose transform (correct).
 - Pipeline/shader (gui_textured / position_tex_color, SAME as working mojangstudios).
 - Texture identity: unique GL ids (edition=9, panorama=10, minecraft=11); ids 9/10/11 NEVER freed/reused
   (logged GLObjectMap free/reuse) -> no id collision. AbstractTexture.texture == view.texture (consistent).
 - PNG decode: succeeds, no stbi failure logged. Real dims confirmed via IHDR (od -j16).
 - Upload: writeToTexture pixelStore correct (rowLen=width, align=4); my GL11 unpackViewBytes size correct
   (logged viewLen=1048576 for 1024x256, source first bytes =0 = transparent = logo-like, NOT atlas).
   No WebGL INVALID/texSubImage errors.
 - Bind: render pass binds correct texId to unit 0 (logged PONYBIND: minecraft id=11 -> unit0). Disabling
   BOTH _bindTexture cache AND _activeTexture cache (forced always-bind) did NOT fix it.
 - Blocks atlas (2048x2048) is NEVER bound during title render (only title textures + GUI atlas id590
   1024x1024 at unit0).
CONCLUSION (link28, WRONG — superseded below): guessed the texSubImage2D upload "silently does not land."

## link29 result: GARBLE ROOT-CAUSED + FIXED — TITLE/ONBOARDING SCREEN RENDERS PERFECTLY ✅
The link28 "upload doesn't land" hypothesis was DISPROVEN. Did the READBACK the notes called for, but
via **WebGL prototype injection in index.html (NO relink)** instead of source instrumentation: wrapped
`WebGL2RenderingContext.prototype.texSubImage2D`, saved each title texture's WebGLTexture, and read it
back through a temp FBO both at upload time and after the title renders (post-readback wired into
chromium-run.js before the screenshot). RESULT: `READBACK-POST 1024x256` (minecraft.png) and `512x64`
(edition.png) both FRAMEBUFFER_COMPLETE with real non-zero content matching upload time — **the uploads
DO land and persist. The GPU texture content is correct.** (mojangstudios 512x512 reads incomplete
post-render only because it's freed after the loading screen.) So the bug was RENDERING, not upload.
- **ROOT CAUSE: `baseVertex` was ignored.** `teavm/src/main/.../opengl/GL32.java` `glDrawElementsBaseVertex`
  had a `// baseVertex ignored` shortcut (just called `drawElements`). But 1.21.11's deferred GUI renderer
  (`GuiRenderer` + `GlCommandEncoder.drawFromBuffers`) packs MANY meshes into ONE shared vertex buffer and
  draws each with a nonzero `baseVertex` offset. Ignoring it -> every mesh after the first read the FIRST
  mesh's vertices (the full-screen background quad) but with its OWN texture bound -> logo/edition drawn
  full-screen = giant garble; transparent regions showed leftover atlas. Buttons/text render fine because
  they use a DIFFERENT vertex format -> their own vertex buffer -> their own baseVertex=0 first mesh.
  (WebGL2 has NO glDrawElementsBaseVertex; the comment's "baseVertex==0 is MC's common case" was false.)
- **FIX (GL32.java, link29):** emulate baseVertex in the shim. WebGL2 baseVertex == a byte offset of
  `baseVertex*stride` added to every enabled vertex attribute's pointer. Read the currently-bound VAO's
  attribute layout LIVE via `getVertexAttrib`/`getVertexAttribOffset` (enabled/size/type/stride/norm/
  integer/buffer-binding + pointer offset) — no per-VAO tracking needed — re-point each attribute at
  `offset+baseVertex*stride`, draw, then restore. Same for glDrawElementsInstancedBaseVertex. Reusable
  static scratch arrays (cap 32 attribs). ceiling: getVertexAttrib round-trips per draw are slow; cache
  per-format if it ever hot-paths. `:teavm:compileJava` green; link29 = clean relink (PONY injections gone).
- **link29 chromium VERIFIED:** title-screen.png = the AccessibilityOnboardingScreen rendering PERFECTLY —
  "MINECRAFT" logo (correct stone-block texture, correct size), "JAVA EDITION" subtitle, the "Welcome to
  Minecraft!" dialog, and Narrator/Accessibility/Language/Continue buttons WITH their icons. Garble GONE.
- **DEBUG TECHNIQUE (new, reusable):** for texture-content questions, wrap the WebGL call at the
  `WebGL2RenderingContext.prototype` level in index.html (runs before classes.js) + read back via a temp
  FBO — proves GPU state with ZERO relink, unlike source instrumentation.
- Remaining known-deferred (non-fatal, not on title screen): FaceBakery precision "Can't find vertex to
  swap" (in-world block bake, TeaVM float != HotSpot); 2 ICU model-load TypeErrors (ULocale/TimeZone data
  not embedded, per-model caught). Both bite in real gameplay, not the title/menu path.

## link30 VERIFIED ✅: Stage D MOUSE INPUT WORKS — TitleScreen + Options reached by clicking
VERIFY RESULT: clicking Continue at its REAL coords (636,670) -> TitleScreen renders (logo, DAA! splash,
Singleplayer/Multiplayer/Realms/Options/Quit/Language/Accessibility) with NO Realms crash. Second click
(Options...) -> Options screen renders fully, AND a "Telemetry is disabled." hover tooltip shows -> click
activation, multi-level navigation, AND precise hover all confirmed. Both link30 fixes landed. Details:
The GLFW input shim (teavm/src/main/org/lwjgl/glfw/GLFW.java) was ALREADY mostly wired in link29: DOM
listeners (keydown/up, mouse move/down/up, wheel, focus), an event queue drained by glfwPollEvents (called
from RenderSystem.pollEvents ~60/s), key/mouse/scroll translation, callbacks stored. But clicks did nothing.
Traced end-to-end with hot-patched diagnostics on the link29 classes.js (NO relink — POLLHB/DRAIN/ONBUTTON/
SCALEX console.log injections; all reverted by the link30 regen):
 - poll runs ~60/s; cursorPos/mouseButton/key callbacks all non-null. DOM events reach the canvas and
   enqueue+drain (DRAIN k=2 cursorpos, k=3 mbutton). MouseHandler.onButton EXECUTES (win handle matches).
 - **ROOT CAUSE = coordinate space mismatch.** Canvas backing store = MC default window 854x480 (glfwCreate-
   Window sets canvas.width/height to the config size); it's CSS-stretched to the 1280x720 viewport. DOM
   offsetX is in CSS px (0..1280) but MC's getScreenWidth()==framebufferWidth==854. MouseHandler.getScaledXPos
   = xpos * guiScaledWidth(427) / screenWidth(854); with xpos=636 (CSS) -> logical 318, off the right edge of
   every centred button. PROOF (no relink): clicking the *compensated* CSS point (424,446) -> logical
   (212,223) = button centre -> Continue highlighted AND activated -> setScreen(TitleScreen).
 - **FIX 1 (GLFW.java onMove):** scale DOM coords CSS->backing: cursorX = offsetX * (canvas.width/clientWidth),
   same for Y and for pointer-locked movementX/Y. Added @JSBody scaleX/scaleY (canvas.width/clientWidth,
   guarded /0 ->1). Correct at ANY resolution/DPI (not a magic 0.5). ponytail: general, not hardcoded.
 - **Then TitleScreen crashed** "Ticking screen" <- com.mojang.realmsclient RealmsHttpException: URL.open-
   Connection(Proxy) not supported. TitleScreen.tick() -> realmsNotificationsScreen.tick() -> data fetcher ->
   RealmsClient HTTP (single-threaded TeaVM runs the ioPool task inline; RealmsHttpException isn't a
   RealmsServiceException so RealmsAvailability.check()'s catch misses it). RealmsAvailability.check() already
   has an offline short-circuit: `if (Minecraft.getInstance().isOfflineDeveloperMode()) return AUTH_ERROR;`
   (no HTTP) -> validClient=false -> getConfiguration()=null -> no data fetcher -> no crash.
 - **FIX 2 (EaglerClientMain launchArgs):** add `--offlineDeveloperMode` (Main.java accepts it as a Void flag).
   Browser-only (desktop runClient never passes it) -> desktop-safe. Also cleanly routes profileFuture +
   profileKeyPairManager (Minecraft.java lines ~425/656 read the FIELD) down their offline branches, matching
   the already-forced createOffline auth. isOfflineDeveloperMode() (method) is read only by Realms.
 - Gate :teavm:compileJava GREEN. link30 = relink+verify. NEXT after link30: run `CLICKS="636,670" HOVER= node
   teavm/run/chromium-run.js` (real button coords now, no compensation) -> expect TitleScreen with panorama +
   Singleplayer/Multiplayer/Realms/Options/Quit; then test keyboard (type in a text field), then deeper menus.
 - HARNESS: chromium-run.js gained CLICKS="x,y;x,y" (and HOVER=1 to move-only) env to drive input for
   verification. title-screen.png is the post-interaction shot.
 - NOTE (deferred, not input): game renders at 854x480 backing upscaled to 1280 = slightly soft. The coord fix
   is resolution-independent so this is purely cosmetic; add `--width 1280 --height 720` to launchArgs later
   for crisp 1:1 (may shift guiScale 2->3; verify layout).

## link31 (RUNNING): SelectWorldScreen crash = TeaVM Formatter missing %n
Driving past the title screen (Continue -> Singleplayer) crashed: "mouseClicked event handler" ->
new SelectWorldScreen. Root-caused WITHOUT relink via classes.js hot-patch of nm_CrashReport_forThrowable
(dump cause chain: `console.log(new Error().stack ...)` then `Object.keys(ex)` then walk `$cause0.$message2`):
cause = **java.util.UnknownFormatConversionException "Unknown format conversion: n"**. TeaVM 0.15's
TFormatter.formatValue() switch handles b/B/h/H/s/S/c/C/d/D/o/O/x/X/f/% but has NO `case 'n'` (platform
newline) -> any MC String.format with %n throws. SelectWorldScreen (search box / world-list text) uses it.
 - **FIX (classlibOverride):** copied teavm-classlib TFormatter.java into
   teavm/src/classlibOverrides/java/org/teavm/classlib/java/util/TFormatter.java (whole-class override, 665
   lines) + added `case 'n': out.append("\n"); break;`. Gate compileClasslibOverridesJava+compileJava GREEN.
 - STILL MISSING in that switch (add when a crash proves them needed, NOT speculatively): e/E/g/G/a/A
   (scientific/general/hex float) and t/T (date/time). MC world-list last-played date may hit %t later.
 - HARNESS: chromium-run.js inter-click delay bumped 1500->3000ms. NOTE: the first (Continue) click is
   INTERMITTENT in headless — sometimes doesn't register (screen stays on onboarding). Not yet root-caused
   (boot/reload timing? not a confirmed port bug). Retry the run if a click no-ops. Input FIX itself is solid
   (link30 reliably reached TitleScreen+Options).
 - Debug technique reused: dump a Java exception's cause chain from JS by reading `$cause0`/`$message2` fields
   on the TeaVM exception object (constructor.name is empty, no .stack) inside nm_CrashReport_forThrowable.
 - NEXT after link31: re-drive Continue->Singleplayer -> expect SelectWorldScreen (empty world list, "Create
   New World" button) -> then Create New World (CreateWorldScreen) -> world gen -> in-world render. Each step
   likely a new blocker (deferred FaceBakery precision + ICU ULocale/TimeZone will bite in-world).

## link31 VERIFIED + link32 (RUNNING): server pack data/ redirect for registry loading
link31 (%n fix) VERIFIED: SelectWorldScreen no longer crashes on %n. Drove Continue->Singleplayer;
next blocker surfaced during WORLD CREATION registry load:
  net.minecraft.ReportedException: Registry Loading
  java.lang.IllegalStateException: Registry must be non-empty: minecraft:cat_variant (+ chicken_variant,
  cow_variant, frog_variant, painting_variant, pig_variant, wolf_variant, wolf_sound_variant,
  zombie_nautilus_variant) -- all the DATA-DRIVEN registries loaded EMPTY.
 - ROOT CAUSE: these load from data/minecraft/<registry>/*.json via the SERVER-DATA vanilla pack
   (ServerPacksSource), but only the CLIENT pack (ClientPackSource) had been redirected to /eagler-assets.
   ServerPacksSource.createVanillaPackSource() only did .pushJarResources() (empty on TeaVM) -> server pack
   sees no data/ -> registries empty. (The data IS in the VFS: vanilla-assets.zip has 7556 data/minecraft
   entries incl. cat_variant/painting_variant; src/game/resources/data/minecraft/* all present.)
 - FIX (src/game ServerPacksSource.createVanillaPackSource): add
   `.pushUniversalPath(java.nio.file.Paths.get("/eagler-assets"))` after pushJarResources, exactly like the
   ClientPackSource patch. One line covers ALL data-driven registries (variants + worldgen). Gate
   :compileJava GREEN. link32 = relink+verify.
 - NEXT after link32: re-drive to world creation; registries should populate. Then Create New World ->
   world gen -> in-world chunk render (expect FaceBakery "Can't find vertex" precision to matter now, ICU
   ULocale/TimeZone, and likely fresh blockers in worldgen/chunk mesh).

## link32 VERIFIED + link33 (RUNNING): world creation reached; integrated-server key-pair crash fixed
link32 (server pack data/ redirect) VERIFIED: registries load, NO "Registry must be non-empty". Drove
Continue->Singleplayer (auto-forwards to CreateWorldScreen when saves empty) -> **CreateWorldScreen renders
perfectly** (Game/World/More tabs, "New World" text field, Game Mode/Difficulty/Cheats buttons + hover
tooltips, Create/Cancel). Clicked "Create New World" (400,671) -> integrated server STARTS ("Starting
integrated minecraft server") then FATAL crash-loop (400+):
  IllegalStateException: Failed to generate key pair <- CryptException <- TypeError null reading $initialize1
 - ROOT CAUSE: MinecraftServer.initializeKeyPair() -> Crypt.generateKeyPair() does
   KeyPairGenerator.getInstance("RSA").initialize(1024).generateKeyPair(). Our TKeyPairGenerator stub's
   getInstance() returned **null** -> .initialize NPE. getKeyPair() is only used by
   ServerLoginPacketListenerImpl (real network encryption handshake); singleplayer's local/memory connection
   skips it, so a null keyPair is safe.
 - FIX (teavm classlibOverride TKeyPairGenerator.getInstance): return `new TKeyPairGenerator()` instead of
   null -> initialize() no-ops, generateKeyPair() returns null -> Crypt returns null (no throw) ->
   server keyPair=null, never dereferenced in singleplayer. Gate compileClasslibOverridesJava GREEN.
 - ⚠️ COULD NOT hot-patch this to pre-scout the next blocker: TeaVM DEAD-CODE-ELIMINATED the initialize/
   generateKeyPair method bodies (old getInstance always returned null -> methods provably unreachable). The
   relink re-emits them. (Same class of "needs relink" as inlined constant stubs.)
 - link33 = relink+verify. NEXT: re-drive Create New World -> expect world gen (Preparing spawn area) ->
   client receives chunks -> in-world terrain render. Watch for: SecureRandom (world seed), more crypto,
   FaceBakery "Can't find vertex" precision (block models, now on the hot path), ICU ULocale/TimeZone, and
   fresh worldgen/chunk-mesh blockers. The 1 non-fatal `null reading '$chunkSource'` seen already may become
   relevant once the server tick survives.

## link33 VERIFIED + link34 (RUNNING): world gen crash = reflective multi-dim array stub
link33 (key-pair fix) VERIFIED: "Failed to generate key pair" GONE; integrated server starts and enters
world gen. Next FATAL (server tick loop, 361x):
  java.lang.UnsupportedOperationException: multi-dim (Stage D)
  (+ secondary "Error: -5" during the post-crash "Saving chunks" shutdown -- ignore, it's cleanup after
   the primary crash.)
 - ROOT CAUSE: teavm classlibOverride TArray.newInstance(Class, int[] dimensions) was a Stage-D stub that
   threw for dimensions.length > 1. World gen (noise/DFU/density-function) does reflective multi-dim array
   creation -> crash.
 - FIX (TArray.java): implemented newMultiArray() recursively -- reuses the existing single-dim
   newInstance(Class,int) + set(): outer levels are arrays-of-arrays whose element class comes from a
   recursively-built sub-array (first.getClass()). Handles dims.length 0 (IAE) / 1 (leaf) / N. Gate
   compileClasslibOverridesJava GREEN. link34 = relink+verify.
 - ⚠️ RELINK HYGIENE (cost a cycle): link33's relink first FAILED because earlier stacked relink attempts
   (a foreground run I killed via tool-timeout + retries) ORPHANED gradle daemons + a 2GB TeaVM worker JVM
   that thrashed the 12G heap ("2 busy Daemons could not be reused"). FIX: `./gradlew --stop` + PowerShell
   Stop-Process the big leftover java.exe/javaw.exe (the worker's cmdline ends in a *.json), LEAVE the small
   lsp-*/*server language-server JVMs. Then ONE clean background relink succeeded. RULE: one relink at a
   time, always run_in_background (never foreground+timeout -> orphans the worker).
 - NEXT after link34: re-drive Create New World -> world gen should proceed past multi-dim. Watch for: more
   Stage-D stubs on the chunk-gen/executor path (TThreadPoolExecutor "no threads", TFileChannel/TFiles VFS
   for chunk save), then client chunk-mesh render + FaceBakery "Can't find vertex" precision (now hot path).

## link34 VERIFIED + link35 (RUNNING): world gen STALL at 16% = ioPool not inline
link34 (multi-dim array) VERIFIED: no more multi-dim crash. World gen PROGRESSES -> "Loading terrain..."
chunk-map screen renders, server logs "Preparing spawn area: 16%". BUT then STALLS (deadlock, JS event loop
frozen -> puppeteer screenshot never taken). Not slow -- stuck at 16% through a 75s wait, no crash.
 - THREADING MODEL: TeaVM here uses cooperative GREEN THREADS (async transform; Platform.startThread), so
   MC's "Server thread" runs and yields on block. PlatformExecutors (teavm/src/main) already returns a
   DirectExecutorService (inline) for Util.backgroundExecutor() -- that part works.
 - ROOT CAUSE: Util.ioPool()/nonCriticalIoPool() were made by makeIoExecutor() using RAW
   `Executors.newCachedThreadPool` (NOT via PlatformExecutors) -> a non-running TeaVM thread pool. IOWorker
   (chunk save/load) wraps ioPool in a PriorityConsecutiveExecutor and .join()s its tasks
   (IOWorker.java:253, ChunkMap chunk read/write) -> the pool never runs the task -> join blocks forever ->
   world-gen deadlock at ~16% (first chunks gen inline, then a chunk IO .join stalls).
 - FIX: platform-split ioPool like backgroundExecutor. Added PlatformExecutors.newIoExecutor(ThreadFactory):
   desktop (src/game) = Executors.newCachedThreadPool(factory) [unchanged behavior]; TeaVM (teavm/src/main)
   = new DirectExecutorService() [inline]. Util.makeIoExecutor now calls PlatformExecutors.newIoExecutor.
   Gate :compileJava + :teavm:compileJava GREEN. link35 = relink+verify.
 - NOTE: client chunk-MESH render (SectionRenderDispatcher) checked -- no separate broken pool (uses the
   already-direct backgroundExecutor / main thread), so probably fine; verify when the world actually renders.
 - NEXT after link35: re-drive Create New World + WORLDWAIT -> expect "Preparing spawn area" to reach 100%
   and the 3D world to render (first in-world frame!). Watch for FaceBakery "Can't find vertex" (block model
   bake precision) now that blocks actually render, ICU, and any client-render thread-pool stall.
 - HARNESS: chromium-run.js gained WORLDWAIT=<seconds> env (extra settle before screenshot for world gen).
   ⚠️ a long WORLDWAIT can exceed the 2-3min tool timeout on the driving Bash call -> run with a big timeout.

## Multiplayer continuation (2026-07-16): P2->P3 handoff + Direct Connect `wss://` plumbing
- Audited the staged `teavm/.../netty/{EaglerWebSocketTransport,EaglerWebSocketChannel,EaglerHandshakeHandler}`
  implementation against both the bundled 1.8 client and `eaglerxserver/core` source.
- **Server facts confirmed:** V5 accepts any Minecraft protocol by default (`max_minecraft_protocol_v5 = -1`);
  `WebSocketEaglerFrameCodec` only unwraps/wraps binary WS frames, so the ordinary MC varint stream stays in
  place after the Eagler handshake. Do NOT strip the `splitter`/`prepender` handlers.
- **Fixed P3 handoff:** on `0x09 SERVER_FINISH_LOGIN`, `EaglerHandshakeHandler` now removes itself and
  re-fires `channelActive`. This releases Minecraft's existing `Connection` handler to send its normal
  login-intention packet through the original varint pipeline. Previously handshake completion logged but
  MC could never begin login.
- **Direct Connect supports full URLs:** `ServerAddress` accepts and validates `ws://`/`wss://` URLs
  (including a path), retains the exact URL, and exposes it to the browser transport. `ConnectScreen` bypasses
  DNS only for such entries, using an unresolved host/port placeholder while preserving the URL for the WebSocket.
  Plain host:port addresses retain the vanilla parsing/resolution behavior.
- **Correct username:** `EaglerWebSocketChannel` reads the active Minecraft user when it is constructed instead
  of always advertising `Player` in the Eagler handshake.
- **Gate:** `./gradlew.bat :compileJava :teavm:compileJava --console=plain` PASSED after the changes.
- **Next verification (needs user's live URL):** relink once the current batch is ready, then run
  `WS_TEST2="wss://host/path" node teavm/run/chromium-run.js` to drive the full netty handshake and capture
  `[EaglerHS]` logs. Then use Direct Connect with the same URL to verify 0x09 -> vanilla login -> play.

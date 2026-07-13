# port-fable handoff — 2026-07-05 (Stage C link — wave 3, netty invoke gate mostly cleared)

Reply in Chinese only.

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

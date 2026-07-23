// Headless node harness: mocks just enough DOM/WebGL2 to drive the TeaVM boot
// past window/GL init so non-rendering runtime errors surface fast. It does NOT
// actually render — real pixels need chromium (swiftshader). Usage:
//   node node-boot.js path/to/classes.js
const path = process.argv[2] || '../build/generated/teavm/js/classes.js';

function glMock() {
	// Every GL call returns a fresh object (usable as an opaque handle) or 0.
	const cache = {};
	return new Proxy({}, {
		get(_t, prop) {
			// GL string queries (VENDOR 0x1F00, RENDERER 0x1F01, VERSION 0x1F02,
			// SHADING_LANGUAGE_VERSION 0x8B8C) must return strings — MC's
			// GraphicsWorkarounds.get() lowercases them. A real browser WebGL2 returns
			// strings here; only this fake Proxy would hand back 0 (→ String backed by a
			// number → toLowerCase crash). Everything else stays 0.
			if (prop === 'getParameter') return (pname) => {
				if (pname === 0x1F00) return 'EaglerMock';
				if (pname === 0x1F01) return 'EaglerMock WebGL2';
				if (pname === 0x1F02) return 'WebGL 2.0 (OpenGL ES 3.0 Mock)';
				if (pname === 0x8B8C) return 'WebGL GLSL ES 3.00';
				// Sensible non-zero sizes/limits so MC's device init doesn't compute
				// zero-sized GPU buffers (a real WebGL2 returns these; the fake Proxy
				// would otherwise hand back 0). Values mirror a typical desktop GL.
				if (pname === 0x8A34) return 256;   // UNIFORM_BUFFER_OFFSET_ALIGNMENT
				if (pname === 0x0D33) return 16384;  // MAX_TEXTURE_SIZE
				if (pname === 0x84FF) return 16;     // MAX_TEXTURE_MAX_ANISOTROPY_EXT
				if (pname === 0x8D57) return 4;      // MAX_SAMPLES
				if (pname === 0x8B4D) return 32;     // MAX_COMBINED_TEXTURE_IMAGE_UNITS
				if (pname === 0x8B4C) return 32;     // MAX_VERTEX_TEXTURE_IMAGE_UNITS
				if (pname === 0x8869) return 16;     // MAX_VERTEX_ATTRIBS
				if (pname === 0x8DFB) return 256;    // MAX_VERTEX_UNIFORM_VECTORS
				if (pname === 0x8DFC) return 256;    // MAX_FRAGMENT_UNIFORM_VECTORS
				return 0;
			};
			if (prop === 'getShaderParameter' || prop === 'getProgramParameter') return () => 1;
			if (prop === 'getShaderInfoLog' || prop === 'getProgramInfoLog') return () => '';
			if (prop === 'getError') return () => 0;
			if (prop === 'getUniformLocation') return () => ({});
			if (prop === 'getExtension') return () => null;
			if (prop === 'getContextAttributes') return () => ({});
			if (String(prop).startsWith('create')) return () => ({ __gl: prop });
			return () => 0;
		},
	});
}

const canvas = {
	width: 1280, height: 720,
	getContext: (kind) => (kind === 'webgl2' ? glMock() : null),
	addEventListener: () => {}, removeEventListener: () => {},
	getBoundingClientRect: () => ({ left: 0, top: 0, width: 1280, height: 720 }),
	style: {},
};
const doc = {
	getElementById: () => canvas,
	getElementsByTagName: () => [canvas],
	querySelector: () => canvas,
	createElement: () => canvas,
	addEventListener: () => {}, documentElement: { style: {} }, body: { style: {} },
};
globalThis.navigator = { hardwareConcurrency: 4, userAgent: 'node' };
globalThis.document = doc;
globalThis.window = globalThis;
globalThis.requestAnimationFrame = (cb) => setTimeout(() => cb(Date.now()), 16);
globalThis.performance = globalThis.performance || { now: () => Date.now() };

const m = require(path);
try {
	m.main([]);
} catch (e) {
	console.error('[main threw]', (e && e.stack) || e);
	process.exit(1);
}

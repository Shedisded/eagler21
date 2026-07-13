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
			if (prop === 'getParameter') return () => 0;
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

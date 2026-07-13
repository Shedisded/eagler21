package com.mojang.text2speech;

/**
 * ponytail: no-op stub replacing Mojang's text2speech native narrator.
 * Browser narration, if ever wanted, goes through the Web Speech API in the
 * TeaVM platform layer instead of a JNI flite binding.
 */
public interface Narrator {
	Narrator EMPTY = new Narrator() {
		@Override
		public void say(String text, boolean interrupt, float volume) {
		}

		@Override
		public void clear() {
		}

		@Override
		public void destroy() {
		}
	};

	void say(String text, boolean interrupt, float volume);

	void clear();

	default boolean active() {
		return false;
	}

	void destroy();

	static Narrator getNarrator() {
		return EMPTY;
	}
}

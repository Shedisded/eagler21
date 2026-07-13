package com.mojang.jtracy;

public class Zone implements AutoCloseable {
	public Zone addText(String text) {
		return this;
	}

	public Zone setColor(int color) {
		return this;
	}

	public Zone addValue(long value) {
		return this;
	}

	@Override
	public void close() {
	}
}

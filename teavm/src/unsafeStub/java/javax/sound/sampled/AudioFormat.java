package javax.sound.sampled;

/**
 * Link stub for javax.sound.sampled.AudioFormat. MC's audio decode path names it
 * (OpenAL streaming); the browser build routes audio through the no-op OpenAL
 * shim, so this only needs to link. Encoding.PCM_SIGNED is the one referenced
 * constant.
 */
@SuppressWarnings("unused")
public class AudioFormat {
	public static class Encoding {
		public static final Encoding PCM_SIGNED = new Encoding("PCM_SIGNED");
		public static final Encoding PCM_UNSIGNED = new Encoding("PCM_UNSIGNED");
		public static final Encoding PCM_FLOAT = new Encoding("PCM_FLOAT");

		private final String name;

		public Encoding(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private final Encoding encoding;
	private final float sampleRate;
	private final int sampleSizeInBits;
	private final int channels;

	public AudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian) {
		this.encoding = signed ? Encoding.PCM_SIGNED : Encoding.PCM_UNSIGNED;
		this.sampleRate = sampleRate;
		this.sampleSizeInBits = sampleSizeInBits;
		this.channels = channels;
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public int getSampleSizeInBits() {
		return sampleSizeInBits;
	}

	public int getChannels() {
		return channels;
	}
}

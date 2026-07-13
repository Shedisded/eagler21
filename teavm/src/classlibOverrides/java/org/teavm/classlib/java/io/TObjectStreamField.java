package org.teavm.classlib.java.io;

/**
 * Link stub for java.io.ObjectStreamField. Reachable via ICU's serialization
 * descriptors, but the browser build never serializes through it — data holder
 * only, no offset/reflection machinery.
 */
public class TObjectStreamField implements Comparable<Object> {
	private final String name;
	private final Class<?> type;
	private final boolean unshared;

	public TObjectStreamField(String name, Class<?> type) {
		this(name, type, false);
	}

	public TObjectStreamField(String name, Class<?> type, boolean unshared) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.type = type;
		this.unshared = unshared;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public char getTypeCode() {
		if (type == int.class) {
			return 'I';
		}
		if (type == long.class) {
			return 'J';
		}
		if (type == double.class) {
			return 'D';
		}
		if (type == float.class) {
			return 'F';
		}
		if (type == boolean.class) {
			return 'Z';
		}
		if (type == byte.class) {
			return 'B';
		}
		if (type == char.class) {
			return 'C';
		}
		if (type == short.class) {
			return 'S';
		}
		if (type != null && type.isArray()) {
			return '[';
		}
		return 'L';
	}

	public boolean isPrimitive() {
		return type != null && type.isPrimitive();
	}

	public boolean isUnshared() {
		return unshared;
	}

	public int getOffset() {
		return 0;
	}

	@Override
	public int compareTo(Object other) {
		if (other instanceof TObjectStreamField field) {
			return name.compareTo(field.name);
		}
		return 0;
	}
}

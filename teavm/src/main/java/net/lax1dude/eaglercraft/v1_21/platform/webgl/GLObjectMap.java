package net.lax1dude.eaglercraft.v1_21.platform.webgl;

import java.util.ArrayList;
import org.teavm.jso.JSObject;

/**
 * Maps LWJGL-style {@code int} object names to WebGL object handles. WebGL
 * hands back opaque objects; MC stores ints. Name 0 is reserved (GL "none");
 * names are 1-based and reused from a free list after delete.
 */
public final class GLObjectMap<T extends JSObject> {
	private final ArrayList<T> slots = new ArrayList<>();
	private final ArrayList<Integer> freeNames = new ArrayList<>();

	public GLObjectMap() {
		slots.add(null); // index 0 == GL none
	}

	public int register(T obj) {
		if (!freeNames.isEmpty()) {
			int name = freeNames.remove(freeNames.size() - 1);
			slots.set(name, obj);
			return name;
		}
		slots.add(obj);
		return slots.size() - 1;
	}

	public T get(int name) {
		return name > 0 && name < slots.size() ? slots.get(name) : null;
	}

	public void free(int name) {
		if (name > 0 && name < slots.size() && slots.get(name) != null) {
			slots.set(name, null);
			freeNames.add(name);
		}
	}
}

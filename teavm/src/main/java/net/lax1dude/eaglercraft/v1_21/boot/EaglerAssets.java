package net.lax1dude.eaglercraft.v1_21.boot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.teavm.jso.JSBody;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * Boot-time vanilla-asset loader for the browser build. MC's VanillaPackResources reads
 * the vanilla pack by WALKING a real filesystem (Files.walk / newDirectoryStream +
 * newInputStream), which embedded classpath resources cannot provide (Class.getResource
 * returns null here and there is nothing to traverse). TeaVM's default FS IS a walkable
 * in-memory VFS, so we unzip the vanilla resources into it at boot and point
 * ClientPackSource at {@link #ROOT} via pushUniversalPath.
 *
 * The zip bytes are fetched by the page harness (fetch('/vanilla-assets.zip')) BEFORE
 * main([]) and stashed at window.__EAGLER_ZIP; we read them here synchronously. If the
 * global is absent (e.g. a bare node run with no zip) we skip — MC then logs missing
 * resources but still boots, which is the pre-assets behaviour.
 */
public final class EaglerAssets {
	/** VFS directory that becomes the vanilla pack root (contains assets/ and data/). */
	public static final String ROOT = "/eagler-assets";

	private EaglerAssets() {
	}

	@JSBody(params = {}, script = "return (typeof window !== 'undefined' && window.__EAGLER_ZIP) ? window.__EAGLER_ZIP : null;")
	private static native Int8Array zipBytes();

	/** Unzip the fetched vanilla-assets zip into the TeaVM VFS under {@link #ROOT}. */
	public static void install() {
		Int8Array data = zipBytes();
		if (data == null) {
			System.out.println("[EaglerAssets] no window.__EAGLER_ZIP present; skipping vanilla asset install");
			return;
		}
		int len = data.getLength();
		byte[] zip = new byte[len];
		for (int i = 0; i < len; i++) {
			zip[i] = data.get(i);
		}

		long t0 = System.currentTimeMillis();
		int files = 0;
		byte[] buf = new byte[16384];
		try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip))) {
			ZipEntry e;
			while ((e = zis.getNextEntry()) != null) {
				if (e.isDirectory()) {
					continue;
				}
				Path out = Paths.get(ROOT, e.getName());
				Path parent = out.getParent();
				if (parent != null) {
					Files.createDirectories(parent);
				}
				java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream(Math.max(64, (int) e.getSize()));
				int n;
				while ((n = zis.read(buf)) > 0) {
					bos.write(buf, 0, n);
				}
				Files.write(out, bos.toByteArray());
				files++;
			}
		} catch (IOException ex) {
			System.out.println("[EaglerAssets] FAILED to install vanilla assets: " + ex);
			ex.printStackTrace();
			return;
		}
		System.out.println("[EaglerAssets] installed " + files + " vanilla resource files into " + ROOT
				+ " in " + (System.currentTimeMillis() - t0) + "ms");
	}
}

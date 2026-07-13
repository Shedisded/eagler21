package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.PointerBuffer;

/**
 * TeaVM shim: no native FreeType in the browser. FT_Init_FreeType succeeds (so
 * the font system boots), but every FT_New_Memory_Face fails, so MC skips
 * TrueType providers and falls back to bitmap / unihex fonts. Everything past a
 * face is therefore dead code that only needs to link.
 */
public final class FreeType {
	private FreeType() {
	}

	public static final int FT_ENCODING_UNICODE = 0x756E6963; // 'unic'

	public static int FT_Init_FreeType(PointerBuffer library) {
		library.put(0, 1L); // fake non-null library handle
		return 0;
	}

	public static int FT_Done_Library(long library) {
		return 0;
	}

	public static int FT_Done_Face(FT_Face face) {
		return 0;
	}

	public static String FT_Error_String(int error) {
		return null;
	}

	public static int FT_New_Memory_Face(long library, ByteBuffer data, long faceIndex, PointerBuffer face) {
		return 1; // non-zero => error => MC skips this TrueType font
	}

	public static String FT_Get_Font_Format(FT_Face face) {
		return "";
	}

	public static int FT_Select_Charmap(FT_Face face, int encoding) {
		return 0;
	}

	public static int FT_Set_Pixel_Sizes(FT_Face face, int width, int height) {
		return 0;
	}

	public static int FT_Load_Glyph(FT_Face face, int glyphIndex, int loadFlags) {
		return 0;
	}

	public static long FT_Get_First_Char(FT_Face face, IntBuffer glyphIndex) {
		return 0L;
	}

	public static long FT_Get_Next_Char(FT_Face face, long charCode, IntBuffer glyphIndex) {
		return 0L;
	}

	public static void FT_Set_Transform(FT_Face face, FT_Matrix matrix, FT_Vector delta) {
	}
}

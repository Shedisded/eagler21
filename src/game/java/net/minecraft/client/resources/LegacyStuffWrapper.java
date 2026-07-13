package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class LegacyStuffWrapper {
    @Deprecated
    public static int[] getPixels(ResourceManager p_118727_, Identifier p_468247_) throws IOException {
        int[] aint;
        try (
            InputStream inputstream = p_118727_.open(p_468247_);
            NativeImage nativeimage = NativeImage.read(inputstream);
        ) {
            aint = nativeimage.makePixelArray();
        }

        return aint;
    }
}

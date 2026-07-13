package net.minecraft.client.renderer.texture;

import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.resources.Identifier;

public interface Dumpable {
    void dumpContents(Identifier p_467299_, Path p_276123_) throws IOException;
}

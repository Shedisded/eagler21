package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class SimpleTexture extends ReloadableTexture {
    public SimpleTexture(Identifier p_469760_) {
        super(p_469760_);
    }

    @Override
    public TextureContents loadContents(ResourceManager p_389644_) throws IOException {
        return TextureContents.load(p_389644_, this.resourceId());
    }
}

package net.minecraft.client.renderer.texture.atlas;

import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

public interface SpriteSource {
    FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");

    void run(ResourceManager p_261770_, SpriteSource.Output p_261757_);

    MapCodec<? extends SpriteSource> codec();

    public interface DiscardableLoader extends SpriteSource.Loader {
        default void discard() {
        }
    }

    @FunctionalInterface
    public interface Loader {
        @Nullable SpriteContents get(SpriteResourceLoader p_460712_);
    }

    public interface Output {
        default void add(Identifier p_467249_, Resource p_261651_) {
            this.add(p_467249_, p_465742_ -> p_465742_.loadSprite(p_467249_, p_261651_));
        }

        void add(Identifier p_466926_, SpriteSource.DiscardableLoader p_460949_);

        void removeAll(Predicate<Identifier> p_261532_);
    }
}

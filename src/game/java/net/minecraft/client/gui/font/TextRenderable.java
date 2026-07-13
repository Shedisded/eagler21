package net.minecraft.client.gui.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;

public interface TextRenderable {
    void render(Matrix4f p_438890_, VertexConsumer p_439958_, int p_439123_, boolean p_439351_);

    RenderType renderType(Font.DisplayMode p_439293_);

    GpuTextureView textureView();

    RenderPipeline guiPipeline();

    float left();

    float top();

    float right();

    float bottom();

    public interface Styled extends ActiveArea, TextRenderable {
        @Override
        default float activeLeft() {
            return this.left();
        }

        @Override
        default float activeTop() {
            return this.top();
        }

        @Override
        default float activeRight() {
            return this.right();
        }

        @Override
        default float activeBottom() {
            return this.bottom();
        }
    }
}

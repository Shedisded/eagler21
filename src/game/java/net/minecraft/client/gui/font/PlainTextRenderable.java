package net.minecraft.client.gui.font;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

public interface PlainTextRenderable extends TextRenderable.Styled {
    float DEFAULT_WIDTH = 8.0F;
    float DEFAULT_HEIGHT = 8.0F;
    float DEFUAULT_ASCENT = 8.0F;

    @Override
    default void render(Matrix4f p_442857_, VertexConsumer p_442956_, int p_443475_, boolean p_443415_) {
        float f = 0.0F;
        if (this.shadowColor() != 0) {
            this.renderSprite(p_442857_, p_442956_, p_443475_, this.shadowOffset(), this.shadowOffset(), 0.0F, this.shadowColor());
            if (!p_443415_) {
                f += 0.03F;
            }
        }

        this.renderSprite(p_442857_, p_442956_, p_443475_, 0.0F, 0.0F, f, this.color());
    }

    void renderSprite(Matrix4f p_443535_, VertexConsumer p_443369_, int p_443207_, float p_442916_, float p_442886_, float p_443017_, int p_443575_);

    float x();

    float y();

    int color();

    int shadowColor();

    float shadowOffset();

    default float width() {
        return 8.0F;
    }

    default float height() {
        return 8.0F;
    }

    default float ascent() {
        return 8.0F;
    }

    @Override
    default float left() {
        return this.x();
    }

    @Override
    default float right() {
        return this.left() + this.width();
    }

    @Override
    default float top() {
        return this.y() + 7.0F - this.ascent();
    }

    @Override
    default float bottom() {
        return this.activeTop() + this.height();
    }
}

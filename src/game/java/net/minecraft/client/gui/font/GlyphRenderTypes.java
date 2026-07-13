package net.minecraft.client.gui.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public record GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset, RenderPipeline guiPipeline) {
    public static GlyphRenderTypes createForIntensityTexture(Identifier p_468890_) {
        return new GlyphRenderTypes(
            RenderTypes.textIntensity(p_468890_),
            RenderTypes.textIntensitySeeThrough(p_468890_),
            RenderTypes.textIntensityPolygonOffset(p_468890_),
            RenderPipelines.GUI_TEXT_INTENSITY
        );
    }

    public static GlyphRenderTypes createForColorTexture(Identifier p_467758_) {
        return new GlyphRenderTypes(
            RenderTypes.text(p_467758_), RenderTypes.textSeeThrough(p_467758_), RenderTypes.textPolygonOffset(p_467758_), RenderPipelines.GUI_TEXT
        );
    }

    public RenderType select(Font.DisplayMode p_285259_) {
        return switch (p_285259_) {
            case NORMAL -> this.normal;
            case SEE_THROUGH -> this.seeThrough;
            case POLYGON_OFFSET -> this.polygonOffset;
        };
    }
}

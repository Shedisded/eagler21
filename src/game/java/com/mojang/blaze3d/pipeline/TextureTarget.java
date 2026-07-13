package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import org.jspecify.annotations.Nullable;

public class TextureTarget extends RenderTarget {
    public TextureTarget(@Nullable String p_405118_, int p_166213_, int p_166214_, boolean p_166215_) {
        super(p_405118_, p_166215_);
        RenderSystem.assertOnRenderThread();
        this.resize(p_166213_, p_166214_);
    }
}

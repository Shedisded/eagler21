package com.mojang.blaze3d.resource;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;

public record RenderTargetDescriptor(int width, int height, boolean useDepth, int clearColor) implements ResourceDescriptor<RenderTarget> {
    public RenderTarget allocate() {
        return new TextureTarget(null, this.width, this.height, this.useDepth);
    }

    public void prepare(RenderTarget p_393765_) {
        if (this.useDepth) {
            RenderSystem.getDevice()
                .createCommandEncoder()
                .clearColorAndDepthTextures(p_393765_.getColorTexture(), this.clearColor, p_393765_.getDepthTexture(), 1.0);
        } else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(p_393765_.getColorTexture(), this.clearColor);
        }
    }

    public void free(RenderTarget p_363223_) {
        p_363223_.destroyBuffers();
    }

    @Override
    public boolean canUsePhysicalResource(ResourceDescriptor<?> p_394237_) {
        return !(p_394237_ instanceof RenderTargetDescriptor rendertargetdescriptor)
            ? false
            : this.width == rendertargetdescriptor.width && this.height == rendertargetdescriptor.height && this.useDepth == rendertargetdescriptor.useDepth;
    }
}

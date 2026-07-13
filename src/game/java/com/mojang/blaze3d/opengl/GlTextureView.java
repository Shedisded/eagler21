package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.jspecify.annotations.Nullable;

public class GlTextureView extends GpuTextureView {
    private static final int EMPTY = -1;
    private boolean closed;
    private int firstFboId = -1;
    private int firstFboDepthId = -1;
    private @Nullable Int2IntMap fboCache;

    protected GlTextureView(GlTexture p_423648_, int p_423671_, int p_423545_) {
        super(p_423648_, p_423671_, p_423545_);
        p_423648_.addViews();
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.texture().removeViews();
            if (this.firstFboId != -1) {
                GlStateManager._glDeleteFramebuffers(this.firstFboId);
            }

            if (this.fboCache != null) {
                for (int i : this.fboCache.values()) {
                    GlStateManager._glDeleteFramebuffers(i);
                }
            }
        }
    }

    public int getFbo(DirectStateAccess p_461283_, @Nullable GpuTexture p_460970_) {
        int i = p_460970_ == null ? 0 : ((GlTexture)p_460970_).id;
        if (this.firstFboDepthId == i) {
            return this.firstFboId;
        } else if (this.firstFboId == -1) {
            this.firstFboId = this.createFbo(p_461283_, i);
            this.firstFboDepthId = i;
            return this.firstFboId;
        } else {
            if (this.fboCache == null) {
                this.fboCache = new Int2IntArrayMap();
            }

            return this.fboCache.computeIfAbsent(i, p_470463_ -> this.createFbo(p_461283_, p_470463_));
        }
    }

    private int createFbo(DirectStateAccess p_470787_, int p_470630_) {
        int i = p_470787_.createFrameBufferObject();
        p_470787_.bindFrameBufferTextures(i, this.texture().id, p_470630_, this.baseMipLevel(), 0);
        return i;
    }

    public GlTexture texture() {
        return (GlTexture)super.texture();
    }
}

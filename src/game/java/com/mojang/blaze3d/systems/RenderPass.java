package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

@DontObfuscate
public interface RenderPass extends AutoCloseable {
    void pushDebugGroup(Supplier<String> p_419472_);

    void popDebugGroup();

    void setPipeline(RenderPipeline p_409777_);

    void bindTexture(String p_410511_, @Nullable GpuTextureView p_423573_, @Nullable GpuSampler p_456187_);

    void setUniform(String p_410513_, GpuBuffer p_418136_);

    void setUniform(String p_410316_, GpuBufferSlice p_418316_);

    void enableScissor(int p_409956_, int p_410530_, int p_409728_, int p_410747_);

    void disableScissor();

    void setVertexBuffer(int p_409605_, GpuBuffer p_410644_);

    void setIndexBuffer(GpuBuffer p_409862_, VertexFormat.IndexType p_410093_);

    void drawIndexed(int p_410350_, int p_410790_, int p_419701_, int p_419484_);

    <T> void drawMultipleIndexed(
        Collection<RenderPass.Draw<T>> p_409795_,
        @Nullable GpuBuffer p_412255_,
        VertexFormat.@Nullable IndexType p_412424_,
        Collection<String> p_418087_,
        T p_428215_
    );

    void draw(int p_410083_, int p_410498_);

    @Override
    void close();

    public record Draw<T>(
        int slot,
        GpuBuffer vertexBuffer,
        @Nullable GpuBuffer indexBuffer,
        VertexFormat.@Nullable IndexType indexType,
        int firstIndex,
        int indexCount,
        @Nullable BiConsumer<T, RenderPass.UniformUploader> uniformUploaderConsumer
    ) {
        public Draw(int p_409953_, GpuBuffer p_410253_, GpuBuffer p_410419_, VertexFormat.IndexType p_410538_, int p_410203_, int p_410514_) {
            this(p_409953_, p_410253_, p_410419_, p_410538_, p_410203_, p_410514_, null);
        }
    }

    public interface UniformUploader {
        void upload(String p_410446_, GpuBufferSlice p_418221_);
    }
}

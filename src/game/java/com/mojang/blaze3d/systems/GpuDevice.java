package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

@DontObfuscate
public interface GpuDevice {
    CommandEncoder createCommandEncoder();

    GpuSampler createSampler(AddressMode p_455282_, AddressMode p_456046_, FilterMode p_456093_, FilterMode p_455084_, int p_461115_, OptionalDouble p_467563_);

    GpuTexture createTexture(
        @Nullable Supplier<String> p_423528_,
        @GpuTexture.Usage int p_410438_,
        TextureFormat p_410483_,
        int p_410598_,
        int p_409867_,
        int p_419879_,
        int p_423680_
    );

    GpuTexture createTexture(
        @Nullable String p_423663_, @GpuTexture.Usage int p_409718_, TextureFormat p_409597_, int p_410249_, int p_410020_, int p_419865_, int p_423594_
    );

    GpuTextureView createTextureView(GpuTexture p_423544_);

    GpuTextureView createTextureView(GpuTexture p_423440_, int p_423542_, int p_423562_);

    GpuBuffer createBuffer(@Nullable Supplier<String> p_410008_, @GpuBuffer.Usage int p_410396_, long p_482308_);

    GpuBuffer createBuffer(@Nullable Supplier<String> p_410623_, @GpuBuffer.Usage int p_418336_, ByteBuffer p_410117_);

    String getImplementationInformation();

    List<String> getLastDebugMessages();

    boolean isDebuggingEnabled();

    String getVendor();

    String getBackendName();

    String getVersion();

    String getRenderer();

    int getMaxTextureSize();

    int getUniformOffsetAlignment();

    default CompiledRenderPipeline precompilePipeline(RenderPipeline p_410493_) {
        return this.precompilePipeline(p_410493_, null);
    }

    CompiledRenderPipeline precompilePipeline(RenderPipeline p_410310_, @Nullable ShaderSource p_460822_);

    void clearPipelineCache();

    List<String> getEnabledExtensions();

    int getMaxSupportedAnisotropy();

    void close();
}

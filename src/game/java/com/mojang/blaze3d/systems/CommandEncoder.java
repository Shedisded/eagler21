package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

@DontObfuscate
public interface CommandEncoder {
    RenderPass createRenderPass(Supplier<String> p_419676_, GpuTextureView p_423660_, OptionalInt p_409831_);

    RenderPass createRenderPass(
        Supplier<String> p_419638_, GpuTextureView p_423639_, OptionalInt p_409827_, @Nullable GpuTextureView p_423568_, OptionalDouble p_423657_
    );

    void clearColorTexture(GpuTexture p_409991_, int p_410624_);

    void clearColorAndDepthTextures(GpuTexture p_410154_, int p_410001_, GpuTexture p_410146_, double p_410605_);

    void clearColorAndDepthTextures(
        GpuTexture p_416454_, int p_415680_, GpuTexture p_416405_, double p_416313_, int p_416654_, int p_415926_, int p_416578_, int p_416179_
    );

    void clearDepthTexture(GpuTexture p_410276_, double p_410184_);

    void writeToBuffer(GpuBufferSlice p_418507_, ByteBuffer p_410481_);

    GpuBuffer.MappedView mapBuffer(GpuBuffer p_418099_, boolean p_418103_, boolean p_418179_);

    GpuBuffer.MappedView mapBuffer(GpuBufferSlice p_418513_, boolean p_418243_, boolean p_418527_);

    void copyToBuffer(GpuBufferSlice p_428842_, GpuBufferSlice p_428828_);

    void writeToTexture(GpuTexture p_410601_, NativeImage p_409782_);

    void writeToTexture(
        GpuTexture p_409951_,
        NativeImage p_410471_,
        int p_410163_,
        int p_410504_,
        int p_410561_,
        int p_410675_,
        int p_409936_,
        int p_410387_,
        int p_409585_,
        int p_423683_
    );

    void writeToTexture(
        GpuTexture p_410708_,
        ByteBuffer p_436585_,
        NativeImage.Format p_410346_,
        int p_410145_,
        int p_409806_,
        int p_409855_,
        int p_409801_,
        int p_409998_,
        int p_423525_
    );

    void copyTextureToBuffer(GpuTexture p_409732_, GpuBuffer p_410694_, long p_482283_, Runnable p_410116_, int p_409794_);

    void copyTextureToBuffer(
        GpuTexture p_409709_,
        GpuBuffer p_409653_,
        long p_482289_,
        Runnable p_409606_,
        int p_409654_,
        int p_409664_,
        int p_482314_,
        int p_482306_,
        int p_482320_
    );

    void copyTextureToTexture(
        GpuTexture p_410347_, GpuTexture p_410302_, int p_410741_, int p_409745_, int p_409805_, int p_409992_, int p_409918_, int p_409592_, int p_410300_
    );

    void presentTexture(GpuTextureView p_423596_);

    GpuFence createFence();

    GpuQuery timerQueryBegin();

    void timerQueryEnd(GpuQuery p_458108_);
}

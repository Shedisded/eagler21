package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.GraphicsWorkarounds;
import com.mojang.blaze3d.buffers.GpuBuffer;
import java.nio.ByteBuffer;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;

public abstract class DirectStateAccess {
    public static DirectStateAccess create(GLCapabilities p_412632_, Set<String> p_412533_, GraphicsWorkarounds p_451134_) {
        if (p_412632_.GL_ARB_direct_state_access && GlDevice.USE_GL_ARB_direct_state_access && !p_451134_.isGlOnDx12()) {
            p_412533_.add("GL_ARB_direct_state_access");
            return new DirectStateAccess.Core();
        } else {
            return new DirectStateAccess.Emulated();
        }
    }

    abstract int createBuffer();

    abstract void bufferData(int p_418516_, long p_418151_, @GpuBuffer.Usage int p_418400_);

    abstract void bufferData(int p_418091_, ByteBuffer p_418122_, @GpuBuffer.Usage int p_418335_);

    abstract void bufferSubData(int p_418475_, long p_482288_, ByteBuffer p_418182_, @GpuBuffer.Usage int p_418483_);

    abstract void bufferStorage(int p_418402_, long p_418519_, @GpuBuffer.Usage int p_418498_);

    abstract void bufferStorage(int p_418285_, ByteBuffer p_418343_, @GpuBuffer.Usage int p_418263_);

    abstract @Nullable ByteBuffer mapBufferRange(int p_418449_, long p_482298_, long p_482280_, int p_418139_, @GpuBuffer.Usage int p_418197_);

    abstract void unmapBuffer(int p_418016_, @GpuBuffer.Usage int p_434489_);

    abstract int createFrameBufferObject();

    abstract void bindFrameBufferTextures(int p_412046_, int p_412388_, int p_412116_, int p_412536_, int p_412779_);

    abstract void blitFrameBuffers(
        int p_412546_,
        int p_412629_,
        int p_412541_,
        int p_412093_,
        int p_412559_,
        int p_412340_,
        int p_412380_,
        int p_412503_,
        int p_412238_,
        int p_412069_,
        int p_412193_,
        int p_412596_
    );

    abstract void flushMappedBufferRange(int p_418119_, long p_482303_, long p_482301_, @GpuBuffer.Usage int p_418382_);

    abstract void copyBufferSubData(int p_428838_, int p_428829_, long p_482310_, long p_482328_, long p_482286_);

    static class Core extends DirectStateAccess {
        @Override
        int createBuffer() {
            GlStateManager.incrementTrackedBuffers();
            return ARBDirectStateAccess.glCreateBuffers();
        }

        @Override
        void bufferData(int p_418123_, long p_418371_, @GpuBuffer.Usage int p_418160_) {
            ARBDirectStateAccess.glNamedBufferData(p_418123_, p_418371_, GlConst.bufferUsageToGlEnum(p_418160_));
        }

        @Override
        void bufferData(int p_418280_, ByteBuffer p_418007_, @GpuBuffer.Usage int p_418178_) {
            ARBDirectStateAccess.glNamedBufferData(p_418280_, p_418007_, GlConst.bufferUsageToGlEnum(p_418178_));
        }

        @Override
        void bufferSubData(int p_418076_, long p_482295_, ByteBuffer p_418117_, @GpuBuffer.Usage int p_418299_) {
            ARBDirectStateAccess.glNamedBufferSubData(p_418076_, p_482295_, p_418117_);
        }

        @Override
        void bufferStorage(int p_418428_, long p_418019_, @GpuBuffer.Usage int p_418289_) {
            ARBDirectStateAccess.glNamedBufferStorage(p_418428_, p_418019_, GlConst.bufferUsageToGlFlag(p_418289_));
        }

        @Override
        void bufferStorage(int p_418345_, ByteBuffer p_418031_, @GpuBuffer.Usage int p_418465_) {
            ARBDirectStateAccess.glNamedBufferStorage(p_418345_, p_418031_, GlConst.bufferUsageToGlFlag(p_418465_));
        }

        @Override
        @Nullable ByteBuffer mapBufferRange(int p_418027_, long p_482278_, long p_482324_, int p_418408_, @GpuBuffer.Usage int p_418310_) {
            return ARBDirectStateAccess.glMapNamedBufferRange(p_418027_, p_482278_, p_482324_, p_418408_);
        }

        @Override
        void unmapBuffer(int p_418046_, int p_433042_) {
            ARBDirectStateAccess.glUnmapNamedBuffer(p_418046_);
        }

        @Override
        public int createFrameBufferObject() {
            return ARBDirectStateAccess.glCreateFramebuffers();
        }

        @Override
        public void bindFrameBufferTextures(int p_412474_, int p_412101_, int p_412181_, int p_412742_, @GpuBuffer.Usage int p_412591_) {
            ARBDirectStateAccess.glNamedFramebufferTexture(p_412474_, 36064, p_412101_, p_412742_);
            ARBDirectStateAccess.glNamedFramebufferTexture(p_412474_, 36096, p_412181_, p_412742_);
            if (p_412591_ != 0) {
                GlStateManager._glBindFramebuffer(p_412591_, p_412474_);
            }
        }

        @Override
        public void blitFrameBuffers(
            int p_412346_,
            int p_412174_,
            int p_412752_,
            int p_412365_,
            int p_412477_,
            int p_412615_,
            int p_412700_,
            int p_412178_,
            int p_412260_,
            int p_412584_,
            int p_412685_,
            int p_412482_
        ) {
            ARBDirectStateAccess.glBlitNamedFramebuffer(
                p_412346_, p_412174_, p_412752_, p_412365_, p_412477_, p_412615_, p_412700_, p_412178_, p_412260_, p_412584_, p_412685_, p_412482_
            );
        }

        @Override
        void flushMappedBufferRange(int p_418135_, long p_482322_, long p_482291_, @GpuBuffer.Usage int p_418262_) {
            ARBDirectStateAccess.glFlushMappedNamedBufferRange(p_418135_, p_482322_, p_482291_);
        }

        @Override
        void copyBufferSubData(int p_428836_, int p_428841_, long p_482292_, long p_482332_, long p_482334_) {
            ARBDirectStateAccess.glCopyNamedBufferSubData(p_428836_, p_428841_, p_482292_, p_482332_, p_482334_);
        }
    }

    static class Emulated extends DirectStateAccess {
        private int selectBufferBindTarget(@GpuBuffer.Usage int p_435075_) {
            if ((p_435075_ & 32) != 0) {
                return 34962;
            } else if ((p_435075_ & 64) != 0) {
                return 34963;
            } else {
                return (p_435075_ & 128) != 0 ? 35345 : 36663;
            }
        }

        @Override
        int createBuffer() {
            return GlStateManager._glGenBuffers();
        }

        @Override
        void bufferData(int p_418415_, long p_418240_, @GpuBuffer.Usage int p_418204_) {
            int i = this.selectBufferBindTarget(p_418204_);
            GlStateManager._glBindBuffer(i, p_418415_);
            GlStateManager._glBufferData(i, p_418240_, GlConst.bufferUsageToGlEnum(p_418204_));
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void bufferData(int p_418504_, ByteBuffer p_418319_, @GpuBuffer.Usage int p_418033_) {
            int i = this.selectBufferBindTarget(p_418033_);
            GlStateManager._glBindBuffer(i, p_418504_);
            GlStateManager._glBufferData(i, p_418319_, GlConst.bufferUsageToGlEnum(p_418033_));
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void bufferSubData(int p_418332_, long p_482316_, ByteBuffer p_418268_, @GpuBuffer.Usage int p_418165_) {
            int i = this.selectBufferBindTarget(p_418165_);
            GlStateManager._glBindBuffer(i, p_418332_);
            GlStateManager._glBufferSubData(i, p_482316_, p_418268_);
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void bufferStorage(int p_418279_, long p_418194_, @GpuBuffer.Usage int p_418045_) {
            int i = this.selectBufferBindTarget(p_418045_);
            GlStateManager._glBindBuffer(i, p_418279_);
            ARBBufferStorage.glBufferStorage(i, p_418194_, GlConst.bufferUsageToGlFlag(p_418045_));
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void bufferStorage(int p_418284_, ByteBuffer p_418011_, @GpuBuffer.Usage int p_418406_) {
            int i = this.selectBufferBindTarget(p_418406_);
            GlStateManager._glBindBuffer(i, p_418284_);
            ARBBufferStorage.glBufferStorage(i, p_418011_, GlConst.bufferUsageToGlFlag(p_418406_));
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        @Nullable ByteBuffer mapBufferRange(int p_418226_, long p_482323_, long p_482335_, int p_418017_, @GpuBuffer.Usage int p_418092_) {
            int i = this.selectBufferBindTarget(p_418092_);
            GlStateManager._glBindBuffer(i, p_418226_);
            ByteBuffer bytebuffer = GlStateManager._glMapBufferRange(i, p_482323_, p_482335_, p_418017_);
            GlStateManager._glBindBuffer(i, 0);
            return bytebuffer;
        }

        @Override
        void unmapBuffer(int p_418235_, @GpuBuffer.Usage int p_433066_) {
            int i = this.selectBufferBindTarget(p_433066_);
            GlStateManager._glBindBuffer(i, p_418235_);
            GlStateManager._glUnmapBuffer(i);
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void flushMappedBufferRange(int p_418330_, long p_482321_, long p_482279_, @GpuBuffer.Usage int p_418325_) {
            int i = this.selectBufferBindTarget(p_418325_);
            GlStateManager._glBindBuffer(i, p_418330_);
            GL30.glFlushMappedBufferRange(i, p_482321_, p_482279_);
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void copyBufferSubData(int p_428853_, int p_428827_, long p_482299_, long p_482327_, long p_482305_) {
            GlStateManager._glBindBuffer(36662, p_428853_);
            GlStateManager._glBindBuffer(36663, p_428827_);
            GL31.glCopyBufferSubData(36662, 36663, p_482299_, p_482327_, p_482305_);
            GlStateManager._glBindBuffer(36662, 0);
            GlStateManager._glBindBuffer(36663, 0);
        }

        @Override
        public int createFrameBufferObject() {
            return GlStateManager.glGenFramebuffers();
        }

        @Override
        public void bindFrameBufferTextures(int p_412192_, int p_412614_, int p_412332_, int p_412295_, int p_412775_) {
            int i = p_412775_ == 0 ? '\u8ca9' : p_412775_;
            int j = GlStateManager.getFrameBuffer(i);
            GlStateManager._glBindFramebuffer(i, p_412192_);
            GlStateManager._glFramebufferTexture2D(i, 36064, 3553, p_412614_, p_412295_);
            GlStateManager._glFramebufferTexture2D(i, 36096, 3553, p_412332_, p_412295_);
            if (p_412775_ == 0) {
                GlStateManager._glBindFramebuffer(i, j);
            }
        }

        @Override
        public void blitFrameBuffers(
            int p_412705_,
            int p_412256_,
            int p_412077_,
            int p_412762_,
            int p_412738_,
            int p_412701_,
            int p_412455_,
            int p_412525_,
            int p_412252_,
            int p_412684_,
            int p_412520_,
            int p_412585_
        ) {
            int i = GlStateManager.getFrameBuffer(36008);
            int j = GlStateManager.getFrameBuffer(36009);
            GlStateManager._glBindFramebuffer(36008, p_412705_);
            GlStateManager._glBindFramebuffer(36009, p_412256_);
            GlStateManager._glBlitFrameBuffer(p_412077_, p_412762_, p_412738_, p_412701_, p_412455_, p_412525_, p_412252_, p_412684_, p_412520_, p_412585_);
            GlStateManager._glBindFramebuffer(36008, i);
            GlStateManager._glBindFramebuffer(36009, j);
        }
    }
}

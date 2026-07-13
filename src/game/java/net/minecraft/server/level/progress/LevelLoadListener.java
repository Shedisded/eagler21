package net.minecraft.server.level.progress;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public interface LevelLoadListener {
    static LevelLoadListener compose(final LevelLoadListener p_434691_, final LevelLoadListener p_435687_) {
        return new LevelLoadListener() {
            @Override
            public void start(LevelLoadListener.Stage p_433873_, int p_435617_) {
                p_434691_.start(p_433873_, p_435617_);
                p_435687_.start(p_433873_, p_435617_);
            }

            @Override
            public void update(LevelLoadListener.Stage p_433548_, int p_433120_, int p_433990_) {
                p_434691_.update(p_433548_, p_433120_, p_433990_);
                p_435687_.update(p_433548_, p_433120_, p_433990_);
            }

            @Override
            public void finish(LevelLoadListener.Stage p_433286_) {
                p_434691_.finish(p_433286_);
                p_435687_.finish(p_433286_);
            }

            @Override
            public void updateFocus(ResourceKey<Level> p_433836_, ChunkPos p_433195_) {
                p_434691_.updateFocus(p_433836_, p_433195_);
                p_435687_.updateFocus(p_433836_, p_433195_);
            }
        };
    }

    void start(LevelLoadListener.Stage p_433225_, int p_433798_);

    void update(LevelLoadListener.Stage p_433769_, int p_436047_, int p_434383_);

    void finish(LevelLoadListener.Stage p_435801_);

    void updateFocus(ResourceKey<Level> p_434196_, ChunkPos p_434258_);

    public static enum Stage {
        START_SERVER,
        PREPARE_GLOBAL_SPAWN,
        LOAD_INITIAL_CHUNKS,
        LOAD_PLAYER_CHUNKS;
    }
}

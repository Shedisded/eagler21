package net.minecraft.client.gui.components.debug;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public interface DebugScreenEntry {
    void display(DebugScreenDisplayer p_435528_, @Nullable Level p_434997_, @Nullable LevelChunk p_433059_, @Nullable LevelChunk p_434406_);

    default boolean isAllowed(boolean p_435984_) {
        return !p_435984_;
    }

    default DebugEntryCategory category() {
        return DebugEntryCategory.SCREEN_TEXT;
    }
}

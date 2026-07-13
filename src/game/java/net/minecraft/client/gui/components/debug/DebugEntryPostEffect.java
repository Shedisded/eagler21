package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryPostEffect implements DebugScreenEntry {
    @Override
    public void display(DebugScreenDisplayer p_434035_, @Nullable Level p_435454_, @Nullable LevelChunk p_433496_, @Nullable LevelChunk p_434900_) {
        Minecraft minecraft = Minecraft.getInstance();
        Identifier identifier = minecraft.gameRenderer.currentPostEffect();
        if (identifier != null) {
            p_434035_.addLine("Post: " + identifier);
        }
    }
}

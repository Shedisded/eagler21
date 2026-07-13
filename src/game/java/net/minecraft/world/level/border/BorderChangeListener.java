package net.minecraft.world.level.border;

public interface BorderChangeListener {
    void onSetSize(WorldBorder p_447016_, double p_445857_);

    void onLerpSize(WorldBorder p_446155_, double p_445890_, double p_445992_, long p_446799_, long p_459150_);

    void onSetCenter(WorldBorder p_445393_, double p_447081_, double p_446847_);

    void onSetWarningTime(WorldBorder p_446543_, int p_446659_);

    void onSetWarningBlocks(WorldBorder p_446764_, int p_446025_);

    void onSetDamagePerBlock(WorldBorder p_446600_, double p_446558_);

    void onSetSafeZone(WorldBorder p_445986_, double p_446577_);
}

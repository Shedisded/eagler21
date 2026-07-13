package net.minecraft.world.level.gamerules;

import net.minecraft.util.StringRepresentable;

public enum GameRuleType implements StringRepresentable {
    INT("integer"),
    BOOL("boolean");

    private final String name;

    private GameRuleType(String p_460628_) {
        this.name = p_460628_;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}

package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.jspecify.annotations.Nullable;

public abstract class FogEnvironment {
    public abstract void setupFog(FogData p_423442_, Camera p_455736_, ClientLevel p_423574_, float p_423615_, DeltaTracker p_423614_);

    public boolean providesColor() {
        return true;
    }

    public int getBaseColor(ClientLevel p_423508_, Camera p_423518_, int p_423620_, float p_423647_) {
        return -1;
    }

    public boolean modifiesDarkness() {
        return false;
    }

    public float getModifiedDarkness(LivingEntity p_423582_, float p_423527_, float p_423572_) {
        return p_423527_;
    }

    public abstract boolean isApplicable(@Nullable FogType p_423608_, Entity p_423467_);
}

package net.minecraft.world.entity;

public interface PlayerRideableJumping extends PlayerRideable {
    void onPlayerJump(int p_21696_);

    boolean canJump();

    void handleStartJump(int p_21695_);

    void handleStopJump();

    default int getJumpCooldown() {
        return 0;
    }

    default float getPlayerJumpPendingScale(int p_454827_) {
        return p_454827_ >= 90 ? 1.0F : 0.4F + 0.4F * p_454827_ / 90.0F;
    }
}

package net.minecraft.server.jsonrpc.internalapi;

import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public interface MinecraftServerSettingsService {
    boolean isAutoSave();

    boolean setAutoSave(boolean p_442986_, ClientInfo p_442752_);

    Difficulty getDifficulty();

    Difficulty setDifficulty(Difficulty p_443123_, ClientInfo p_443115_);

    boolean isEnforceWhitelist();

    boolean setEnforceWhitelist(boolean p_443222_, ClientInfo p_443128_);

    boolean isUsingWhitelist();

    boolean setUsingWhitelist(boolean p_443021_, ClientInfo p_443236_);

    int getMaxPlayers();

    int setMaxPlayers(int p_443551_, ClientInfo p_442556_);

    int getPauseWhenEmptySeconds();

    int setPauseWhenEmptySeconds(int p_443144_, ClientInfo p_442733_);

    int getPlayerIdleTimeout();

    int setPlayerIdleTimeout(int p_442510_, ClientInfo p_442527_);

    boolean allowFlight();

    boolean setAllowFlight(boolean p_443603_, ClientInfo p_442672_);

    int getSpawnProtectionRadius();

    int setSpawnProtectionRadius(int p_443007_, ClientInfo p_443130_);

    String getMotd();

    String setMotd(String p_443253_, ClientInfo p_443336_);

    boolean forceGameMode();

    boolean setForceGameMode(boolean p_443460_, ClientInfo p_443139_);

    GameType getGameMode();

    GameType setGameMode(GameType p_442695_, ClientInfo p_442480_);

    int getViewDistance();

    int setViewDistance(int p_442827_, ClientInfo p_442632_);

    int getSimulationDistance();

    int setSimulationDistance(int p_443502_, ClientInfo p_443318_);

    boolean acceptsTransfers();

    boolean setAcceptsTransfers(boolean p_442537_, ClientInfo p_442727_);

    int getStatusHeartbeatInterval();

    int setStatusHeartbeatInterval(int p_442860_, ClientInfo p_443340_);

    LevelBasedPermissionSet getOperatorUserPermissions();

    LevelBasedPermissionSet setOperatorUserPermissions(LevelBasedPermissionSet p_455091_, ClientInfo p_455457_);

    boolean hidesOnlinePlayers();

    boolean setHidesOnlinePlayers(boolean p_443289_, ClientInfo p_443495_);

    boolean repliesToStatus();

    boolean setRepliesToStatus(boolean p_443407_, ClientInfo p_443163_);

    int getEntityBroadcastRangePercentage();

    int setEntityBroadcastRangePercentage(int p_442989_, ClientInfo p_442943_);
}

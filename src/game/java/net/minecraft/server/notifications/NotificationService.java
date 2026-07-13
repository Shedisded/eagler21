package net.minecraft.server.notifications;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.level.gamerules.GameRule;

public interface NotificationService {
    void playerJoined(ServerPlayer p_443342_);

    void playerLeft(ServerPlayer p_443049_);

    void serverStarted();

    void serverShuttingDown();

    void serverSaveStarted();

    void serverSaveCompleted();

    void serverActivityOccured();

    void playerOped(ServerOpListEntry p_443297_);

    void playerDeoped(ServerOpListEntry p_443410_);

    void playerAddedToAllowlist(NameAndId p_443091_);

    void playerRemovedFromAllowlist(NameAndId p_442791_);

    void ipBanned(IpBanListEntry p_442869_);

    void ipUnbanned(String p_443069_);

    void playerBanned(UserBanListEntry p_442671_);

    void playerUnbanned(NameAndId p_442719_);

    <T> void onGameRuleChanged(GameRule<T> p_460968_, T p_460994_);

    void statusHeartbeat();
}

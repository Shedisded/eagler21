package net.minecraft.server.jsonrpc.internalapi;

import java.util.Collection;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserBanListEntry;

public interface MinecraftBanListService {
    void addUserBan(UserBanListEntry p_442858_, ClientInfo p_442596_);

    void removeUserBan(NameAndId p_442766_, ClientInfo p_442577_);

    Collection<UserBanListEntry> getUserBanEntries();

    Collection<IpBanListEntry> getIpBanEntries();

    void addIpBan(IpBanListEntry p_442810_, ClientInfo p_443614_);

    void clearIpBans(ClientInfo p_443457_);

    void removeIpBan(String p_443138_, ClientInfo p_443258_);

    void clearUserBans(ClientInfo p_449751_);
}

package net.minecraft.server.jsonrpc.internalapi;

import java.util.Collection;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserWhiteListEntry;

public interface MinecraftAllowListService {
    Collection<UserWhiteListEntry> getEntries();

    boolean add(UserWhiteListEntry p_442981_, ClientInfo p_443582_);

    void clear(ClientInfo p_442899_);

    void remove(NameAndId p_443148_, ClientInfo p_442774_);

    void kickUnlistedPlayers(ClientInfo p_442972_);
}

package net.minecraft.server.jsonrpc.internalapi;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.ServerOpListEntry;

public interface MinecraftOperatorListService {
    Collection<ServerOpListEntry> getEntries();

    void op(NameAndId p_442803_, Optional<PermissionLevel> p_442509_, Optional<Boolean> p_443203_, ClientInfo p_442987_);

    void op(NameAndId p_443004_, ClientInfo p_443149_);

    void deop(NameAndId p_443571_, ClientInfo p_442674_);

    void clear(ClientInfo p_442772_);
}

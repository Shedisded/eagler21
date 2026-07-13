package net.minecraft.server.jsonrpc.internalapi;

import java.util.Collection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.level.ServerPlayer;

public interface MinecraftServerStateService {
    boolean isReady();

    boolean saveEverything(boolean p_443106_, boolean p_442701_, boolean p_442958_, ClientInfo p_443145_);

    void halt(boolean p_442815_, ClientInfo p_442720_);

    void sendSystemMessage(Component p_443245_, ClientInfo p_442696_);

    void sendSystemMessage(Component p_442786_, boolean p_443547_, Collection<ServerPlayer> p_442836_, ClientInfo p_442707_);

    void broadcastSystemMessage(Component p_442623_, boolean p_442925_, ClientInfo p_443515_);
}

package net.minecraft.server.jsonrpc.internalapi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public interface MinecraftPlayerListService {
    List<ServerPlayer> getPlayers();

    @Nullable ServerPlayer getPlayer(UUID p_442718_);

    default CompletableFuture<Optional<NameAndId>> getUser(Optional<UUID> p_443010_, Optional<String> p_443013_) {
        if (p_443010_.isPresent()) {
            Optional<NameAndId> optional = this.getCachedUserById(p_443010_.get());
            return optional.isPresent()
                ? CompletableFuture.completedFuture(optional)
                : CompletableFuture.supplyAsync(() -> this.fetchUserById(p_443010_.get()), Util.nonCriticalIoPool());
        } else {
            return p_443013_.isPresent()
                ? CompletableFuture.supplyAsync(() -> this.fetchUserByName(p_443013_.get()), Util.nonCriticalIoPool())
                : CompletableFuture.completedFuture(Optional.empty());
        }
    }

    Optional<NameAndId> fetchUserByName(String p_449848_);

    Optional<NameAndId> fetchUserById(UUID p_449966_);

    Optional<NameAndId> getCachedUserById(UUID p_449421_);

    Optional<ServerPlayer> getPlayer(Optional<UUID> p_442948_, Optional<String> p_443177_);

    List<ServerPlayer> getPlayersWithAddress(String p_443310_);

    @Nullable ServerPlayer getPlayerByName(String p_442749_);

    void remove(ServerPlayer p_443301_, ClientInfo p_443420_);
}

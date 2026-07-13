package net.minecraft.server.players;

import java.util.Optional;
import java.util.UUID;

public interface UserNameToIdResolver {
    void add(NameAndId p_434896_);

    Optional<NameAndId> get(String p_435365_);

    Optional<NameAndId> get(UUID p_432917_);

    void resolveOfflineUsers(boolean p_435952_);

    void save();
}

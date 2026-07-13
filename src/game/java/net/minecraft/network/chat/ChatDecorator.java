package net.minecraft.network.chat;

import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ChatDecorator {
    ChatDecorator PLAIN = (p_300715_, p_300716_) -> p_300716_;

    Component decorate(@Nullable ServerPlayer p_236962_, Component p_236963_);
}

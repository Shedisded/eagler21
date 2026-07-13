package net.minecraft.client.gui.screens.dialog;

import java.util.Optional;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.Dialog;
import org.jspecify.annotations.Nullable;

public interface DialogConnectionAccess {
    void disconnect(Component p_451514_);

    void runCommand(String p_427462_, @Nullable Screen p_427380_);

    void openDialog(Holder<Dialog> p_427479_, @Nullable Screen p_428375_);

    void sendCustomAction(Identifier p_467953_, Optional<Tag> p_427294_);

    ServerLinks serverLinks();
}

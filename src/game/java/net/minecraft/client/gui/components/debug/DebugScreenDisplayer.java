package net.minecraft.client.gui.components.debug;

import java.util.Collection;
import net.minecraft.resources.Identifier;

public interface DebugScreenDisplayer {
    void addPriorityLine(String p_435623_);

    void addLine(String p_433177_);

    void addToGroup(Identifier p_467081_, Collection<String> p_469678_);

    void addToGroup(Identifier p_468214_, String p_467946_);
}

package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.realmsclient.dto.RealmsServer;

public interface RealmsConfigurationTab {
    void updateData(RealmsServer p_419665_);

    default void onSelected(RealmsServer p_419724_) {
    }

    default void onDeselected(RealmsServer p_420048_) {
    }
}

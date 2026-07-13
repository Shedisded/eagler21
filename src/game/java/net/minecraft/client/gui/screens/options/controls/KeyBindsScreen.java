package net.minecraft.client.gui.screens.options.controls;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class KeyBindsScreen extends OptionsSubScreen {
    private static final Component TITLE = Component.translatable("controls.keybinds.title");
    public @Nullable KeyMapping selectedKey;
    public long lastKeySelection;
    private KeyBindsList keyBindsList;
    private Button resetButton;

    public KeyBindsScreen(Screen p_344996_, Options p_344771_) {
        super(p_344996_, p_344771_, TITLE);
    }

    @Override
    protected void addContents() {
        this.keyBindsList = this.layout.addToContents(new KeyBindsList(this, this.minecraft));
    }

    @Override
    protected void addOptions() {
    }

    @Override
    protected void addFooter() {
        this.resetButton = Button.builder(Component.translatable("controls.resetAll"), p_346345_ -> {
            for (KeyMapping keymapping : this.options.keyMappings) {
                keymapping.setKey(keymapping.getDefaultKey());
            }

            this.keyBindsList.resetMappingAndUpdateButtons();
        }).build();
        LinearLayout linearlayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        linearlayout.addChild(this.resetButton);
        linearlayout.addChild(Button.builder(CommonComponents.GUI_DONE, p_432229_ -> this.onClose()).build());
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.keyBindsList.updateSize(this.width, this.layout);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent p_445878_, boolean p_434872_) {
        if (this.selectedKey != null) {
            this.selectedKey.setKey(InputConstants.Type.MOUSE.getOrCreate(p_445878_.button()));
            this.selectedKey = null;
            this.keyBindsList.resetMappingAndUpdateButtons();
            return true;
        } else {
            return super.mouseClicked(p_445878_, p_434872_);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent p_446842_) {
        if (this.selectedKey != null) {
            if (p_446842_.isEscape()) {
                this.selectedKey.setKey(InputConstants.UNKNOWN);
            } else {
                this.selectedKey.setKey(InputConstants.getKey(p_446842_));
            }

            this.selectedKey = null;
            this.lastKeySelection = Util.getMillis();
            this.keyBindsList.resetMappingAndUpdateButtons();
            return true;
        } else {
            return super.keyPressed(p_446842_);
        }
    }

    @Override
    public void render(GuiGraphics p_346209_, int p_344846_, int p_346350_, float p_345601_) {
        super.render(p_346209_, p_344846_, p_346350_, p_345601_);
        boolean flag = false;

        for (KeyMapping keymapping : this.options.keyMappings) {
            if (!keymapping.isDefault()) {
                flag = true;
                break;
            }
        }

        this.resetButton.active = flag;
    }
}

package net.minecraft.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class KeyMapping implements Comparable<KeyMapping> {
    private static final Map<String, KeyMapping> ALL = Maps.newHashMap();
    private static final Map<InputConstants.Key, List<KeyMapping>> MAP = Maps.newHashMap();
    private final String name;
    private final InputConstants.Key defaultKey;
    private final KeyMapping.Category category;
    protected InputConstants.Key key;
    private boolean isDown;
    private int clickCount;
    private final int order;

    public static void click(InputConstants.Key p_90836_) {
        forAllKeyMappings(p_90836_, p_445128_ -> p_445128_.clickCount++);
    }

    public static void set(InputConstants.Key p_90838_, boolean p_90839_) {
        forAllKeyMappings(p_90838_, p_445126_ -> p_445126_.setDown(p_90839_));
    }

    private static void forAllKeyMappings(InputConstants.Key p_446544_, Consumer<KeyMapping> p_446545_) {
        List<KeyMapping> list = MAP.get(p_446544_);
        if (list != null && !list.isEmpty()) {
            for (KeyMapping keymapping : list) {
                p_446545_.accept(keymapping);
            }
        }
    }

    public static void setAll() {
        Window window = Minecraft.getInstance().getWindow();

        for (KeyMapping keymapping : ALL.values()) {
            if (keymapping.shouldSetOnIngameFocus()) {
                keymapping.setDown(InputConstants.isKeyDown(window, keymapping.key.getValue()));
            }
        }
    }

    public static void releaseAll() {
        for (KeyMapping keymapping : ALL.values()) {
            keymapping.release();
        }
    }

    public static void restoreToggleStatesOnScreenClosed() {
        for (KeyMapping keymapping : ALL.values()) {
            if (keymapping instanceof ToggleKeyMapping togglekeymapping && togglekeymapping.shouldRestoreStateOnScreenClosed()) {
                togglekeymapping.setDown(true);
            }
        }
    }

    public static void resetToggleKeys() {
        for (KeyMapping keymapping : ALL.values()) {
            if (keymapping instanceof ToggleKeyMapping togglekeymapping) {
                togglekeymapping.reset();
            }
        }
    }

    public static void resetMapping() {
        MAP.clear();

        for (KeyMapping keymapping : ALL.values()) {
            keymapping.registerMapping(keymapping.key);
        }
    }

    public KeyMapping(String p_90821_, int p_90822_, KeyMapping.Category p_445591_) {
        this(p_90821_, InputConstants.Type.KEYSYM, p_90822_, p_445591_);
    }

    public KeyMapping(String p_90825_, InputConstants.Type p_90826_, int p_90827_, KeyMapping.Category p_447123_) {
        this(p_90825_, p_90826_, p_90827_, p_447123_, 0);
    }

    public KeyMapping(String p_456154_, InputConstants.Type p_455527_, int p_455544_, KeyMapping.Category p_455587_, int p_455420_) {
        this.name = p_456154_;
        this.key = p_455527_.getOrCreate(p_455544_);
        this.defaultKey = this.key;
        this.category = p_455587_;
        this.order = p_455420_;
        ALL.put(p_456154_, this);
        this.registerMapping(this.key);
    }

    public boolean isDown() {
        return this.isDown;
    }

    public KeyMapping.Category getCategory() {
        return this.category;
    }

    public boolean consumeClick() {
        if (this.clickCount == 0) {
            return false;
        } else {
            this.clickCount--;
            return true;
        }
    }

    protected void release() {
        this.clickCount = 0;
        this.setDown(false);
    }

    protected boolean shouldSetOnIngameFocus() {
        return this.key.getType() == InputConstants.Type.KEYSYM && this.key.getValue() != InputConstants.UNKNOWN.getValue();
    }

    public String getName() {
        return this.name;
    }

    public InputConstants.Key getDefaultKey() {
        return this.defaultKey;
    }

    public void setKey(InputConstants.Key p_90849_) {
        this.key = p_90849_;
    }

    public int compareTo(KeyMapping p_90841_) {
        if (this.category == p_90841_.category) {
            return this.order == p_90841_.order ? I18n.get(this.name).compareTo(I18n.get(p_90841_.name)) : Integer.compare(this.order, p_90841_.order);
        } else {
            return Integer.compare(KeyMapping.Category.SORT_ORDER.indexOf(this.category), KeyMapping.Category.SORT_ORDER.indexOf(p_90841_.category));
        }
    }

    public static Supplier<Component> createNameSupplier(String p_90843_) {
        KeyMapping keymapping = ALL.get(p_90843_);
        return keymapping == null ? () -> Component.translatable(p_90843_) : keymapping::getTranslatedKeyMessage;
    }

    public boolean same(KeyMapping p_90851_) {
        return this.key.equals(p_90851_.key);
    }

    public boolean isUnbound() {
        return this.key.equals(InputConstants.UNKNOWN);
    }

    public boolean matches(KeyEvent p_446478_) {
        return p_446478_.key() == InputConstants.UNKNOWN.getValue()
            ? this.key.getType() == InputConstants.Type.SCANCODE && this.key.getValue() == p_446478_.scancode()
            : this.key.getType() == InputConstants.Type.KEYSYM && this.key.getValue() == p_446478_.key();
    }

    public boolean matchesMouse(MouseButtonEvent p_446005_) {
        return this.key.getType() == InputConstants.Type.MOUSE && this.key.getValue() == p_446005_.button();
    }

    public Component getTranslatedKeyMessage() {
        return this.key.getDisplayName();
    }

    public boolean isDefault() {
        return this.key.equals(this.defaultKey);
    }

    public String saveString() {
        return this.key.getName();
    }

    public void setDown(boolean p_90846_) {
        this.isDown = p_90846_;
    }

    private void registerMapping(InputConstants.Key p_446356_) {
        MAP.computeIfAbsent(p_446356_, p_445127_ -> new ArrayList<>()).add(this);
    }

    public static @Nullable KeyMapping get(String p_389468_) {
        return ALL.get(p_389468_);
    }

    public record Category(Identifier id) {
        static final List<KeyMapping.Category> SORT_ORDER = new ArrayList<>();
        public static final KeyMapping.Category MOVEMENT = register("movement");
        public static final KeyMapping.Category MISC = register("misc");
        public static final KeyMapping.Category MULTIPLAYER = register("multiplayer");
        public static final KeyMapping.Category GAMEPLAY = register("gameplay");
        public static final KeyMapping.Category INVENTORY = register("inventory");
        public static final KeyMapping.Category CREATIVE = register("creative");
        public static final KeyMapping.Category SPECTATOR = register("spectator");
        public static final KeyMapping.Category DEBUG = register("debug");

        private static KeyMapping.Category register(String p_449101_) {
            return register(Identifier.withDefaultNamespace(p_449101_));
        }

        public static KeyMapping.Category register(Identifier p_468881_) {
            KeyMapping.Category keymapping$category = new KeyMapping.Category(p_468881_);
            if (SORT_ORDER.contains(keymapping$category)) {
                throw new IllegalArgumentException(String.format(Locale.ROOT, "Category '%s' is already registered.", p_468881_));
            } else {
                SORT_ORDER.add(keymapping$category);
                return keymapping$category;
            }
        }

        public Component label() {
            return Component.translatable(this.id.toLanguageKey("key.category"));
        }
    }
}

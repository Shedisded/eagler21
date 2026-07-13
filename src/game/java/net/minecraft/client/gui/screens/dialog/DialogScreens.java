package net.minecraft.client.gui.screens.dialog;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.ConfirmationDialog;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogListDialog;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.ServerLinksDialog;
import org.jspecify.annotations.Nullable;

public class DialogScreens {
    private static final Map<MapCodec<? extends Dialog>, DialogScreens.Factory<?>> FACTORIES = new HashMap<>();

    private static <T extends Dialog> void register(MapCodec<T> p_425747_, DialogScreens.Factory<? super T> p_425724_) {
        FACTORIES.put(p_425747_, p_425724_);
    }

    public static <T extends Dialog> @Nullable DialogScreen<T> createFromData(T p_426275_, @Nullable Screen p_425938_, DialogConnectionAccess p_427500_) {
        DialogScreens.Factory<T> factory = (DialogScreens.Factory<T>)FACTORIES.get(p_426275_.codec());
        return factory != null ? factory.create(p_425938_, p_426275_, p_427500_) : null;
    }

    public static void bootstrap() {
        register(ConfirmationDialog.MAP_CODEC, SimpleDialogScreen::new);
        register(NoticeDialog.MAP_CODEC, SimpleDialogScreen::new);
        register(DialogListDialog.MAP_CODEC, DialogListDialogScreen::new);
        register(MultiActionDialog.MAP_CODEC, MultiButtonDialogScreen::new);
        register(ServerLinksDialog.MAP_CODEC, ServerLinksDialogScreen::new);
    }

    @FunctionalInterface
    public interface Factory<T extends Dialog> {
        DialogScreen<T> create(@Nullable Screen p_426168_, T p_426213_, DialogConnectionAccess p_427412_);
    }
}

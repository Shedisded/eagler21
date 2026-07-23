package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackDetector;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.Util;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PackSelectionScreen extends Screen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component AVAILABLE_TITLE = Component.translatable("pack.available.title");
    private static final Component SELECTED_TITLE = Component.translatable("pack.selected.title");
    private static final Component OPEN_PACK_FOLDER_TITLE = Component.translatable("pack.openFolder");
    private static final Component SEARCH = Component.translatable("gui.packSelection.search").withStyle(EditBox.SEARCH_HINT_STYLE);
    private static final int LIST_WIDTH = 200;
    private static final int HEADER_ELEMENT_SPACING = 4;
    private static final int SEARCH_BOX_HEIGHT = 15;
    private static final Component DRAG_AND_DROP = Component.translatable("pack.dropInfo").withStyle(ChatFormatting.GRAY);
    private static final Component DIRECTORY_BUTTON_TOOLTIP = Component.translatable("pack.folderInfo");
    private static final int RELOAD_COOLDOWN = 20;
    private static final Identifier DEFAULT_ICON = Identifier.withDefaultNamespace("textures/misc/unknown_pack.png");
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final PackSelectionModel model;
    private PackSelectionScreen.@Nullable Watcher watcher;
    private long ticksToReload;
    private @Nullable TransferableSelectionList availablePackList;
    private @Nullable TransferableSelectionList selectedPackList;
    private @Nullable EditBox search;
    private final Path packDir;
    private @Nullable Button doneButton;
    private final Map<String, Identifier> packIcons = Maps.newHashMap();

    public PackSelectionScreen(PackRepository p_275398_, Consumer<PackRepository> p_275659_, Path p_275522_, Component p_275337_) {
        super(p_275337_);
        this.model = new PackSelectionModel(this::populateLists, this::getPackIcon, p_275398_, p_275659_);
        this.packDir = p_275522_;
        this.watcher = PackSelectionScreen.Watcher.create(p_275522_);
    }

    @Override
    public void onClose() {
        this.model.commit();
        this.closeWatcher();
    }

    private void closeWatcher() {
        if (this.watcher != null) {
            try {
                this.watcher.close();
                this.watcher = null;
            } catch (Exception exception) {
            }
        }
    }

    @Override
    protected void init() {
        this.layout.setHeaderHeight(4 + 9 + 4 + 9 + 4 + 15 + 4);
        LinearLayout linearlayout = this.layout.addToHeader(LinearLayout.vertical().spacing(4));
        linearlayout.defaultCellSetting().alignHorizontallyCenter();
        linearlayout.addChild(new StringWidget(this.getTitle(), this.font));
        linearlayout.addChild(new StringWidget(DRAG_AND_DROP, this.font));
        this.search = linearlayout.addChild(new EditBox(this.font, 0, 0, 200, 15, Component.empty()));
        this.search.setHint(SEARCH);
        this.search.setResponder(this::updateFilteredEntries);
        this.availablePackList = this.layout.addToContents(new TransferableSelectionList(this.minecraft, this, 200, this.height - 66, AVAILABLE_TITLE));
        this.selectedPackList = this.layout.addToContents(new TransferableSelectionList(this.minecraft, this, 200, this.height - 66, SELECTED_TITLE));
        LinearLayout linearlayout1 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        linearlayout1.addChild(
            Button.builder(OPEN_PACK_FOLDER_TITLE, p_465534_ -> Util.getPlatform().openPath(this.packDir))
                .tooltip(Tooltip.create(DIRECTORY_BUTTON_TOOLTIP))
                .build()
        );
        this.doneButton = linearlayout1.addChild(Button.builder(CommonComponents.GUI_DONE, p_100036_ -> this.onClose()).build());
        this.layout.visitWidgets(p_329736_ -> {
            AbstractWidget abstractwidget = this.addRenderableWidget(p_329736_);
        });
        this.repositionElements();
        this.reload();
    }

    @Override
    protected void setInitialFocus() {
        if (this.search != null) {
            this.setInitialFocus(this.search);
        } else {
            super.setInitialFocus();
        }
    }

    private void updateFilteredEntries(String p_449270_) {
        this.filterEntries(p_449270_, this.model.getSelected(), this.selectedPackList);
        this.filterEntries(p_449270_, this.model.getUnselected(), this.availablePackList);
    }

    private void filterEntries(String p_449047_, Stream<PackSelectionModel.Entry> p_449704_, @Nullable TransferableSelectionList p_449110_) {
        if (p_449110_ != null) {
            String s = p_449047_.toLowerCase(Locale.ROOT);
            Stream<PackSelectionModel.Entry> stream = p_449704_.filter(
                p_448690_ -> p_449047_.isBlank()
                    || p_448690_.getId().toLowerCase(Locale.ROOT).contains(s)
                    || p_448690_.getTitle().getString().toLowerCase(Locale.ROOT).contains(s)
                    || p_448690_.getDescription().getString().toLowerCase(Locale.ROOT).contains(s)
            );
            p_449110_.updateList(stream, null);
        }
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.availablePackList != null) {
            this.availablePackList.updateSizeAndPosition(200, this.layout.getContentHeight(), this.width / 2 - 15 - 200, this.layout.getHeaderHeight());
        }

        if (this.selectedPackList != null) {
            this.selectedPackList.updateSizeAndPosition(200, this.layout.getContentHeight(), this.width / 2 + 15, this.layout.getHeaderHeight());
        }
    }

    @Override
    public void tick() {
        if (this.watcher != null) {
            try {
                if (this.watcher.pollForChanges()) {
                    this.ticksToReload = 20L;
                }
            } catch (IOException ioexception) {
                LOGGER.warn("Failed to poll for directory {} changes, stopping", this.packDir);
                this.closeWatcher();
            }
        }

        if (this.ticksToReload > 0L && --this.ticksToReload == 0L) {
            this.reload();
        }
    }

    private void populateLists(PackSelectionModel.@Nullable EntryBase p_440233_) {
        if (this.selectedPackList != null) {
            this.selectedPackList.updateList(this.model.getSelected(), p_440233_);
        }

        if (this.availablePackList != null) {
            this.availablePackList.updateList(this.model.getUnselected(), p_440233_);
        }

        if (this.search != null) {
            this.updateFilteredEntries(this.search.getValue());
        }

        if (this.doneButton != null) {
            this.doneButton.active = !this.selectedPackList.children().isEmpty();
        }
    }

    private void reload() {
        this.model.findNewPacks();
        this.populateLists(null);
        this.ticksToReload = 0L;
        this.packIcons.clear();
    }

    protected static void copyPacks(Minecraft p_100000_, List<Path> p_100001_, Path p_100002_) {
        MutableBoolean mutableboolean = new MutableBoolean();
        p_100001_.forEach(p_170009_ -> {
            try (Stream<Path> stream = Files.walk(p_170009_)) {
                stream.forEach(p_465540_ -> {
                    try {
                        Util.copyBetweenDirs(p_170009_.getParent(), p_100002_, p_465540_);
                    } catch (IOException ioexception1) {
                        LOGGER.warn("Failed to copy datapack file  from {} to {}", p_465540_, p_100002_, ioexception1);
                        mutableboolean.setTrue();
                    }
                });
            } catch (IOException ioexception) {
                LOGGER.warn("Failed to copy datapack file from {} to {}", p_170009_, p_100002_);
                mutableboolean.setTrue();
            }
        });
        if (mutableboolean.isTrue()) {
            SystemToast.onPackCopyFailure(p_100000_, p_100002_.toString());
        }
    }

    @Override
    public void onFilesDrop(List<Path> p_100029_) {
        String s = extractPackNames(p_100029_).collect(Collectors.joining(", "));
        this.minecraft
            .setScreen(
                new ConfirmScreen(
                    p_293606_ -> {
                        if (p_293606_) {
                            List<Path> list = new ArrayList<>(p_100029_.size());
                            Set<Path> set = new HashSet<>(p_100029_);
                            PackDetector<Path> packdetector = new PackDetector<Path>(this.minecraft.directoryValidator()) {
                                protected Path createZipPack(Path p_294508_) {
                                    return p_294508_;
                                }

                                protected Path createDirectoryPack(Path p_296022_) {
                                    return p_296022_;
                                }
                            };
                            List<ForbiddenSymlinkInfo> list1 = new ArrayList<>();

                            for (Path path : p_100029_) {
                                try {
                                    Path path1 = packdetector.detectPackResources(path, list1);
                                    if (path1 == null) {
                                        LOGGER.warn("Path {} does not seem like pack", path);
                                    } else {
                                        list.add(path1);
                                        set.remove(path1);
                                    }
                                } catch (IOException ioexception) {
                                    LOGGER.warn("Failed to check {} for packs", path, ioexception);
                                }
                            }

                            if (!list1.isEmpty()) {
                                this.minecraft.setScreen(NoticeWithLinkScreen.createPackSymlinkWarningScreen(() -> this.minecraft.setScreen(this)));
                                return;
                            }

                            if (!list.isEmpty()) {
                                copyPacks(this.minecraft, list, this.packDir);
                                this.reload();
                            }

                            if (!set.isEmpty()) {
                                String s1 = extractPackNames(set).collect(Collectors.joining(", "));
                                this.minecraft
                                    .setScreen(
                                        new AlertScreen(
                                            () -> this.minecraft.setScreen(this),
                                            Component.translatable("pack.dropRejected.title"),
                                            Component.translatable("pack.dropRejected.message", s1)
                                        )
                                    );
                                return;
                            }
                        }

                        this.minecraft.setScreen(this);
                    },
                    Component.translatable("pack.dropConfirm"),
                    Component.literal(s)
                )
            );
    }

    private static Stream<String> extractPackNames(Collection<Path> p_296218_) {
        return p_296218_.stream().map(Path::getFileName).map(Path::toString);
    }

    private Identifier loadPackIcon(TextureManager p_100017_, Pack p_100018_) {
        try {
            Identifier identifier1;
            try (PackResources packresources = p_100018_.open()) {
                IoSupplier<InputStream> iosupplier = packresources.getRootResource("pack.png");
                if (iosupplier == null) {
                    return DEFAULT_ICON;
                }

                String s = p_100018_.getId();
                Identifier identifier = Identifier.withDefaultNamespace(
                    "pack/" + Util.sanitizeName(s, Identifier::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(s) + "/icon"
                );

                try (InputStream inputstream = iosupplier.get()) {
                    NativeImage nativeimage = NativeImage.read(inputstream);
                    p_100017_.register(identifier, new DynamicTexture(identifier::toString, nativeimage));
                    identifier1 = identifier;
                }
            }

            return identifier1;
        } catch (Exception exception) {
            LOGGER.warn("Failed to load icon from pack {}", p_100018_.getId(), exception);
            return DEFAULT_ICON;
        }
    }

    private Identifier getPackIcon(Pack p_99990_) {
        return this.packIcons.computeIfAbsent(p_99990_.getId(), p_465536_ -> this.loadPackIcon(this.minecraft.getTextureManager(), p_99990_));
    }

    // ponytail: pack-folder watching (java.nio.file.WatchService) is meaningless on the
    // browser VFS and drags in the whole Watch API (WatchService/Key/Event/Watchable/
    // StandardWatchEventKinds). Disabled: create() returns null so PackSelectionScreen never
    // polls; the bodies are gutted to keep the Watch API out of the TeaVM reachable set.
    static class Watcher implements AutoCloseable {
        private Watcher(Path p_250327_) {
        }

        public static PackSelectionScreen.@Nullable Watcher create(Path p_252119_) {
            return null;
        }

        public boolean pollForChanges() throws IOException {
            return false;
        }

        @Override
        public void close() throws IOException {
        }
    }
}

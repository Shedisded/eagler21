package net.minecraft.server.packs.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.Error;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener<T> extends SimplePreparableReloadListener<Map<Identifier, T>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DynamicOps<JsonElement> ops;
    private final Codec<T> codec;
    private final FileToIdConverter lister;

    protected SimpleJsonResourceReloadListener(HolderLookup.Provider p_386840_, Codec<T> p_371393_, ResourceKey<? extends Registry<T>> p_386822_) {
        this(p_386840_.createSerializationContext(JsonOps.INSTANCE), p_371393_, FileToIdConverter.registry(p_386822_));
    }

    protected SimpleJsonResourceReloadListener(Codec<T> p_371214_, FileToIdConverter p_388264_) {
        this(JsonOps.INSTANCE, p_371214_, p_388264_);
    }

    private SimpleJsonResourceReloadListener(DynamicOps<JsonElement> p_387563_, Codec<T> p_371490_, FileToIdConverter p_387322_) {
        this.ops = p_387563_;
        this.codec = p_371490_;
        this.lister = p_387322_;
    }

    protected Map<Identifier, T> prepare(ResourceManager p_10771_, ProfilerFiller p_10772_) {
        Map<Identifier, T> map = new HashMap<>();
        scanDirectory(p_10771_, this.lister, this.ops, this.codec, map);
        return map;
    }

    public static <T> void scanDirectory(
        ResourceManager p_386974_,
        ResourceKey<? extends Registry<T>> p_388878_,
        DynamicOps<JsonElement> p_388402_,
        Codec<T> p_387608_,
        Map<Identifier, T> p_386495_
    ) {
        scanDirectory(p_386974_, FileToIdConverter.registry(p_388878_), p_388402_, p_387608_, p_386495_);
    }

    public static <T> void scanDirectory(
        ResourceManager p_279308_, FileToIdConverter p_387906_, DynamicOps<JsonElement> p_371830_, Codec<T> p_371493_, Map<Identifier, T> p_279404_
    ) {
        for (Entry<Identifier, Resource> entry : p_387906_.listMatchingResources(p_279308_).entrySet()) {
            Identifier identifier = entry.getKey();
            Identifier identifier1 = p_387906_.fileToId(identifier);

            try (Reader reader = entry.getValue().openAsReader()) {
                p_371493_.parse(p_371830_, StrictJsonParser.parse(reader)).ifSuccess(p_371454_ -> {
                    if (p_279404_.putIfAbsent(identifier1, (T)p_371454_) != null) {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + identifier1);
                    }
                }).ifError(p_371566_ -> LOGGER.error("Couldn't parse data file '{}' from '{}': {}", identifier1, identifier, p_371566_));
            } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
                LOGGER.error("Couldn't parse data file '{}' from '{}'", identifier1, identifier, jsonparseexception);
            }
        }
    }
}

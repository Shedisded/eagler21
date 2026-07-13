package net.minecraft.client.data.models;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelProvider implements DataProvider {
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider itemInfoPathProvider;
    private final PackOutput.PathProvider modelPathProvider;

    public ModelProvider(PackOutput p_388260_) {
        this.blockStatePathProvider = p_388260_.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.itemInfoPathProvider = p_388260_.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.modelPathProvider = p_388260_.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput p_387857_) {
        ModelProvider.ItemInfoCollector modelprovider$iteminfocollector = new ModelProvider.ItemInfoCollector();
        ModelProvider.BlockStateGeneratorCollector modelprovider$blockstategeneratorcollector = new ModelProvider.BlockStateGeneratorCollector();
        ModelProvider.SimpleModelCollector modelprovider$simplemodelcollector = new ModelProvider.SimpleModelCollector();
        new BlockModelGenerators(modelprovider$blockstategeneratorcollector, modelprovider$iteminfocollector, modelprovider$simplemodelcollector).run();
        new ItemModelGenerators(modelprovider$iteminfocollector, modelprovider$simplemodelcollector).run();
        modelprovider$blockstategeneratorcollector.validate();
        modelprovider$iteminfocollector.finalizeAndValidate();
        return CompletableFuture.allOf(
            modelprovider$blockstategeneratorcollector.save(p_387857_, this.blockStatePathProvider),
            modelprovider$simplemodelcollector.save(p_387857_, this.modelPathProvider),
            modelprovider$iteminfocollector.save(p_387857_, this.itemInfoPathProvider)
        );
    }

    @Override
    public final String getName() {
        return "Model Definitions";
    }

    static class BlockStateGeneratorCollector implements Consumer<BlockModelDefinitionGenerator> {
        private final Map<Block, BlockModelDefinitionGenerator> generators = new HashMap<>();

        public void accept(BlockModelDefinitionGenerator p_405192_) {
            Block block = p_405192_.block();
            BlockModelDefinitionGenerator blockmodeldefinitiongenerator = this.generators.put(block, p_405192_);
            if (blockmodeldefinitiongenerator != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        }

        public void validate() {
            Stream<Holder.Reference<Block>> stream = BuiltInRegistries.BLOCK.listElements().filter(p_388333_ -> true);
            List<Identifier> list = stream.filter(p_386843_ -> !this.generators.containsKey(p_386843_.value()))
                .map(p_465437_ -> p_465437_.key().identifier())
                .toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Missing blockstate definitions for: " + list);
            }
        }

        public CompletableFuture<?> save(CachedOutput p_388014_, PackOutput.PathProvider p_388192_) {
            Map<Block, BlockModelDefinition> map = Maps.transformValues(this.generators, BlockModelDefinitionGenerator::create);
            Function<Block, Path> function = p_465439_ -> p_388192_.json(p_465439_.builtInRegistryHolder().key().identifier());
            return DataProvider.saveAll(p_388014_, BlockModelDefinition.CODEC, function, map);
        }
    }

    static class ItemInfoCollector implements ItemModelOutput {
        private final Map<Item, ClientItem> itemInfos = new HashMap<>();
        private final Map<Item, Item> copies = new HashMap<>();

        @Override
        public void accept(Item p_387063_, ItemModel.Unbaked p_388578_, ClientItem.Properties p_455547_) {
            this.register(p_387063_, new ClientItem(p_388578_, p_455547_));
        }

        private void register(Item p_388205_, ClientItem p_388233_) {
            ClientItem clientitem = this.itemInfos.put(p_388205_, p_388233_);
            if (clientitem != null) {
                throw new IllegalStateException("Duplicate item model definition for " + p_388205_);
            }
        }

        @Override
        public void copy(Item p_386920_, Item p_386789_) {
            this.copies.put(p_386789_, p_386920_);
        }

        public void finalizeAndValidate() {
            BuiltInRegistries.ITEM.forEach(p_465440_ -> {
                if (!this.copies.containsKey(p_465440_)) {
                    if (p_465440_ instanceof BlockItem blockitem && !this.itemInfos.containsKey(blockitem)) {
                        Identifier identifier = ModelLocationUtils.getModelLocation(blockitem.getBlock());
                        this.accept(blockitem, ItemModelUtils.plainModel(identifier));
                    }
                }
            });
            this.copies.forEach((p_386494_, p_386575_) -> {
                ClientItem clientitem = this.itemInfos.get(p_386575_);
                if (clientitem == null) {
                    throw new IllegalStateException("Missing donor: " + p_386575_ + " -> " + p_386494_);
                } else {
                    this.register(p_386494_, clientitem);
                }
            });
            List<Identifier> list = BuiltInRegistries.ITEM
                .listElements()
                .filter(p_388636_ -> !this.itemInfos.containsKey(p_388636_.value()))
                .map(p_465441_ -> p_465441_.key().identifier())
                .toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Missing item model definitions for: " + list);
            }
        }

        public CompletableFuture<?> save(CachedOutput p_387552_, PackOutput.PathProvider p_388501_) {
            return DataProvider.saveAll(
                p_387552_, ClientItem.CODEC, p_465443_ -> p_388501_.json(p_465443_.builtInRegistryHolder().key().identifier()), this.itemInfos
            );
        }
    }

    static class SimpleModelCollector implements BiConsumer<Identifier, ModelInstance> {
        private final Map<Identifier, ModelInstance> models = new HashMap<>();

        public void accept(Identifier p_467041_, ModelInstance p_388119_) {
            Supplier<JsonElement> supplier = this.models.put(p_467041_, p_388119_);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model definition for " + p_467041_);
            }
        }

        public CompletableFuture<?> save(CachedOutput p_386795_, PackOutput.PathProvider p_388673_) {
            return DataProvider.saveAll(p_386795_, Supplier::get, p_388673_::json, this.models);
        }
    }
}

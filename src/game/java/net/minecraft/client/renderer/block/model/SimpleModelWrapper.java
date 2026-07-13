package net.minecraft.client.renderer.block.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record SimpleModelWrapper(QuadCollection quads, boolean useAmbientOcclusion, TextureAtlasSprite particleIcon) implements BlockModelPart {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static BlockModelPart bake(ModelBaker p_405335_, Identifier p_468685_, ModelState p_405869_) {
        ResolvedModel resolvedmodel = p_405335_.getModel(p_468685_);
        TextureSlots textureslots = resolvedmodel.getTopTextureSlots();
        boolean flag = resolvedmodel.getTopAmbientOcclusion();
        TextureAtlasSprite textureatlassprite = resolvedmodel.resolveParticleSprite(textureslots, p_405335_);
        QuadCollection quadcollection = resolvedmodel.bakeTopGeometry(textureslots, p_405335_, p_405869_);
        Multimap<Identifier, Identifier> multimap = null;

        for (BakedQuad bakedquad : quadcollection.getAll()) {
            TextureAtlasSprite textureatlassprite1 = bakedquad.sprite();
            if (!textureatlassprite1.atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS)) {
                if (multimap == null) {
                    multimap = HashMultimap.create();
                }

                multimap.put(textureatlassprite1.atlasLocation(), textureatlassprite1.contents().name());
            }
        }

        if (multimap != null) {
            LOGGER.warn("Rejecting block model {}, since it contains sprites from outside of supported atlas: {}", p_468685_, multimap);
            return p_405335_.missingBlockModelPart();
        } else {
            return new SimpleModelWrapper(quadcollection, flag, textureatlassprite);
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction p_405263_) {
        return this.quads.getQuads(p_405263_);
    }
}

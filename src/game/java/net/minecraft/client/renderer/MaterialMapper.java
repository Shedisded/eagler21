package net.minecraft.client.renderer;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.Identifier;

public record MaterialMapper(Identifier sheet, String prefix) {
    public Material apply(Identifier p_468142_) {
        return new Material(this.sheet, p_468142_.withPrefix(this.prefix + "/"));
    }

    public Material defaultNamespaceApply(String p_400080_) {
        return this.apply(Identifier.withDefaultNamespace(p_400080_));
    }
}

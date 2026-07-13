package net.minecraft.client.resources.model;

import net.minecraft.resources.Identifier;

public interface ResolvableModel {
    void resolveDependencies(ResolvableModel.Resolver p_387087_);

    public interface Resolver {
        void markDependency(Identifier p_468817_);
    }
}

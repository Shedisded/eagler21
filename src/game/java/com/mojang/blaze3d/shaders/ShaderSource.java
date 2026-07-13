package com.mojang.blaze3d.shaders;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ShaderSource {
    @Nullable String get(Identifier p_467157_, ShaderType p_461026_);
}

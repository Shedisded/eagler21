package com.mojang.blaze3d.shaders;

public enum UniformType {
    UNIFORM_BUFFER("ubo"),
    TEXEL_BUFFER("utb");

    final String name;

    private UniformType(String p_410816_) {
        this.name = p_410816_;
    }
}

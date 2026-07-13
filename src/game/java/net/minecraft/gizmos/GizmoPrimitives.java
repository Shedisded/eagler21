package net.minecraft.gizmos;

import net.minecraft.world.phys.Vec3;

public interface GizmoPrimitives {
    void addPoint(Vec3 p_455698_, int p_454955_, float p_455274_);

    void addLine(Vec3 p_455246_, Vec3 p_454683_, int p_454903_, float p_454949_);

    void addTriangleFan(Vec3[] p_456034_, int p_454984_);

    void addQuad(Vec3 p_456009_, Vec3 p_456094_, Vec3 p_456104_, Vec3 p_454650_, int p_455891_);

    void addText(Vec3 p_455388_, String p_455385_, TextGizmo.Style p_455902_);
}

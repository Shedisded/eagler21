package net.minecraft.server.permissions;

public interface PermissionSet {
    PermissionSet NO_PERMISSIONS = p_454771_ -> false;
    PermissionSet ALL_PERMISSIONS = p_454708_ -> true;

    boolean hasPermission(Permission p_454867_);

    default PermissionSet union(PermissionSet p_456158_) {
        return (PermissionSet)(p_456158_ instanceof PermissionSetUnion ? p_456158_.union(this) : new PermissionSetUnion(this, p_456158_));
    }
}

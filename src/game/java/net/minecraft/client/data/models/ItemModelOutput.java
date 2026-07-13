package net.minecraft.client.data.models;

import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.world.item.Item;

public interface ItemModelOutput {
    default void accept(Item p_387543_, ItemModel.Unbaked p_386880_) {
        this.accept(p_387543_, p_386880_, ClientItem.Properties.DEFAULT);
    }

    void accept(Item p_456234_, ItemModel.Unbaked p_454728_, ClientItem.Properties p_455827_);

    void copy(Item p_387316_, Item p_387995_);
}

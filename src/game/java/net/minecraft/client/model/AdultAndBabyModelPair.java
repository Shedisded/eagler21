package net.minecraft.client.model;

public record AdultAndBabyModelPair<T extends Model>(T adultModel, T babyModel) {
    public T getModel(boolean p_393881_) {
        return p_393881_ ? this.babyModel : this.adultModel;
    }
}

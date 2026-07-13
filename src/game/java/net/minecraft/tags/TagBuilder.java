package net.minecraft.tags;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;

public class TagBuilder {
    private final List<TagEntry> entries = new ArrayList<>();

    public static TagBuilder create() {
        return new TagBuilder();
    }

    public List<TagEntry> build() {
        return List.copyOf(this.entries);
    }

    public TagBuilder add(TagEntry p_215903_) {
        this.entries.add(p_215903_);
        return this;
    }

    public TagBuilder addElement(Identifier p_468950_) {
        return this.add(TagEntry.element(p_468950_));
    }

    public TagBuilder addOptionalElement(Identifier p_469303_) {
        return this.add(TagEntry.optionalElement(p_469303_));
    }

    public TagBuilder addTag(Identifier p_468413_) {
        return this.add(TagEntry.tag(p_468413_));
    }

    public TagBuilder addOptionalTag(Identifier p_467982_) {
        return this.add(TagEntry.optionalTag(p_467982_));
    }
}

package net.minecraft.network.protocol.common.custom;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.resources.Identifier;

public interface CustomPacketPayload {
    CustomPacketPayload.Type<? extends CustomPacketPayload> type();

    static <B extends ByteBuf, T extends CustomPacketPayload> StreamCodec<B, T> codec(StreamMemberEncoder<B, T> p_319960_, StreamDecoder<B, T> p_320047_) {
        return StreamCodec.ofMember(p_319960_, p_320047_);
    }

    static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createType(String p_319908_) {
        return new CustomPacketPayload.Type<>(Identifier.withDefaultNamespace(p_319908_));
    }

    static <B extends FriendlyByteBuf> StreamCodec<B, CustomPacketPayload> codec(
        final CustomPacketPayload.FallbackProvider<B> p_319839_, List<CustomPacketPayload.TypeAndCodec<? super B, ?>> p_320495_
    ) {
        final Map<Identifier, StreamCodec<? super B, ? extends CustomPacketPayload>> map = p_320495_.stream()
            .collect(Collectors.toUnmodifiableMap(p_466114_ -> p_466114_.type().id(), CustomPacketPayload.TypeAndCodec::codec));
        return new StreamCodec<B, CustomPacketPayload>() {
            private StreamCodec<? super B, ? extends CustomPacketPayload> findCodec(Identifier p_467071_) {
                StreamCodec<? super B, ? extends CustomPacketPayload> streamcodec = map.get(p_467071_);
                return streamcodec != null ? streamcodec : p_319839_.create(p_467071_);
            }

            private <T extends CustomPacketPayload> void writeCap(B p_320565_, CustomPacketPayload.Type<T> p_320917_, CustomPacketPayload p_320112_) {
                p_320565_.writeIdentifier(p_320917_.id());
                StreamCodec<B, T> streamcodec = (StreamCodec<B, T>)this.findCodec(p_320917_.id);
                streamcodec.encode(p_320565_, (T)p_320112_);
            }

            public void encode(B p_320490_, CustomPacketPayload p_319776_) {
                this.writeCap(p_320490_, p_319776_.type(), p_319776_);
            }

            public CustomPacketPayload decode(B p_320227_) {
                Identifier identifier = p_320227_.readIdentifier();
                return (CustomPacketPayload)this.findCodec(identifier).decode(p_320227_);
            }
        };
    }

    public interface FallbackProvider<B extends FriendlyByteBuf> {
        StreamCodec<B, ? extends CustomPacketPayload> create(Identifier p_468543_);
    }

    public record Type<T extends CustomPacketPayload>(Identifier id) {
    }

    public record TypeAndCodec<B extends FriendlyByteBuf, T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<B, T> codec) {
    }
}

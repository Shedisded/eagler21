package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record RealmsJoinInformation(
    @SerializedName("address") @Nullable String address,
    @SerializedName("resourcePackUrl") @Nullable String resourcePackUrl,
    @SerializedName("resourcePackHash") @Nullable String resourcePackHash,
    @SerializedName("sessionRegionData") RealmsJoinInformation.@Nullable RegionData regionData
) implements ReflectionBasedSerialization {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RealmsJoinInformation EMPTY = new RealmsJoinInformation(null, null, null, null);

    public static RealmsJoinInformation parse(GuardedSerializer p_426148_, String p_425511_) {
        try {
            RealmsJoinInformation realmsjoininformation = p_426148_.fromJson(p_425511_, RealmsJoinInformation.class);
            if (realmsjoininformation == null) {
                LOGGER.error("Could not parse RealmsServerAddress: {}", p_425511_);
                return EMPTY;
            } else {
                return realmsjoininformation;
            }
        } catch (Exception exception) {
            LOGGER.error("Could not parse RealmsServerAddress", (Throwable)exception);
            return EMPTY;
        }
    }

    public record RegionData(
        @SerializedName("regionName") @Nullable RealmsRegion region, @SerializedName("serviceQuality") @Nullable ServiceQuality serviceQuality
    ) implements ReflectionBasedSerialization {
    }
}

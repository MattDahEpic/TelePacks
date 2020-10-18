package com.mattdahepic.telepacks;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class TelePacksConfig {
    public static class Common {
        public final ForgeConfigSpec.IntValue basicRechargeTicks;
        public final ForgeConfigSpec.IntValue advancedRechargeTicks;

        public Common (ForgeConfigSpec.Builder builder) {
            this.basicRechargeTicks = builder
                    .comment("How long (in ticks) should the basic TelePack take to recharge after teleporting?","Default is 90 ticks.")
                    .defineInRange("basicRechargeTicks",90,0,Integer.MAX_VALUE);
            this.advancedRechargeTicks = builder
                    .comment("How long (in ticks) should the advanced TelePack take to recharge after teleporting?","Default is 240 ticks.")
                    .defineInRange("advancedRechargeTicks",240,0,Integer.MAX_VALUE);
        }
    }
    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    static {
        Pair<Common, ForgeConfigSpec> specPair = (new ForgeConfigSpec.Builder()).configure(Common::new);
        COMMON_SPEC = (ForgeConfigSpec)specPair.getRight();
        COMMON = (Common)specPair.getLeft();
    }
}

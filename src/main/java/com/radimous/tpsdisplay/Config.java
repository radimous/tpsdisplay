package com.radimous.tpsdisplay;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> showList;
    public static ForgeConfigSpec SPEC;
    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        showList = BUILDER
            .comment("uuid of players that want to see the TPS display - use /tpsdisplay enable to add yourself")
            .defineList("showList", new ArrayList<>(), entry -> true);
        SPEC = BUILDER.build();

    }
}

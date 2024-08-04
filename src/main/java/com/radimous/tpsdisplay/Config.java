package com.radimous.tpsdisplay;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> showList;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> hideList;

    public static ForgeConfigSpec.BooleanValue enabledByDefault;
    public static ForgeConfigSpec SPEC;
    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        // this really doesn't need to be exposed, but it's easier to use forge configs than to write my own
        showList = BUILDER
            .comment("Players that want to see the TPS display if default is hidden - don't change manually, use /tpsdisplay enable")
            .defineList("showList", new ArrayList<>(), entry -> true);
        hideList = BUILDER
            .comment("players that don't want to see the TPS display if default is shown  - don't change manually, use /tpsdisplay disable")
            .defineList("hideList", new ArrayList<>(), entry -> true);


        enabledByDefault = BUILDER
            .comment("Whether the TPS display is shown by default")
            .define("shownByDefault", false);

        SPEC = BUILDER.build();

    }
}

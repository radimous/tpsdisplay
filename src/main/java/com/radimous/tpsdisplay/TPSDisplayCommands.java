package com.radimous.tpsdisplay;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class TPSDisplayCommands {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent e) {
        new TPSDisplayCommands(e.getDispatcher());
        ConfigCommand.register(e.getDispatcher());
    }

    public TPSDisplayCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpsdisplay")
            .then(Commands.literal("enable").executes(this::enable).build())
            .then(Commands.literal("disable").executes(this::disable).build())
            .then(Commands.literal("enabledByDefault").requires((player) -> player.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("enabled", BoolArgumentType.bool()).executes(this::setEnabledByDefault).build()
            ).build())

        );
    }

    @SuppressWarnings("unchecked")
    private int enable(CommandContext<CommandSourceStack> command) {
        Entity source = command.getSource().getEntity();
        System.out.println(source);
        if (source instanceof Player player) {
            UUID pid = player.getUUID();
            if (Config.hideList.get().contains(pid.toString())) {
                List<? extends String> newHideList = Config.hideList.get();
                newHideList.remove(pid.toString());
                Config.hideList.set(newHideList);
                Config.hideList.save();
            }
            if (Config.showList.get().contains(pid.toString())) {
                command.getSource().sendSuccess(new TextComponent("TPS is already enabled").withStyle(
                    ChatFormatting.GRAY), false);
                return 0;
            }
            List<? extends String> newShowList = Config.showList.get();
            ((List<String>) newShowList).add(pid.toString());
            Config.showList.set(newShowList);
            Config.showList.save();
            command.getSource().sendSuccess(new TextComponent("TPS display enabled").withStyle(
                ChatFormatting.GRAY), false);
            return 0;
        }
        {
            command.getSource().sendSuccess(new TextComponent("You must be a player to use this command").withStyle(
                ChatFormatting.GRAY), false);
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private int disable(CommandContext<CommandSourceStack> command) {
        Entity source = command.getSource().getEntity();
        if (source instanceof Player player) {
            UUID pid = player.getUUID();
            if (Config.showList.get().contains(pid.toString())) {
                List<? extends String> newShowList = Config.showList.get();
                newShowList.remove(pid.toString());
                Config.showList.set(newShowList);
                Config.showList.save();
            }
            if (Config.hideList.get().contains(pid.toString())) {
                command.getSource().sendSuccess(new TextComponent("TPS is already disabled").withStyle(
                    ChatFormatting.GRAY), false);
                return 0;
            }
            List<? extends String> newHideList = Config.hideList.get();
            ((List<String>) newHideList).add(pid.toString());
            Config.hideList.set(newHideList);
            Config.hideList.save();
            command.getSource().sendSuccess(new TextComponent("TPS display disabled").withStyle(
                ChatFormatting.GRAY), false);
            return 0;
        }
        {
            command.getSource().sendSuccess(new TextComponent("You must be a player to use this command").withStyle(
                ChatFormatting.GRAY), false);
            return 0;
        }
    }

    private int setEnabledByDefault(CommandContext<CommandSourceStack> command) {
        boolean enabled = BoolArgumentType.getBool(command, "enabled");
        Config.enabledByDefault.set(enabled);
        Config.enabledByDefault.save();
        command.getSource().sendSuccess(new TextComponent("TPS display is now " + (enabled ? "enabled" : "disabled") +" by default").withStyle(
            ChatFormatting.GRAY), false);
        return 0;
    }

}
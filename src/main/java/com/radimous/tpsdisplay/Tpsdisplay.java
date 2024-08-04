package com.radimous.tpsdisplay;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Locale;

@Mod("tpsdisplay")
public class Tpsdisplay {

    private static final Component EMPTY_COMPONENT = new TextComponent("");

    public Tpsdisplay() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        MinecraftForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null && server.getTickCount() % 20 == 0) {
            updatePlayerList(server);
        }
    }

    private ChatFormatting getTPSColor(double mspt) {
        if (mspt > 50) {
            return ChatFormatting.DARK_RED;
        }
        if (mspt > 0.8D * 50) {
            return ChatFormatting.RED;
        }
        if (mspt > 0.5D * 50) {
            return ChatFormatting.YELLOW;
        }
        return ChatFormatting.GREEN;
    }


    private void updatePlayerList(MinecraftServer server) {
        double mspt = Mth.average(server.tickTimes) * 1.0E-6D;
        double tps = 1000.0D / Math.max(MinecraftServer.MS_PER_TICK, mspt);
        ChatFormatting color = getTPSColor(mspt);

        Component tpsComponent = new TextComponent("TPS: ").withStyle(ChatFormatting.GRAY)
            .append(new TextComponent(String.format(Locale.ROOT, "%.1f", tps)).withStyle(color))
            .append(new TextComponent(" MSPT: ").withStyle(ChatFormatting.GRAY))
            .append(new TextComponent(String.format(Locale.ROOT, "%.1f", mspt)).withStyle(color));

        server.getPlayerList().getPlayers().forEach(player -> {
            boolean shouldShow;
            if (Config.enabledByDefault.get()) {
                shouldShow = !Config.hideList.get().contains(player.getUUID().toString());
            } else {
                shouldShow = Config.showList.get().contains(player.getUUID().toString());
            }

            if (shouldShow) {
                player.connection.send(new ClientboundTabListPacket(EMPTY_COMPONENT, tpsComponent));
            } else {
                player.connection.send(new ClientboundTabListPacket(EMPTY_COMPONENT, EMPTY_COMPONENT));
            }
        });
    }
}
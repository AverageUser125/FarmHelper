package com.jelly.farmhelper;

import com.jelly.farmhelper.config.Config;
import com.jelly.farmhelper.events.ChatMsgEvent;
import com.jelly.farmhelper.events.OverlayMsgEvent;
import com.jelly.farmhelper.events.PartyChatMsgEvent;
import com.jelly.farmhelper.features.SpaceFarmer;
import com.jelly.farmhelper.hud.ClickGui;
import com.jelly.farmhelper.misc.Utils;
import com.mojang.brigadier.CommandDispatcher;
import commands.FarmHelperCommand;
import io.wispforest.owo.config.ui.ConfigScreenProviders;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static net.fabricmc.loader.impl.FabricLoaderImpl.MOD_ID;

public class Main implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftClient mc;
    public static IEventBus eventBus = new EventBus();

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        FarmHelperCommand.init(dispatcher);
    }

    @Override
    public void onInitializeClient() {
        long start = Util.getMeasuringTimeMs();

        mc = MinecraftClient.getInstance();

        Config.load();
        ConfigScreenProviders.register("com.jelly.farmhelper", screen -> new ClickGui());
        ClientCommandRegistrationCallback.EVENT.register(Main::registerCommands);

        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            String msg = Utils.toPlain(message);

            if (overlay) {
                return !eventBus.post(new OverlayMsgEvent(message, msg)).isCancelled();
            }

            boolean cancelled = eventBus.post(new ChatMsgEvent(message, msg)).isCancelled();
            if (msg.startsWith("Party > ") && msg.contains(": ")) {
                int nameStart = msg.contains("]") && msg.indexOf("]") < msg.indexOf(":") ? msg.indexOf("]") : msg.indexOf(">");
                String[] clean = msg.replace(msg.substring(0, nameStart + 1), "").split(":", 2);
                String author = clean[0].trim(), content = clean[1].trim();
                cancelled = eventBus.post(new PartyChatMsgEvent(content, author)).isCancelled() || cancelled;
            }
            return !cancelled;
        });

        eventBus.registerLambdaFactory("com.jelly.farmhelper",
                (lookupInMethod, klass) -> (MethodHandles.Lookup)
                        lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

        eventBus.subscribe(SpaceFarmer.class);

        LOGGER.info("It's time to get real, NoFrills mod initialized in {}ms.", Util.getMeasuringTimeMs() - start);
    }
}

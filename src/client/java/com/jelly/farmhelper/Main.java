package com.jelly.farmhelper;

import com.jelly.farmhelper.config.Config;
import com.jelly.farmhelper.events.ChatMsgEvent;
import com.jelly.farmhelper.events.OverlayMsgEvent;
import com.jelly.farmhelper.events.PartyChatMsgEvent;
import com.jelly.farmhelper.features.SpaceFarmer;
import com.jelly.farmhelper.misc.Utils;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import io.wispforest.owo.config.ui.ConfigScreenProviders;
import com.jelly.farmhelper.hud.ClickGui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static net.fabricmc.loader.impl.FabricLoaderImpl.MOD_ID;

public class Main implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftClient mc;
    public static IEventBus eventBus = new EventBus();
    @Override
    public void onInitializeClient() {
        mc = MinecraftClient.getInstance();

        Config.load();
        ConfigScreenProviders.register(MOD_ID, screen -> new ClickGui());

        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            String msg = Utils.toPlain(message);

            if (overlay) {
                return !eventBus.post(new OverlayMsgEvent(message, msg)).isCancelled();
            }

            boolean cancelled = eventBus.post(new ChatMsgEvent(message, msg)).isCancelled();
            if (msg.startsWith("Party > ") && msg.contains(": ")) {
                int nameStart = msg.contains("]") & msg.indexOf("]") < msg.indexOf(":") ? msg.indexOf("]") : msg.indexOf(">");
                String[] clean = msg.replace(msg.substring(0, nameStart + 1), "").split(":", 2);
                String author = clean[0].trim(), content = clean[1].trim();
                cancelled = eventBus.post(new PartyChatMsgEvent(content, author)).isCancelled() || cancelled;
            }
            return !cancelled;
        });

        eventBus.registerLambdaFactory(MOD_ID, (lookupInMethod, glass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, glass, MethodHandles.lookup()));

        eventBus.subscribe(SpaceFarmer.class);
    }
}

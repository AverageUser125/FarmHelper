package com.jelly.farmhelper;

import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
public class Main implements ClientModInitializer {

    public static MinecraftClient mc;
    public static IEventBus eventBus = new EventBus();
    @Override
    public void onInitializeClient() {
        mc = MinecraftClient.getInstance();
    }
}

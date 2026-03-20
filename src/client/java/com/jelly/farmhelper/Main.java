package com.jelly.farmhelper;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class Main implements ClientModInitializer {

    public static MinecraftClient mc;

    @Override
    public void onInitializeClient() {
        mc = MinecraftClient.getInstance();
    }
}

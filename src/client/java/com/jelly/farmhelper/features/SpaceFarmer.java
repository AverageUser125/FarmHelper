package com.jelly.farmhelper.features;

public class SpaceFarmer {
    public static SpaceFarmer instance;
    public static boolean spaceHeld = false;
    public boolean isActive() {
        return instance != null;
    }
}

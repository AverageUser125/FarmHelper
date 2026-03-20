package com.jelly.farmhelper.events;

import static com.jelly.farmhelper.Main.mc;

public class PartyChatMsgEvent extends Cancellable {

    public String message;
    public String sender;
    public boolean self;

    public PartyChatMsgEvent(String message, String sender) {
        this.setCancelled(false);
        this.message = message;
        this.sender = sender;
        this.self = sender.equalsIgnoreCase(mc.getSession().getUsername());
    }
}

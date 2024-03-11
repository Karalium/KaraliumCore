package com.kerix.karalium.economy;

import com.kerix.karalium.core.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerRegister implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Main main = Main.getINSTANCE();
        main.implementer.createPlayerAccount(e.getPlayer().getName());
    }
}

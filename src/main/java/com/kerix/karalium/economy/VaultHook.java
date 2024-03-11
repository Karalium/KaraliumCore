package com.kerix.karalium.economy;

import com.kerix.karalium.core.Main;
import org.kerix.api.console.color.ConsoleColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;


/**
 * Plugin Hooking to VaultAPI method.
 */
public class VaultHook {
    private final Main main = Main.getINSTANCE();
    private Economy provider;

    public void hook(){
        provider = main.implementer;
        Bukkit.getServicesManager().register(Economy.class , provider , main, ServicePriority.Normal);
        main.getLogger().info(ConsoleColor.GREEN + "VaultAPI Hooked into " + ConsoleColor.AQUA + main.getName() + ConsoleColor.RESET);
    }

    public void unhook(){
        provider = main.implementer;
        Bukkit.getServicesManager().unregister(Economy.class , provider);
        main.getLogger().info(ConsoleColor.RED + "VaultAPI UnHooked from " + ConsoleColor.AQUA + main.getName() + ConsoleColor.RESET);
    }
}

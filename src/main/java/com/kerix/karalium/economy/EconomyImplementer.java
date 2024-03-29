package com.kerix.karalium.economy;

import com.kerix.karalium.configfiles.Data;
import com.kerix.karalium.core.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;


/**
 * Economy implementation method.
 */
public class EconomyImplementer implements Economy {
    private final Main plugin = Main.getINSTANCE();

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "₽";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return null;
    }

    @Override
    public String currencyNamePlural() {
        return "PokeDollars";
    }

    @Override
    public String currencyNameSingular() {
        return "PokeDollar";
    }

    @Override
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    @Override
    public double getBalance(String s) {
        Player player = Bukkit.getPlayer(s);
        assert player != null;
        UUID uuid = player.getUniqueId();
        return plugin.playerBank.get(uuid);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        return plugin.playerBank.get(uuid);
    }

    @Override
    public double getBalance(String s, String s1) {
        Player player = Bukkit.getPlayer(s);
        assert player != null;
        UUID uuid = player.getUniqueId();
        return plugin.playerBank.get(uuid);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        UUID uuid = offlinePlayer.getUniqueId();
        return plugin.playerBank.get(uuid);
    }

    @Override
    public boolean has(String s, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return false;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        Player player = Bukkit.getPlayer(s);
        assert player != null;
        UUID uuid = player.getUniqueId();
        Double oldBalance = plugin.playerBank.get(uuid);
        if(!plugin.playerBank.containsKey(uuid)) {
            plugin.playerBank.put(uuid , -v);
            return null;
        }
        plugin.playerBank.put(uuid, oldBalance - v);
        plugin.saveDataBank();
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        Player player = Bukkit.getPlayer(s);
        assert player != null;
        UUID uuid = player.getUniqueId();
        Double oldBalance = plugin.playerBank.get(uuid);
        if(!plugin.playerBank.containsKey(uuid)) {
            plugin.playerBank.put(uuid , v);
            return null;
        }
        plugin.playerBank.put(uuid, oldBalance + v);
        plugin.saveDataBank();
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        Player player = Bukkit.getPlayer(s);
        assert player != null;
        UUID uuid = player.getUniqueId();
        Double oldBalance = plugin.playerBank.get(uuid);
        if(!plugin.playerBank.containsKey(uuid)) {
            plugin.playerBank.put(uuid , v);
            return null;
        }
        plugin.playerBank.put(uuid, oldBalance + v);
        plugin.saveDataBank();
        return null;
    }


    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        UUID uuid = offlinePlayer.getUniqueId();
        Double oldBalance = plugin.playerBank.get(uuid);
        if(!plugin.playerBank.containsKey(uuid)) {
            plugin.playerBank.put(uuid , v);
            return null;
        }
        plugin.playerBank.put(uuid, oldBalance + v);
        plugin.saveDataBank();
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        Data data = plugin.getDataConfig();
        Player player = Bukkit.getPlayer(s);
        if(player != null) {
            if(!data.isRegistered(player)){
                YamlConfiguration config = data.getConfig();
                String path = "Players." + player.getUniqueId();
                config.set(path + ".Balance", 0);
                config.set(path + ".Username", player.getName());
            }
        }
        data.saveConfig();
        plugin.loadEconomy();
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}

package me.discoversquishy.lightningcommands;

import me.discoversquishy.lightningcommands.commands.CmdLightningRods;
import me.discoversquishy.lightningcommands.listeners.PlayerActivity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class LightningCommands extends JavaPlugin {
    private static LightningCommands instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getCommand("lightningrods").setExecutor(new CmdLightningRods());
        Bukkit.getPluginManager().registerEvents(new PlayerActivity(), this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static LightningCommands getInstance() {
        return instance;
    }

    public String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}

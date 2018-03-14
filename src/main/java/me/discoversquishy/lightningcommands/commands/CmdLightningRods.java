package me.discoversquishy.lightningcommands.commands;

import me.discoversquishy.lightningcommands.LightningCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
//TODO Add strikeless2go.

public class CmdLightningRods implements CommandExecutor {
    private LightningCommands plugin = LightningCommands.getInstance();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("lightningrods")) {
            return false;
        }

        if (args.length == 0) {
            for (String message : plugin.getConfig().getStringList("Messages.Help")) {
                sender.sendMessage(plugin.colorize(message));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("lightningcommands.give")) {
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("Messages.Missing-Permission", "&c&l(!) &cYou need the permission %permission% to do this!").replace("%permission%", "lightningcommands.give")));
                return false;
            }

            if (args.length == 1) {
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("Messages.Specify-Player", "&c&l(!) &cSpecify a player to give the lightning rod to!")));
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("Messages.Player-Offline", "&c&l(!) &c%player% isn't online!").replace("%player%", args[1])));
                return false;
            }

            if (args.length == 2) {
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("Messages.Specify-Rod", "&c&l(!) &cSpecify a rod to give the player!")));
                return false;
            }

            for (String key : plugin.getConfig().getConfigurationSection("Rods").getKeys(false)) {
                if (!key.equalsIgnoreCase(args[2])) {
                    continue;
                }

                ItemStack rodItem = new ItemStack(Material.valueOf(plugin.getConfig().getString("Rods." + key + ".Item.ID", "BLAZE_ROD").toUpperCase()));
                ItemMeta rodMeta = rodItem.getItemMeta();

                if (args.length >= 4) {
                    try {
                        rodItem.setAmount(Integer.parseInt(args[3]));
                    } catch (IllegalArgumentException e) {
                        Bukkit.dispatchCommand(sender, "lightningrods");
                    }
                }

                if (plugin.getConfig().contains("Rods." + key + ".Item.Name")) {
                    rodMeta.setDisplayName(plugin.colorize(plugin.getConfig().getString("Rods." + key + ".Item.Name")));
                }

                if (plugin.getConfig().contains("Rods." + key + ".Item.Lore")) {
                    List<String> lore = new ArrayList<>();

                    for (String loreLine : plugin.getConfig().getStringList("Rods." + key + ".Item.Lore")) {
                        lore.add(plugin.colorize(loreLine));
                    }

                    rodMeta.setLore(lore);
                }

                rodItem.setItemMeta(rodMeta);

                if (target.getInventory().firstEmpty() == -1) {
                    target.getWorld().dropItem(target.getLocation(), rodItem);
                } else {
                    target.getInventory().addItem(rodItem);
                }
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("Messages.Rod-Given", "&a&l(!) &aRod given to %player%!").replace("%player%", target.getName())));
                return true;
            }
            sender.sendMessage(plugin.colorize(plugin.getConfig().getString("Messages.Unknown-Rod", "&c&l(!) &cThe rod %rod% doesn't exist!").replace("%rod%", args[2])));
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("lightningcommands.reload")) {
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("Messages.Missing-Permission", "&c&l(!) &cYou need the permission %permission% to do this!").replace("%permission%", "lightningcommands.reload")));
                return false;
            }

            plugin.reloadConfig();
            sender.sendMessage(plugin.colorize(plugin.getConfig().getString("Messages.Config-Reloaded", "&a&l(!) &aConfiguration files reloaded!")));
            return true;
        }

        Bukkit.dispatchCommand(sender, "lightningrods");
        return false;
    }
}

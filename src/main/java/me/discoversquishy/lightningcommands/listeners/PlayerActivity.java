package me.discoversquishy.lightningcommands.listeners;

import me.discoversquishy.lightningcommands.LightningCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerActivity implements Listener {
    private LightningCommands plugin = LightningCommands.getInstance();
    private HashMap<UUID, String> strikes = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (player.getItemInHand() == null) {
            return;
        }

        for (String key : plugin.getConfig().getConfigurationSection("Rods").getKeys(false)) {
            ItemStack rodItem = new ItemStack(Material.valueOf(plugin.getConfig().getString("Rods." + key + ".Item.ID", "BLAZE_ROD").toUpperCase()));
            ItemMeta rodMeta = rodItem.getItemMeta();

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

            if (player.getItemInHand().isSimilar(rodItem)) {
                Block targetBlock = player.getTargetBlock((Set<Material>) null, 160);

                if (targetBlock == null) {
                    player.sendMessage(plugin.colorize(plugin.getConfig().getString("Messages.No-Target", "&c&l(!) &cYou must have a location in your sight within 160 blocks!")));
                    return;
                }

                strikes.put(targetBlock.getWorld().strikeLightning(targetBlock.getLocation()).getUniqueId(), key);
                player.sendMessage(plugin.colorize(plugin.getConfig().getString("Messages.Lighting-Summoned", "&a&l(!) &aSuccessfully summoned lightning!")));
                return;
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        if (e.getCause() != EntityDamageEvent.DamageCause.LIGHTNING) {
            return;
        }
        Player victim = (Player) e.getEntity();
        Entity damager = e.getDamager();

        if (!strikes.containsKey(damager.getUniqueId())) {
            return;
        }
        String key = strikes.get(damager.getUniqueId());
        e.setDamage(0);

        if (!plugin.getConfig().contains("Rods." + key + ".Commands")) {
            return;
        }

        for (String command : plugin.getConfig().getStringList("Rods." + key + ".Commands")) {
            command = command.replace("%player%", victim.getName());

            if (command.toUpperCase().startsWith("MESSAGE ")) {
                command = command.replace(command.split(" ")[0], "").trim();

                victim.sendMessage(plugin.colorize(command));
                continue;
            }

            if (command.toUpperCase().startsWith("BROADCAST ")) {
                command = command.replace(command.split(" ")[0], "").trim();

                Bukkit.broadcastMessage(plugin.colorize(command));
                continue;
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}

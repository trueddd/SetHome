package me.downthepark.sethome;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.logging.Level;

public class SetHomeEvents implements Listener {

    private SetHome plugin;

    public SetHomeEvents(SetHome plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        SetHomeUtils utils = new SetHomeUtils(plugin);

        if (event.getPlayer().getBedSpawnLocation() != null) {
            if (plugin.getConfig().getBoolean("debug")) {
                plugin.getLogger().log(Level.WARNING, "Player " + event.getPlayer().getName() + " already has a bed location. Not teleporting to 'set home'.");
            }
        } else if (plugin.getConfig().getBoolean("respawn-player-at-home")) {

            if (utils.homeIsNull(event.getPlayer())) {
                return;
            } else {
                event.setRespawnLocation(new Location(Bukkit.getWorld(plugin.homes.getString("Homes." + event.getPlayer().getUniqueId().toString() + ".World")),
                        plugin.homes.getDouble("Homes." + event.getPlayer().getUniqueId().toString() + ".X"),
                        plugin.homes.getDouble("Homes." + event.getPlayer().getUniqueId().toString() + ".Y"),
                        plugin.homes.getDouble("Homes." + event.getPlayer().getUniqueId().toString() + ".Z"),
                        plugin.homes.getLong("Homes." + event.getPlayer().getUniqueId().toString() + ".Yaw"),
                        plugin.homes.getLong("Homes." + event.getPlayer().getUniqueId().toString() + ".Pitch"))
                );
            }

        }
    }
}
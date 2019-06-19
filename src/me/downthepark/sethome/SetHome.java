package me.downthepark.sethome;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class SetHome extends JavaPlugin {

    private File file = new File(getDataFolder(), "Homes.yml");
    YamlConfiguration homes = YamlConfiguration.loadConfiguration(file);

    private File configFile = new File(getDataFolder(), "Config.yml");
    YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

    private static final String prefixError = ChatColor.DARK_RED + "[" + ChatColor.RED + "*" + ChatColor.DARK_RED + "] " + ChatColor.GRAY;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        SetHomeUtils utils = new SetHomeUtils(this);

        if (!configFile.exists()) {
            saveConfigFile();
        }

        if (command.getName().equals("sethome")) {
            if (sender instanceof ConsoleCommandSender) {
                getLogger().log(Level.WARNING, "Only players can use this command.");
            } else if (sender instanceof Player) {

                Player player = (Player) sender;

                // --- Set player's home by saving it to a file (Homes.yml)
                utils.setHome(player);

                if (config.getBoolean("debug")) {
                    getLogger().log(Level.INFO, player.getDisplayName() + " has set their home at: " + utils.getHomeLocation(player).toString());
                }

                String strFormatted = config.getString("sethome-message").replace("%player%", player.getDisplayName());

                if (config.getBoolean("show-sethome-message")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', strFormatted));
                }

            } else {
                sender.sendMessage(prefixError + "There was an error performing this command.");
            }
        } else if (command.getName().equals("home")) {
            if (sender instanceof ConsoleCommandSender) {
                getLogger().log(Level.WARNING, "Only players can use this command.");
            } else if (sender instanceof Player) {

                Player player = (Player) sender;

                if (utils.homeIsNull(player)) {
                    player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.RED + "*" + ChatColor.DARK_RED + "] " + ChatColor.GRAY + "You must first use /sethome");
                } else {
                    utils.sendHome(player);

                    if (config.getBoolean("play-warp-sound")) {
                        player.playSound(utils.getHomeLocation(player), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    }

                    if (config.getBoolean("debug")) {
                        getLogger().log(Level.INFO, player.getDisplayName() + " has been sent to their home at: " + utils.getHomeLocation(player).toString());
                    }

                    String strFormatted = config.getString("teleport-message").replace("%player%", player.getDisplayName());

                    if (config.getBoolean("show-teleport-message")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', strFormatted));
                    }
                }

            }
        }

        return false;
    }

    public void onEnable() {

        getCommand("sethome").setExecutor(this);
        getCommand("home").setExecutor(this);

        getServer().getPluginManager().registerEvents(new SetHomeEvents(this), this);


        if (!configFile.exists()) {
            saveConfigFile();
        }

        if (!file.exists()) {
            saveHomesFile();
        }
    }

    public void saveHomesFile() {
        try {
            homes.save(file);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save homes file.\nHere is the stack trace:");
            e.printStackTrace();
        }
    }

    private void saveConfigFile() {
        InputStream inputStream = getResource("Config.yml");
        String string = convertStreamToString(inputStream);

        try {
            FileOutputStream stream = new FileOutputStream(configFile);
            stream.write(string.getBytes());
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String convertStreamToString(InputStream inputStream) {

        int i;
        StringBuilder sb = new StringBuilder();

        try {
            while ((i = inputStream.read()) != -1) {
                sb.append((char) i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
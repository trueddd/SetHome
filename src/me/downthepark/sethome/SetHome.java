package me.downthepark.sethome;

// --- Import Bukkit libraries
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

// --- Import Java libraries

// --- Beginning of SetHome class
public class SetHome extends JavaPlugin { // --- SetHome is a JavaPlugin (Bukkit plugin)

    // --- Create private instance of 'Homes.yml' file
    private File file = new File(getDataFolder(), "Homes.yml");
    // --- Create package-private instance of 'Homes.yml' (configurable type)
    YamlConfiguration homes = YamlConfiguration.loadConfiguration(file);

    // --- Create instance of config from Bukkit's getConfig() method. (kind of useless)
    private FileConfiguration config = getConfig();

    // --- Create variable of String type that simplifies handling the error prefix for messages
    private static final String prefixError = ChatColor.DARK_RED + "[" + ChatColor.RED + "*" + ChatColor.DARK_RED + "] " + ChatColor.GRAY;

    // --- Function called onCommand() that is called by the server to get command information
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // --- Create instance of SetHomeUtils and pass in 'JavaPlugin' as parameter
        SetHomeUtils utils = new SetHomeUtils(this);

        // --- Check if command equals 'sethome'
        if (command.getName().equals("sethome")) {
            // --- Check if the one sending the command is the Console
            if (sender instanceof ConsoleCommandSender) {
                // --- If the sender if Console, tell them to piss off
                getLogger().log(Level.WARNING, "Only players can use this command.");

            } else if (sender instanceof Player) { // --- Check if the sender is a Player

                // --- If the sender is a player, continue below
                Player player = (Player) sender; // --- Create instance of Player and cast it to sender

                // --- Set player's home by saving it to a file (Homes.yml)
                utils.setHome(player);

                // --- Check if debugging is enabled
                if (config.getBoolean("debug")) { // --- If debugging is enabled, give the Console some use{full/less} information
                    getLogger().log(Level.INFO, player.getDisplayName() + " has set their home at: " + utils.getHomeLocation(player).toString());
                }

                // --- Create instance of a String that is formatted from the 'config.yml' file.
                String strFormatted = config.getString("sethome-message").replace("%player%", player.getDisplayName());

                // --- If option 'show-sethome-message' is enabled in config, show the player the 'sethome-message' as defined in 'config.yml'
                if (config.getBoolean("show-sethome-message")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', strFormatted));
                }

            } else { // --- If anything goes wrong, tell the sender there was some sort of error that took place
                sender.sendMessage(prefixError + "There was an error performing this command.");
            }

        } else if (command.getName().equals("home")) { // --- I think I'm gonna take a break with the comments for now.
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

        // --- Make commands work using this 'getCommand()' function
        getCommand("sethome").setExecutor(this);
        getCommand("home").setExecutor(this);

        // --- Register plugin events to server
        getServer().getPluginManager().registerEvents(new SetHomeEvents(this), this);

        // --- Load configuration defaults and save file in data folder
        config.options().copyDefaults(true);
        saveDefaultConfig();

        try {
            config.save(getDataFolder() + File.separator + "config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // --- Check if 'Homes.yml' exists - if not, create a new one
        if (!file.exists()) {
            saveHomesFile();
        }
    }

    // --- Method to save 'Homes.yml' file
    public void saveHomesFile() {
        try {
            homes.save(file);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save homes file.\nHere is the stack trace:");
            e.printStackTrace();
        }
    }

}
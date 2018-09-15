package me.downthepark.sethome;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;

public class SetHome extends JavaPlugin {

    private File file = new File(getDataFolder(), "Homes.yml");
    YamlConfiguration homes = YamlConfiguration.loadConfiguration(file);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String prefixError = ChatColor.DARK_RED + "[" + ChatColor.RED + "*" + ChatColor.DARK_RED + "] " + ChatColor.GRAY;
        String prefixSuccess = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "*" + ChatColor.DARK_GREEN + "] ";

        MyUtils utils = new MyUtils(this);
        DecimalFormat df = new DecimalFormat("#.##");

        if(command.getName().equals("sethome")) {
            if(sender instanceof ConsoleCommandSender) {
                getLogger().log(Level.WARNING, "Only players can use this command.");
            }
            else if (sender instanceof Player) {

                Player player = (Player) sender;

                // --- Set player's home by saving it to a file (Homes.yml)
                utils.setHome(player);

                player.sendMessage(
                        prefixSuccess + ChatColor.GOLD + player.getName() + "'s" + ChatColor.GRAY + " home has been set to: " +
                        ChatColor.DARK_AQUA + df.format(player.getLocation().getX()) +
                        ChatColor.GRAY + ", " + ChatColor.DARK_AQUA + df.format(player.getLocation().getY()) +
                        ChatColor.GRAY + ", " + ChatColor.DARK_AQUA + df.format(player.getLocation().getZ())
                );
            } else {
                sender.sendMessage(prefixError + "There was an error performing this command.");
            }
        }

        else if (command.getName().equals("home")) {
            if(sender instanceof ConsoleCommandSender) {
                getLogger().log(Level.WARNING, "Only players can use this command.");
            }
            else if (sender instanceof Player) {

                Player player = (Player) sender;

                utils.goHome(player);

                player.sendMessage(
                        prefixSuccess + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " has been teleported home (" +
                                ChatColor.DARK_AQUA + df.format(player.getLocation().getX()) +
                                ChatColor.GRAY + ", " + ChatColor.DARK_AQUA + df.format(player.getLocation().getY()) +
                                ChatColor.GRAY + ", " + ChatColor.DARK_AQUA + df.format(player.getLocation().getZ()) +
                                ChatColor.GRAY + ")"
                );

            }
        }

        return false;
    }

    public void onEnable() {
        getCommand("sethome").setExecutor(this);
        getCommand("home").setExecutor(this);

        if(!file.exists()) {
            saveHomesFile();
        }

    }

    public void saveHomesFile() {
        try {
            homes.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

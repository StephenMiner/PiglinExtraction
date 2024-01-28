package piglinextraction.me.stephenminer.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.LevelGroup;
import piglinextraction.me.stephenminer.levels.Level;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.lang.String;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

public class JoinCmd implements CommandExecutor, TabCompleter{
    private final PiglinExtraction plugin;
    public JoinCmd(PiglinExtraction plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("join")){
            if (sender instanceof Player player){
                if (player.hasPermission("pe.commands.join")){
                    int size = args.length;
                    if (size >= 1){
                        LevelGroup group = null;
                        for (LevelGroup levelGroup : LevelGroup.levelGroups){
                            if (levelGroup.getName().equalsIgnoreCase(args[0])){
                                group = levelGroup;
                                break;
                            }
                        }
                        if (group != null){
                            if (size >= 2){
                                Level level = Level.fromId(args[1]);
                                if (level == null){
                                    Bukkit.broadcastMessage("Creating level object");
                                    level = Level.fromString(plugin, args[1]);
                                    if (level == null){
                                        sendInventory(player, group);
                                        return false;
                                    }
                                }
                                level.getWaitingRoom().addPlayer(player);
                                player.sendMessage(ChatColor.GREEN + "Sending you to your lobby");
                                return true;
                            }else sendInventory(player, group);
                            return true;
                        }
                    }
                    sendInventory(player);
                    sender.sendMessage(ChatColor.GREEN + "Select a level to join");
                    return true;
                }else sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }else sender.sendMessage(ChatColor.RED + "Sorry, only players can use this command!");
        }
        return false;
    }

    private void sendInventory(Player player, LevelGroup group){
        Inventory inv = Bukkit.createInventory(null, 9, group.getName() + " rooms");
        player.openInventory(inv);
        player.sendMessage(ChatColor.GREEN + "Choose the game lobby you'd like to join");
    }
    private void sendInventory(Player player){
        Inventory inv = Bukkit.createInventory(null, 9, "Levels");
        player.openInventory(inv);
        player.sendMessage(ChatColor.GREEN + "Choose which level you'd like to join");
    }



    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("join")){
            int size = args.length;
            if (size == 1) return groups(args[0]);
            if (size == 2) return levels(args[0]);
        }
        return null;
    }

    private List<String> filter(Collection<String> base, String match){
        List<String> filtered = new ArrayList<>();
        match = match.toLowerCase(Locale.ROOT);
        for (String entry : base){
            String temp = entry.toLowerCase(Locale.ROOT);
            if(temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }

    private List<String> groups(String match){
        List<String> names = new ArrayList<>();
        for (LevelGroup group : LevelGroup.levelGroups){
            names.add(group.getName());
        }
        return filter(names, match);
    }

    private List<String> levels(String match){
        Set<String> levelIds = plugin.levelsFile.getConfig().getConfigurationSection("levels").getKeys(false);
        return filter(levelIds,  match);
    }
}

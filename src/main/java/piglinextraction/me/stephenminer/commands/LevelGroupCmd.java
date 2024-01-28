package piglinextraction.me.stephenminer.commands;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.LevelGroup;

public class LevelGroupCmd implements CommandExecutor, TabCompleter {
    private final PiglinExtraction plugin;
    public LevelGroupCmd(PiglinExtraction plugin){
        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("levelGroup")){
            if (sender instanceof Player player){
                if (!player.hasPermission("pe.commands.levelgroup")){
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    return false;
                }
            }
            int size = args.length;
            if (size > 1){
                LevelGroup levelGroup = null;
                for (LevelGroup group : LevelGroup.levelGroups){
                    if (group.getName().equalsIgnoreCase(args[0]))
                        levelGroup = group;
                }
                if (args[1].equalsIgnoreCase("create")){
                    if (groupExists(args[0])){
                        sender.sendMessage(ChatColor.RED + "This group already exists!");
                        return false;
                    }
                    LevelGroup group = new LevelGroup(plugin, args[0]);
                    group.save();
                    sender.sendMessage(ChatColor.GREEN + "Created new level group " + group.getDisplayName());
                    return true;
                }
                if (levelGroup == null){
                    sender.sendMessage(ChatColor.RED + "The level group inputted (" + args[0] + "), doesn't exist!");
                    return false;
                }
                if (args[1].equalsIgnoreCase("delete")){
                    levelGroup.delete();
                    sender.sendMessage(ChatColor.GREEN + "Deleted levelgroup " + args[1]);
                    return true;
                }
                if (size > 2){
                    if (args[1].equalsIgnoreCase("setIcon")){
                        try{
                            Material mat = Material.matchMaterial(args[2]);
                            levelGroup.setIcon(mat);
                            levelGroup.save();
                            sender.sendMessage(ChatColor.GREEN + "Set level group icon");
                            return true;
                        }catch(Exception ignored){}
                        sender.sendMessage(ChatColor.RED + "Inputted material doesn't exist! Try again");
                    }
                    if (args[1].equalsIgnoreCase("setName")){
                        levelGroup.setName(args[2]);
                        sender.sendMessage(ChatColor.GREEN + "Changed name of group to " + levelGroup.getName());
                        levelGroup.save();
                        return true;
                    }
                    if (!plugin.levelsFile.getConfig().contains("levels." + args[2])){
                        sender.sendMessage(ChatColor.RED + "Inputted level, " + args[2] + ", doesn't exist!");
                        return false;
                    }
                    String level = args[2];
                    if (args[1].equalsIgnoreCase("addLevel")){
                        levelGroup.addLevel(level);
                        sender.sendMessage(ChatColor.GREEN + "Added level " + args[2] + " to level-group " + levelGroup.getName());
                        levelGroup.save();
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("removeLevel")){
                        levelGroup.removeLevel(args[2]);
                        sender.sendMessage(ChatColor.GREEN + "Removed level " + args[2] + " from level-group " + levelGroup.getName());
                        levelGroup.save();
                        return true;
                    }
                }
            }

        }
        return false;
    }

    private boolean groupExists(String name){
        for (LevelGroup group : LevelGroup.levelGroups){
            if (group.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("levelGroup")){
            int size = args.length;
            if (size == 1) return groups(args[0]);
            if (size == 2) return subCmds(args[1]);
            if (size == 3){
                if (args[1].equalsIgnoreCase("setIcon")) return mats(args[2]);
                return levels(args[2]);
            }
        }
        return null;
    }

    private List<String> filter(Collection<String> base, String match){
        List<String> filtered = new ArrayList<>();
        match = match.toLowerCase(Locale.ROOT);
        for (String entry : base){
            String temp = entry.toLowerCase(Locale.ROOT);
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }
    private List<String> subCmds(String match){
        List<String> subs = new ArrayList<>();
        subs.add("setName");
        subs.add("delete");
        subs.add("addLevel");
        subs.add("removeLevel");
        subs.add("create");
        subs.add("setIcon");
        return filter(subs, match);
    }
    private List<String> levels(String match){
        List<String> names = new ArrayList<>();
        for (Level level : Level.levels){
            names.add(level.getId());
        }
        return filter(names, match);
    }
    private List<String> groups(String match){
        List<String> names = new ArrayList<>();
        names.add("[Your name here]");
        for (LevelGroup group : LevelGroup.levelGroups){
            names.add(group.getName());
        }
        return filter(names, match);
    }
    private List<String> mats(String match){
        Set<String> names = new HashSet<>();
        for (Material mat : Material.values()){
            names.add(mat.name().toLowerCase(Locale.ROOT));
        }
        return filter(names, match);
    }
}

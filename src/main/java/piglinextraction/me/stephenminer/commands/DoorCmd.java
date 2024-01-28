package piglinextraction.me.stephenminer.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import piglinextraction.me.stephenminer.PiglinExtraction;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;


import java.util.*;


public class DoorCmd implements CommandExecutor, TabCompleter {
    private final PiglinExtraction plugin;
    public static HashMap<UUID, String> adding = new HashMap<>();
    public DoorCmd(PiglinExtraction plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("door")){
            int size = args.length;
            if (size >= 3){
                if (roomIsReal(args[0])){
                    String roomId = args[0];
                    if (doorIsReal(roomId, args[1])){
                        String doorId = args[1];
                        if (sender instanceof Player player) {
                            if (!player.hasPermission("pe.commands.door")) {
                                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                                return false;
                            }
                            //addTrigger
                            if (args[2].equalsIgnoreCase("addTrigger")) {
                                adding.put(player.getUniqueId(), doorId);
                                player.sendMessage(ChatColor.GREEN + "Right click on a block to add it as a trigger for door");
                                return true;
                            }
                            if (args[2].equalsIgnoreCase("setJammable")) {
                                if (size >= 4) {
                                    boolean jammable = Boolean.parseBoolean(args[2]);
                                    setJammable(roomId, doorId, jammable);
                                    player.sendMessage("Door jam-ability set to " + jammable);
                                    return true;
                                }else sender.sendMessage(ChatColor.RED + "You need to input a true/false value");
                            }
                        }


                    }else sender.sendMessage(ChatColor.RED + "Door " + args[1] + " doesn't exist in room " + args[0]);
                }else sender.sendMessage(ChatColor.RED + "Region " + args[0] + " doesn't exist!");

            }
        }
        return false;
    }

    private boolean roomIsReal(String roomId){
        return plugin.roomsFile.getConfig().contains("rooms." + roomId);
    }
    private boolean doorIsReal(String roomId, String doorId){
        return plugin.roomsFile.getConfig().contains("rooms." + roomId + ".doors." + doorId);
    }

    private void setJammable(String room, String door, boolean jammable){
        String path = "rooms." + room + ".doors." + door + ".jammable";
        plugin.roomsFile.getConfig().set(path, jammable);
        plugin.roomsFile.saveConfig();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equals("door")){
            int size = args.length;
            if (size == 1) return roomIds(args[0]);
            if (size == 2) return doorIds(args[0], args[1]);
            if (size == 3) return subCmds(args[2]);
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

    private List<String> roomIds(String match){
        if (!plugin.roomsFile.getConfig().contains("rooms")) return null;
        Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
        return filter(roomIds, match);
    }
    private List<String> doorIds(String roomId, String match){
        if (!plugin.roomsFile.getConfig().contains("rooms." + roomId + ".doors")) return null;
        Set<String> doorIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms." + roomId + ".doors").getKeys(false);
        return filter(doorIds, match);
    }
    private List<String> subCmds(String match){
        List<String> subCmds = new ArrayList<>();
        subCmds.add("addTrigger");
        return filter(subCmds, match);
    }

}

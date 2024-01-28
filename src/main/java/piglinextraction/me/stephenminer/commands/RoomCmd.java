package piglinextraction.me.stephenminer.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.rooms.Room;

import java.util.*;

public class RoomCmd implements CommandExecutor, TabCompleter {
    private final PiglinExtraction plugin;
    private final List<String> outlining;
    public RoomCmd(PiglinExtraction plugin){
        this.plugin = plugin;
        this.outlining = new ArrayList<>();
    }



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("room")){
            if (sender instanceof Player player){
                if (player.hasPermission("pe.commands")){
                    int size = args.length;
                    if (size < 3){
                        sender.sendMessage(ChatColor.RED + "You must specify which room you are talking about along with the proper arguments!");
                        return false;
                    }
                    if(plugin.roomsFile.getConfig().contains("rooms." + args[0])){
                        if(args[1].equalsIgnoreCase("outline")){
                            boolean on = Boolean.parseBoolean(args[2]);
                            if (on) {
                                outlining.add(args[0]);
                                outline(args[0]);
                                player.sendMessage(ChatColor.GREEN + "Now showing outline for " + args[0]);
                                return true;
                            }else{
                                outlining.remove(args[0]);
                                player.sendMessage(ChatColor.GREEN + "Stopped showing outline for " + args[0]);
                                return true;
                            }
                        }
                        //outdated
                        if (args[1].equalsIgnoreCase("editmode")){
                            boolean on = Boolean.parseBoolean(args[2]);
                            Room room = Room.BY_IDS.get(args[0]);
                            if (on){
                                room.setEditMode(true);
                                Bukkit.broadcastMessage("" + room.editModeOn());
                                player.sendMessage(ChatColor.GREEN + "Edit mode is now on for room " + args[0]);
                            }else {
                                room.setEditMode(false);
                                player.sendMessage(ChatColor.GREEN + "Edit mode is now off for room " + args[0]);
                                Bukkit.broadcastMessage("" + room.editModeOn());
                            }
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("load")){
                            Room room = Room.BY_IDS.get(args[1]);
                            room.load();
                            player.sendMessage(ChatColor.GREEN + "Loaded room " + room.getId());
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("unload")){
                            Room room = Room.BY_IDS.get(args[1]);
                            room.unload();
                            player.sendMessage(ChatColor.GREEN + "Unloaded room " + room.getId());
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("reload")){
                            Room room = Room.BY_IDS.get(args[1]);
                            room.reload();
                            player.sendMessage(ChatColor.GREEN + "Reloaded room " + room.getId());
                            return true;
                        }
                    }
                }else sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }else sender.sendMessage(ChatColor.RED + "Sorry, but only players can use this command");
        }
        return false;
    }

    private void outline(String room){
        String path = "rooms." + room;
        Location loc1 = plugin.fromString(plugin.roomsFile.getConfig().getString(path + ".loc1")).clone().add(0.5,0.5,0.5);
        Location loc2 = plugin.fromString(plugin.roomsFile.getConfig().getString(path + ".loc2")).clone().add(0.5,0.5,0.5);
        World world = loc1.getWorld();
        org.bukkit.util.Vector fp = loc1.toVector();
        org.bukkit.util.Vector sp = loc2.toVector();
        org.bukkit.util.Vector max = org.bukkit.util.Vector.getMaximum(fp, sp);
        org.bukkit.util.Vector min = Vector.getMinimum(fp, sp);
        Set<Location> locSet = new HashSet<>();
        for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
            for (int x = min.getBlockX() - 1; x <= max.getBlockX() + 1; x++) {
                locSet.add(new Location(world, x, y, min.getBlockZ() - 1));
                locSet.add(new Location(world, x, y, max.getBlockZ() + 1));
            }
            for (int z = min.getBlockZ() - 1; z <= max.getBlockZ() + 1; z++) {
                locSet.add(new Location(world,min.getBlockX() - 1, y, z));
                locSet.add(new Location(world,max.getBlockX() + 1, y, z));
            }
        }
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!outlining.contains(room)) this.cancel();
                for (Location loc : locSet){
                    Location l = loc.clone().add(0.5,0.5,0.5);
                    world.spawnParticle(Particle.VILLAGER_HAPPY, l, 0);
                }
            }
        }.runTaskTimer(plugin, 1, 10);
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("room")){
            int size = args.length;
            if (size == 1) return rooms(args[0]);
            if (size == 2) return options();
            if (size == 3){
                if(args[1].equalsIgnoreCase("outline")) return bools();
                if(args[1].equalsIgnoreCase("editmode")) return bools();
            }
        }
        return null;
    }


    private List<String> filter(Collection<String> list, String match){
        List<String> filtered = new ArrayList<>();
        match = match.toLowerCase(Locale.ROOT);
        for (String entry : list){
            String temp = entry.toLowerCase(Locale.ROOT);
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }


    private List<String> rooms(String match){
        Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
        return filter(roomIds, match);
    }
    private List<String> options(){
        List<String> options = new ArrayList();
        options.add("outline");
        options.add("editmode");
        options.add("load");
        options.add("unload");
        options.add("reload");
        return options;
    }
    private List<String> bools(){
        List<String> bools = new ArrayList<>();
        bools.add("true");
        bools.add("false");
        return bools;
    }
}

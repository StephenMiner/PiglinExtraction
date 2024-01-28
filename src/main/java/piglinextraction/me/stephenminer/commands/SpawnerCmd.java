package piglinextraction.me.stephenminer.commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.containers.Items;
import piglinextraction.me.stephenminer.mobs.PiglinType;

import java.util.*;

public class SpawnerCmd implements CommandExecutor, TabCompleter {

    private final PiglinExtraction plugin;
    private boolean outlining;

    public SpawnerCmd(PiglinExtraction plugin){
        this.plugin = plugin;
        outlining = false;
    }



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("spawner")){
            if (sender instanceof Player player) {
                if (!player.hasPermission("pe.commands.node")){
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return false;
                }
                int size = args.length;
                if (size < 2){
                    player.sendMessage(ChatColor.RED + "Not enough arguments!");
                    return false;
                }
                if (args[0].equalsIgnoreCase("outline")){
                    outlining = Boolean.parseBoolean(args[1]);
                    if (outlining){
                        outline();
                        player.sendMessage(ChatColor.GREEN + "Outlining all spawner nodes");
                    }else player.sendMessage(ChatColor.GREEN + "No longer outlining spawner nodes");
                    return true;
                }
                if (size < 3){
                    player.sendMessage(ChatColor.RED + "You need a minimum of 3, max of 4 arguments to use this command!");
                    return false;
                }
                Items items = new Items(plugin);
                PiglinType type = null;
                try{
                    type = PiglinType.valueOf(args[1]);
                }catch (Exception ignored){}
                if (type != null){
                    try{
                        int radius = Integer.parseInt(args[2]);
                        if (size >= 4){
                            int interval = Integer.parseInt(args[3]);
                            player.getInventory().addItem(items.nodeItem(type, radius, interval));
                            player.sendMessage(ChatColor.GREEN + "You have been given a node wand with the settings you inputted! Right click a block to add this node to that location! Break that block to remove it.");
                            return true;
                        }
                        player.getInventory().addItem(items.nodeItem(type, radius));
                        player.sendMessage(ChatColor.GREEN + "You have been given a node wand with the settings you inputted! Right click a block to add this node to that location! Break that block to remove it.");
                        return true;
                    }catch (Exception ignored){}
                    sender.sendMessage(ChatColor.RED + "Something went wrong when getting integer values, make sure you are using whole number digits for the 3rd and/or 4th arguments!");
                }else sender.sendMessage(ChatColor.RED + "The inputted piglin entity " + args[1] + " doesn't exist!");
            }else sender.sendMessage(ChatColor.RED + "Sorry, only players can use this command!");
        }
        return false;
    }


    private void outline(){
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!outlining) {
                    this.cancel();
                    return;
                }
                Player player;
                if (!plugin.roomsFile.getConfig().contains("rooms")) return;
                Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
                for (String roomId : roomIds){
                    if (!plugin.roomsFile.getConfig().contains("rooms." + roomId + ".nodes")) continue;
                    Set<String> nodeLocs = plugin.roomsFile.getConfig().getConfigurationSection("rooms." + roomId + ".nodes").getKeys(false);
                    for (String nodeLoc : nodeLocs){
                        outlineLoc(plugin.fromString(nodeLoc));
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    private void outlineLoc(Location loc){
        Block block = loc.getBlock();
        BoundingBox bounds = block.getBoundingBox();
        World world = loc.getWorld();
        Location container = loc.clone();
        container.setY(bounds.getMaxY() + 0.2);
        for (double x = bounds.getMinX(); x <= bounds.getMaxX(); x+=0.2){
            for (double z = bounds.getMinZ(); z <= bounds.getMaxZ(); z+=0.2){
                container.setX(x);
                container.setZ(z);
                world.spawnParticle(Particle.VILLAGER_HAPPY, container, 0);
            }
        }
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("spawner")){
            int size = args.length;
            if (size == 1) return options(args[0]);
            if (size == 2) {
                if (args[0].equalsIgnoreCase("outline")) return booleans(args[1]);
                if (args[0].equalsIgnoreCase("give")) return entity(args[1]);
            }
            if (size == 3){
                if (args[0].equalsIgnoreCase("give")) return radius();
            }
            if (size == 4){
                if (args[0].equalsIgnoreCase("give")) return interval();
            }


        }
        return null;
    }



    private List<String> filter(Collection<String> base, String match){
        List<String> filtered = new ArrayList<>();
        match = match.toLowerCase(Locale.ROOT);
        for (String entry : base ){
            String temp = entry.toLowerCase(Locale.ROOT);
            if (temp.contains(match)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    private List<String> options(String match){
        List<String> options = new ArrayList<>();
        options.add("outline");
        options.add("give");
        return filter(options, match);
    }

    private List<String> entity(String match){
        List<String> piglinNames = new ArrayList<>();
        for (PiglinType piglinType : PiglinType.values()){
            piglinNames.add(piglinType.name());
        }
        return filter(piglinNames, match);
    }

    private List<String> radius(){
        List<String> radius = new ArrayList<>();
        radius.add("[whole number spawn radius]");
        return radius;
    }
    private List<String> interval(){
        List<String> interval = new ArrayList<>();
        interval.add("[time in ticks between spawning]");
        return interval;
    }

    private List<String> booleans(String match){
        List<String> bools = new ArrayList<>();
        bools.add("true");
        bools.add("false");
        return filter(bools, match);
    }
}

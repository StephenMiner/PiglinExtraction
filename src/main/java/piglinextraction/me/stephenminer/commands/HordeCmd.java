package piglinextraction.me.stephenminer.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.*;
import piglinextraction.me.stephenminer.mobs.boss.Warlord;
import piglinextraction.me.stephenminer.mobs.hordes.TriggerType;

import java.util.*;
import java.util.stream.Collectors;

public class HordeCmd implements CommandExecutor, TabCompleter {
    private final PiglinExtraction plugin;

    private final Set<UUID> highlighting;

    public HordeCmd(PiglinExtraction plugin){
        this.plugin = plugin;
        highlighting = new HashSet<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            if(!player.hasPermission("pe.commands.horde")){
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            int size = args.length;
            //Not enough args
            if (size < 1){
                player.sendMessage(ChatColor.RED + "You need to input at least one argument to use this command!");
                return false;
            }
            String sub = args[0];
            boolean validSub = subCmds(null).contains(sub);
            //invalid 1st arg
            if (!validSub){
                player.sendMessage(ChatColor.RED + "The input 1st argument is not a valid sub-cmd. See the tab completer for appropriate arguments");
                return false;
            }
            //create
            if (sub.equalsIgnoreCase("create")){
                if (size >= 2){
                    String id = args[1];
                    if (!validHorde(id)){
                        createHorde(id);
                        player.sendMessage(ChatColor.GREEN + "Created new horde entry with id " + id);
                        player.sendMessage(ChatColor.YELLOW + "Now you must add spawn nodes and define the trigger for this horde through the correct commands!");
                        return true;
                    }else player.sendMessage(ChatColor.RED + "The inputted id is already taken! Please select one you aren't already using!");
                }
            }
            //horde add-spawnnode [id] [toSpawn] [PiglinTypes...]
            //add-spawnnode
            if (sub.equalsIgnoreCase("add-spawnnode")){
                if (size >= 4){
                    String id = args[1];
                    if (validHorde(id)){
                        try{
                            int toSpawn = Integer.parseInt(args[2]);
                            List<Class<? extends PiglinEntity>> classes = new ArrayList<>();
                            MobTranslator translator = new MobTranslator();
                            for (int i = 3; i < size; i++){
                                Class<? extends PiglinEntity> clazz = translator.parseString(args[i]);
                                classes.add(clazz);
                            }
                            Location loc = player.getLocation();
                            addSpawnNode(id,loc,toSpawn, classes);
                            player.sendMessage(ChatColor.GREEN + "Created spawn node!");
                        }catch (Exception e){
                            e.printStackTrace();
                            player.sendMessage(ChatColor.RED + "One of your arguments was invalid, please be sure to have Case matching characters!");
                        }
                    }else player.sendMessage(ChatColor.RED + "The input id is not real!");
                }
            }
            //horde remove-spawnnode [id] [location]
            if (sub.equalsIgnoreCase("remove-spannode")){
                if (size >= 3){
                    String id = args[1];
                    if (validHorde(id)){
                        String sLoc = args[2];
                        removeSpawnNode(id,sLoc);
                        player.sendMessage(ChatColor.GREEN + "Remoed horde spawn node at location " + sLoc + " for horde " + id);
                        return true;
                    }else player.sendMessage(ChatColor.RED + id + " is not a real horde Id");
                 }else player.sendMessage(ChatColor.RED + "Missing arguments, need to input horde id and node location!");
            }

            if (sub.equalsIgnoreCase("set-trigger")){
                String id = args[1];
                if (!validHorde(id)){
                    player.sendMessage(ChatColor.RED + "Invalid horde Id");
                    return false;
                }
                if (size >= 4){
                    String strType = args[2];
                    TriggerType type = TriggerType.valueOf(strType);
                    String triggerId = args[3];
                    if (!validTriggerId(type, triggerId)){
                        player.sendMessage(ChatColor.RED + "Invalid triggerId");
                        return false;
                    }
                    setTrigger(id, triggerId, type);
                    player.sendMessage(ChatColor.GREEN + "Created Horde");
                }
            }


            //horde highlight [id]

        }else sender.sendMessage(ChatColor.RED + "For the sake of my sanity, only players can use this command!");
        return false;
    }

    private void createHorde(String id){
        plugin.hordesFile.getConfig().set("hordes." + id + ".id", id);
        plugin.hordesFile.saveConfig();
    }

    private void addSpawnNode(String hordeId, Location loc, int toSpawn, List<Class<? extends PiglinEntity>> types){
        String path = "hordes." + hordeId + ".nodes." + plugin.fromBlockLoc(loc);
        MobTranslator translator = new MobTranslator();
        List<String> sTypes = types.stream().map(translator::fromClass).toList();
        plugin.hordesFile.getConfig().set(path + ".types", sTypes);
        plugin.hordesFile.getConfig().set(path + ".toSpawn",toSpawn);
        plugin.hordesFile.saveConfig();
    }

    private void removeSpawnNode(String hordeId,String sLoc){
        String path = "hordes." + hordeId + ".nodes." + sLoc;
        plugin.hordesFile.getConfig().set(path,null);
        plugin.hordesFile.saveConfig();
    }

    private boolean containsNode(String hordeId, Location loc){
        String sLoc = plugin.fromBlockLoc(loc);
        Set<String> nodes = plugin.hordesFile.getConfig().getConfigurationSection("hordes." + hordeId + ".nodes." + plugin.fromBlockLoc(loc)).getKeys(false);
        return nodes.stream().filter(str -> str.equalsIgnoreCase(sLoc)).findFirst().orElse(null) != null;
    }

    private boolean validTriggerId(TriggerType type, String triggerId){
        if (type == TriggerType.DOOR){
            Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
            for (String roomId : roomIds){
                if (plugin.roomsFile.getConfig().contains("rooms." + roomId + ".doors." + triggerId)) return true;
            }
        }else{
            return plugin.levelsFile.getConfig().contains("levels." + triggerId);
        }
        return false;
    }

    private Set<Location> nodeLocs(String hordeId){
        Set<String> nodes = plugin.hordesFile.getConfig().getConfigurationSection("hordes." + hordeId + ".nodes").getKeys(false);
        return nodes.stream().map(plugin::fromString).collect(Collectors.toSet());
    }

    private void highlight(Player player, String hordeId){
        Set<Location> nodeLocs = nodeLocs(hordeId);
        World world = player.getWorld();
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!highlighting.contains(player.getUniqueId())){
                    this.cancel();
                    return;
                }
                nodeLocs.forEach(loc -> world.spawnParticle(Particle.VILLAGER_HAPPY,loc, 1));
            }
        }.runTaskTimer(plugin, 1, 10);
    }

    private void delete(String hordeId){
        plugin.hordesFile.getConfig().set("hordes." + hordeId,null);
        plugin.hordesFile.saveConfig();
    }

    private boolean realDoor(CommandSender sender, String roomId, String doorId){
        if (plugin.roomsFile.getConfig().contains("rooms." + roomId + ".doors." + doorId)){
            return true;
        }else{
            sender.sendMessage(ChatColor.RED + "Invalid Door or Room ID");
            return false;
        }
    }


    private void setTrigger(String hordeId, String triggerId, TriggerType type){
        String strTrigger = triggerId + "/" + type.toString();
        plugin.hordesFile.getConfig().set("hordes." + hordeId + ".trigger",strTrigger);
        plugin  .hordesFile.saveConfig();
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        int size = args.length;
        if (size == 1) return subCmds(args[0]);
        if (size == 2){
            if (!args[0].equalsIgnoreCase("create")) return hordeIds(args[1]);
        }
        if (size == 3) {
            if (args[1].equalsIgnoreCase("add-spawnnode")) return toSpawn();
            if (args[1].equalsIgnoreCase("set-trigger")) return triggerTypes(args[2]);
        }
        if (size >= 4){
            if (args[1].equalsIgnoreCase("add-spawnnode")) return piglinTypes(args[size-1]);
            if (args[1].equalsIgnoreCase("set-trigger")) return potTriggerIds(args[3]);
        }
        return null;
    }


    private boolean validHorde(String id){
        return plugin.hordesFile.getConfig().contains("hordes." + id);
    }



    public List<String> filter(Collection<String> base, String match){
        if (match == null || match.isEmpty()) return new ArrayList<>(base);
        match = match.toLowerCase();
        List<String> filtered = new ArrayList<>();
        for (String entry : base){
            String temp = ChatColor.stripColor(entry).toLowerCase();
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }

    public List<String> subCmds(String match){
        List<String> subs = new ArrayList<>();
        subs.add("create");
        subs.add("add-spawnnode");
        subs.add("highlight-spawnnodes");
        subs.add("remove-spawnnode");
        subs.add("set-trigger");
        subs.add("delete");
        return filter(subs, match);
    }

    public List<String> piglinTypes(String match){
        List<String> types = new ArrayList<>();
        MobTranslator translator = new MobTranslator();
        types.add(translator.fromClass(PiglinGrunt.class));
        types.add(translator.fromClass(PiglinKnight.class));
        types.add(translator.fromClass(PiglinGuard.class));
        types.add(translator.fromClass(Necromancer.class));
        types.add(translator.fromClass(BlazeShooter.class));
        types.add(translator.fromClass(Warlord.class));
        return filter(types, match);
    }

    public List<String> toSpawn(){
        List<String> list = new ArrayList<>();
        list.add("[to-spawn]");
        return list;
    }
    
    public List<String> hordeIds(String match){
        if (!plugin.hordesFile.getConfig().contains("hordes")) return null;
        Set<String> ids = plugin.hordesFile.getConfig().getConfigurationSection("hordes").getKeys(false);
        return filter(ids, match);
    }

    public List<String> spawnNodes(String hordeId, String match){
        Set<String> nodes = plugin.hordesFile.getConfig().getConfigurationSection("hordes." + hordeId).getKeys(false);
        return filter(nodes, match);
    }

    public List<String> potTriggerIds(String match){
        Set<String> triggerIds = new HashSet<>();
        Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
        for (String roomId : roomIds){
            Set<String> doorIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms." + roomId + ".doors").getKeys(false);
            triggerIds.addAll(doorIds);
        }
        Set<String> levelIds = plugin.levelsFile.getConfig().getConfigurationSection("levels").getKeys(false);
        triggerIds.addAll(levelIds);
        return filter(triggerIds, match);
    }

    public List<String> triggerTypes(String match){
        List<String> vals = Arrays.stream(TriggerType.values()).map(TriggerType::toString).toList();
        return filter(vals,match);
    }
}

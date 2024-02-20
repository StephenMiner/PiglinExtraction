package piglinextraction.me.stephenminer.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.objectives.Objective;
import piglinextraction.me.stephenminer.levels.objectives.ObjectiveType;
import piglinextraction.me.stephenminer.levels.objectives.RuneObj;
import piglinextraction.me.stephenminer.levels.objectives.SlayingObj;
import piglinextraction.me.stephenminer.mobs.PiglinType;

import java.util.*;

public class LevelCmd implements CommandExecutor, TabCompleter {
    private final PiglinExtraction plugin;

    public static HashMap<UUID, Objective> addObj = new HashMap<>();
    public static HashMap<UUID, String> tiedLevel = new HashMap<>();
    public LevelCmd(PiglinExtraction plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("level")){
            int size = args.length;
            if (size < 2){
                sender.sendMessage(ChatColor.RED + "Not enough arguments!");
                return false;
            }
            if (sender instanceof Player player){
                if(!player.hasPermission("pe.commands.level")){
                    player.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use this command!");
                    return false;
                }
                if (!plugin.levelsFile.getConfig().contains("levels." + args[1])){
                    player.sendMessage(ChatColor.RED + "Inputted level " + args[1] + " doesn't exist!");
                    return false;
                }
                if (args[0].equalsIgnoreCase("setLobby")){
                    plugin.levelsFile.getConfig().set("levels." + args[1] + ".lobby", plugin.fromLoc(player.getLocation()));
                    plugin.levelsFile.saveConfig();
                    player.sendMessage(ChatColor.GREEN + "Set lobby spawn for level " + args[1] + "!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("setSpawn")){
                    plugin.levelsFile.getConfig().set("levels." + args[1] + ".spawn", plugin.fromLoc(player.getLocation()));
                    plugin.levelsFile.saveConfig();
                    player.sendMessage(ChatColor.GREEN + "Set level start spawn for level " + args[1]);
                    return true;
                }
                if (size >= 4){
                    if (args[0].equalsIgnoreCase("addObjective")){
                        if (plugin.levelsFile.getConfig().contains("levels." + args[1])){
                            UUID uuid = player.getUniqueId();
                            if (addObj.containsKey(uuid) || tiedLevel.containsKey(uuid)){
                                player.sendMessage(ChatColor.RED + "You are currently already adding objective spawns to level " + tiedLevel.get(uuid) + ". Either cancel or finish this process before using this command again!");
                            }
                            ObjectiveType type = ObjectiveType.valueOf(args[2].toUpperCase());
                            String id = args[3];
                            if (type == ObjectiveType.RUNE_COLLECTION) {
                                addObj.put(uuid, fromType(id, type));
                                tiedLevel.put(uuid, args[1]);
                                player.sendMessage(ChatColor.GREEN + "Right click on the blocks you want this objective to be able to spawn on top of!");
                                player.sendMessage(ChatColor.YELLOW + "Type 'done' in chat when you are finished to save the objectives to the level!");
                                player.sendMessage(ChatColor.YELLOW + "Type 'cancel' in chat to cancel this process");
                                return true;
                            }else if (size >= 6){
                                if (type == ObjectiveType.SLAYING) {
                                    String sType = args[4];
                                    PiglinType piglin = PiglinType.valueOf(sType);
                                    int num = Integer.parseInt(args[5]);
                                    saveSlayingObj(id,args[1],piglin, num);
                                    player.sendMessage(ChatColor.GREEN + "Successfully created slaying objective");
                                    return true;
                                }
                            }
                            return true;
                        }
                    }
                }
            }

            if(args[0].equalsIgnoreCase("delete")) {
                if (plugin.levelsFile.getConfig().contains("levels." + args[1])) {
                    plugin.levelsFile.getConfig().set("levels." + args[1], null);
                    plugin.levelsFile.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Deleted level " + args[1] + "!");
                    return true;
                }else sender.sendMessage(ChatColor.RED + "Inputted level " + args[1] + " doesn't exist!");
            }
            if (args[0].equalsIgnoreCase("load")){
                for (Level level : Level.levels){
                    if (level.getId().equalsIgnoreCase(args[1])){
                        level.load();
                        sender.sendMessage(ChatColor.GREEN + "Loaded level " + level.getId());
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED + "Inputted level " + args[1] + " doesn't exist!");
            }
            if (args[0].equalsIgnoreCase("unload")){
                for (Level level : Level.levels){
                    if (level.getId().equalsIgnoreCase(args[1])){
                        level.unload();
                        sender.sendMessage(ChatColor.GREEN + "Unloaded level " + level.getId());
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED + "Inputted level " + args[1] + " doesn't exist!");
            }
            if (args[0].equalsIgnoreCase("reload")){
                for (Level level : Level.levels){
                    if (level.getId().equalsIgnoreCase(args[1])){
                        level.reload();
                        sender.sendMessage(ChatColor.GREEN + "Reloaded level " + level.getId());
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED + "Inputted level " + args[1] + " doesn't exist!");
            }
            if(size < 3){
                sender.sendMessage(ChatColor.RED + "Not enough arguments!");
                return false;
            }
            if (args[0].equalsIgnoreCase("create")){
                String id = args[1];
                if(plugin.levelsFile.getConfig().contains("levels." + id)){
                    sender.sendMessage(ChatColor.RED + "Level " + args[1] + " already exists!");
                    return false;
                }
                String name = args[2];
                plugin.levelsFile.getConfig().set("levels." + id + ".name", name);
                plugin.levelsFile.saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Created new level " + name + "!");
                sender.sendMessage(ChatColor.GOLD + "Right now your level doensn't do much, use /level setIcon to set the icon of the level and /level addRoom to add a room, and /level setSpawn to set the spawn!");
                return true;
            }

            if (args[0].equalsIgnoreCase("setIcon")){
                if (plugin.levelsFile.getConfig().contains("levels." + args[1])){
                    try{
                        Material mat = Material.matchMaterial(args[2].toUpperCase(Locale.ROOT));
                        plugin.levelsFile.getConfig().set("levels." + args[1] + ".mat", args[2].toUpperCase(Locale.ROOT));
                        plugin.levelsFile.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Set level icon to " + args[2] + " for level " + args[1]);
                        return true;
                    }catch (Exception ignored){}
                    sender.sendMessage(ChatColor.RED + "Inputted material " + args[2] + " doesn't exist!");
                }
                /*
                for(Level level : Level.levels){
                    if (level.getId().equalsIgnoreCase(args[1])){
                        try{
                            Material mat = Material.matchMaterial(args[2].toUpperCase(Locale.ROOT));
                            level.setIcon(mat);
                            level.save();
                            sender.sendMessage(ChatColor.GREEN + "Set level icon to " + mat.name().toLowerCase(Locale.ROOT).replace('_',' '));
                            return true;
                        }catch (Exception ignored){}
                        sender.sendMessage(ChatColor.RED + "Inputted material " + args[2] + " doesn't exist!");
                        return false;
                    }
                }

                 */
                sender.sendMessage(ChatColor.RED + "Inputted level " + args[1] + " doesn't exist!");
            }

            if (args[0].equalsIgnoreCase("addRoom")){
                if (plugin.levelsFile.getConfig().contains("levels." + args[1])){
                    if (plugin.roomsFile.getConfig().contains("rooms." + args[2])){
                        List<String> rooms = plugin.levelsFile.getConfig().getStringList("levels." + args[1] + ".rooms");
                        rooms.add(args[2]);
                        plugin.levelsFile.getConfig().set("levels." + args[1] + ".rooms", rooms);
                        plugin.levelsFile.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Added room " + args[2] + " to level " + args[1]);
                        return true;
                    }else sender.sendMessage(ChatColor.RED + "Inputted room " + args[2] + " doesn't exist!");
                }else sender.sendMessage(ChatColor.RED + "Inputted level " + args[1] + " doesn't exist!");
                /*
                for (Level level : Level.levels){
                    if (level.getId().equalsIgnoreCase(args[1])){
                        if(Room.BY_IDS.containsKey(args[2])){
                            level.addRoom(Room.BY_IDS.get(args[2]));
                            level.save();
                            sender.sendMessage(ChatColor.GREEN + "Added room " + args[2] + " to level " + args[1] + "!");
                            return true;
                        }else if (plugin.roomsFile.getConfig().contains("rooms." + args[1])) {
                            level.addRoom(Room.fromString(plugin, args[1]));
                            level.save();
                            sender.sendMessage(ChatColor.GREEN + "Added room " + args[2] + " to level " + args[1] + "!");
                        }else{
                            sender.sendMessage(ChatColor.RED + "Inputted room " + args[2] + " doesn't exist! (Keep in mind caps matter!)");
                            return false;
                        }
                    }
                }

                 */

            }

            if (args[0].equalsIgnoreCase("removeRoom")){
                if (plugin.levelsFile.getConfig().contains("levels." + args[1])){
                    if (plugin.roomsFile.getConfig().contains("rooms." + args[2])){
                        List<String> rooms = plugin.levelsFile.getConfig().getStringList("levels." + args[1] + ".rooms");
                        rooms.remove(args[2]);
                        plugin.levelsFile.getConfig().set("levels." + args[1] + ".rooms", rooms);
                        plugin.levelsFile.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Removed room " + args[2] + " to level " + args[1]);
                        return true;
                    }else sender.sendMessage(ChatColor.RED + "Inputted room " + args[2] + " doesn't exist!");
                }else sender.sendMessage(ChatColor.RED + "Inputted level " + args[1] + " doesn't exist!");
            }

            if (args[0].equalsIgnoreCase("addHorde")){
                if (plugin.levelsFile.getConfig().contains("levels." + args[1])){
                    if (plugin.hordesFile.getConfig().contains("hordes." + args[2])){
                        List<String> hordes = plugin.levelsFile.getConfig().getStringList("levels." + args[1] + ".hordes");
                        if (!hordes.contains(args[2])) hordes.add(args[2]);
                        plugin.levelsFile.getConfig().set("levels." + args[1] + ".hordes", null);
                        plugin.levelsFile.getConfig().set("levels."+ args[1] + ".hordes",hordes);
                        plugin.levelsFile.saveConfig();
                    }
                }
            }

            if (args[0].equalsIgnoreCase("removeHorde")){
                if (plugin.levelsFile.getConfig().contains("levels." + args[1])){
                    if (plugin.hordesFile.getConfig().contains("hordes." + args[2])){
                        List<String> hordes = plugin.levelsFile.getConfig().getStringList("levels." + args[1] + ".hordes");
                        hordes.remove(args[2]);
                        plugin.levelsFile.getConfig().set("levels." + args[1] + ".hordes", null);
                        plugin.levelsFile.getConfig().set("levels."+ args[1] + ".hordes",hordes);
                        plugin.levelsFile.saveConfig();
                    }
                }
            }



        }
        return false;
    }


    private void saveSlayingObj(String id,String levelId,  PiglinType type, int num){
        SlayingObj obj = new SlayingObj(plugin,id,type,num);
        obj.save(levelId);
    }

    private Objective fromType(String id, ObjectiveType type){
        return switch (type){
            case RUNE_COLLECTION -> new RuneObj(plugin, id);
            default -> new Objective(plugin, id, type);
        };
    }

    private void cleanupTimer(Player player){
        new BukkitRunnable(){
            final UUID uuid = player.getUniqueId();
            final int max = 12000;
            int count = 0;
            @Override
            public void run(){
                if (count >= max){
                    LevelCmd.tiedLevel.remove(uuid);
                    LevelCmd.addObj.remove(uuid);
                    player.sendMessage(ChatColor.YELLOW + "You are no longer editing objectives");
                }
                count+=20;
            }
        }.runTaskTimer(plugin, 1, 20);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("level")){
            int size = args.length;
            if (size == 1) return options(args[0]);
            if (size == 2){
                String sub = args[0].toLowerCase(Locale.ROOT);
                return switch (sub){
                    case "create" -> yourName(true);
                    default -> levels(args[1]);
                };
            }
            if (size == 3){
                if (args[0].equalsIgnoreCase("create")) return yourName(false);
                if (args[0].equalsIgnoreCase("addRoom")) return rooms(args[2]);
                if (args[0].equalsIgnoreCase("setIcon")) return matList(args[2]);
                if (args[0].equalsIgnoreCase("removeRoom")){
                    return levelRooms(args[1], args[2]);
                }
                if (args[0].equalsIgnoreCase("addObjective")) return objTypeList(args[2]);

            }
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

    private List<String> levels(String match){
        Set<String> levelIds = plugin.levelsFile.getConfig().getConfigurationSection("levels").getKeys(false);
        return filter(levelIds, match);
    }
    private List<String> rooms(String match){
        Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
        return filter(roomIds, match);
    }

    private List<String> levelRooms(String levelId, String match){
        if (plugin.levelsFile.getConfig().contains("levels." + levelId + ".rooms")){
            List<String> rooms = plugin.levelsFile.getConfig().getStringList("levels." + levelId + ".rooms");
            return filter(rooms, match);
        }
        return null;
    }

    private List<String> options(String match){
        List<String> options = new ArrayList<>();
        options.add("create");
        options.add("addRoom");
        options.add("delete");
        options.add("setIcon");
        options.add("removeRoom");
        options.add("load");
        options.add("unload");
        options.add("reload");
        options.add("setLobby");
        options.add("setSpawn");
        options.add("addObjective");
        options.add("removeObjective");
        return filter(options, match);
    }
    private List<String> yourName(boolean id){
        List<String> name = new ArrayList<>();
        if(id)
            name.add("[Level ID here]");
        else name.add("[Level name here]");
        return name;
    }


    private List<String> matList(String match){
        List<String> mats = new ArrayList<>();
        for(Material mat : Material.values()){
            mats.add(mat.name());
        }
        return filter(mats, match);
    }

    private List<String> objTypeList(String match){
        List<String> types = new ArrayList<>();
        for (ObjectiveType type : ObjectiveType.values()){
            types.add(type.name());
        }
        return filter(types, match);
    }



}

package piglinextraction.me.stephenminer.levels;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.objectives.Objective;
import piglinextraction.me.stephenminer.levels.objectives.RuneObj;
import piglinextraction.me.stephenminer.levels.rooms.Door;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.levels.spawners.Node;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;
import piglinextraction.me.stephenminer.mobs.boss.encounters.Encounter;
import piglinextraction.me.stephenminer.mobs.hordes.Horde;
import piglinextraction.me.stephenminer.player.DeathMarker;
import piglinextraction.me.stephenminer.player.GameStats;
import piglinextraction.me.stephenminer.player.loadout.LoadOut;
import piglinextraction.me.stephenminer.weapons.Flashlight;
import piglinextraction.me.stephenminer.weapons.Shield;

import java.util.*;

public class Level {
    public static List<Level> levels = new ArrayList<>();

    private final PiglinExtraction plugin;

    private String id;
    private String name;
    private List<Room> rooms;
    private List<UUID> players;
    private List<UUID> offline;
    private List<UUID> dead;
    private List<DeathMarker> markers;
    private List<Objective> objectives;
    private List<Encounter> encounters;
    private List<Horde> hordes;
    private Location spawn;
    private Location lobby;
    private Material icon;
    private WaitingRoom waitingRoom;
    private GameStats stats;

    private HashMap<UUID, PiglinEntity> spawned;

    private boolean start;



    public Level(PiglinExtraction plugin, String id, String name, Location spawn, Location lobby, List<Room> rooms){
        if (rooms == null) this.rooms = new ArrayList<>();
        else this.rooms = rooms;
        encounters = new ArrayList<>();
        players = new ArrayList<>();
        offline = new ArrayList<>();
        dead = new ArrayList<>();
        markers = new ArrayList<>();
        objectives = new ArrayList<>();
        stats = new GameStats(this);
        this.id = id;
        this.name = name;
        this.spawn = spawn;
        this.plugin = plugin;
        this.icon = Material.DIRT;
        this.hordes = new ArrayList<>();
        Level.levels.add(this);
        spawned = new HashMap<>();
        this.waitingRoom = new WaitingRoom(plugin, Level.levels.get(Level.levels.indexOf(this)));
        Bukkit.broadcastMessage("Level object 1");
    }
    public Level(PiglinExtraction plugin, String id, String name, Material icon, Location spawn, List<Room> rooms){
        if (rooms == null) this.rooms = new ArrayList<>();
        else this.rooms = rooms;
        encounters = new ArrayList<>();
        players = new ArrayList<>();
        offline = new ArrayList<>();
        dead = new ArrayList<>();
        markers = new ArrayList<>();
        objectives = new ArrayList<>();
        stats = new GameStats(this);
        this.id = id;
        this.name = name;
        this.spawn = spawn;
        this.plugin = plugin;
        this.icon = icon;
        this.hordes = new ArrayList<>();
        Level.levels.add(this);
        this.waitingRoom = new WaitingRoom(plugin, Level.levels.get(Level.levels.indexOf(this)));
        spawned = new HashMap<>();
        Bukkit.broadcastMessage("Level object 2");
    }
    public Level(PiglinExtraction plugin, String id, String name, List<Room> rooms){
        if (rooms == null) this.rooms = new ArrayList<>();
        else this.rooms = rooms;
        encounters = new ArrayList<>();
        players = new ArrayList<>();
        offline = new ArrayList<>();
        dead = new ArrayList<>();
        objectives = new ArrayList<>();
        stats = new GameStats(this);
        this.id = id;
        this.name = name;
        this.plugin = plugin;
        this.icon = Material.DIRT;
        Level.levels.add(this);
        this.hordes = new ArrayList<>();
        this.waitingRoom = new WaitingRoom(plugin, Level.levels.get(Level.levels.indexOf(this)));
        spawned = new HashMap<>();
        Bukkit.broadcastMessage("Level object 3");
    }


    public void load(){
        this.waitingRoom = new WaitingRoom(plugin, Level.levels.get(Level.levels.indexOf(this)));
        for (Room room : rooms){
            room.load();
        }
        for (Objective obj : objectives){
            obj.init();
        }
        for (UUID uuid : players){
            stats.addKill(uuid, 0);
            stats.addDowned(uuid, 0);
            stats.addRevived(uuid, 0);
        }
    }

    public void unload(){
        for (Room room : rooms){
            room.unload();
        }
        List<UUID> uuidList = new ArrayList<>(spawned.keySet());
        for (int i = uuidList.size()-1; i >= 0; i--){
            UUID uuid = uuidList.get(i);
            PiglinEntity entity = spawned.get(uuid);
            entity.getMob().setHealth(0);
            entity.getMob().remove();
        }
        spawned.clear();
    }

    public void reload(){
        unload();
        load();
    }


    public void spawnPlayer(Player player){
        player.getInventory().clear();
        player.clearTitle();
        player.sendTitle("Entering " + name, "");
        if (players.size() >= 4){
            player.sendMessage(ChatColor.RED + "Sorry, but this level is full!");
            return;
        }
        player.teleport(spawn);
        LoadOut kit = LoadOut.fromPlayer(player);
        if (kit != null){
            kit.getMeleWeapon();
            kit.getPrimaryRanged();
        }
        Shield shield = new Shield();
        player.getInventory().setItemInOffHand(shield.getItem());
        Shield.shields.put(player.getUniqueId(),shield);
        plugin.flashlights.put(player.getUniqueId(), new Flashlight(plugin, player, 7, 1, 6));
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        player.setHealth(40);
        players.add(player.getUniqueId());
    }
    public void revivePlayer(Player player){
        if (dead.contains(player.getUniqueId())){
            dead.remove(player.getUniqueId());
            players.add(player.getUniqueId());
            Location loc = player.getLocation();
            player.teleport(loc.clone().add(0,1,0));
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(10);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        }
    }
    public void removePlayer(Player player){
        players.remove(player.getUniqueId());
    }

    public void save(){
        String base = "levels." + id;
        if (spawn != null) plugin.levelsFile.getConfig().set(base + ".spawn", plugin.fromLoc(spawn));
        if (lobby != null) plugin.levelsFile.getConfig().set(base + ".lobby", plugin.fromLoc(lobby));
        plugin.levelsFile.getConfig().set(base + ".name", name);
        plugin.levelsFile.getConfig().set(base + ".mat", icon.name());
        List<String> roomIds = new ArrayList<>();
        for (Room room : rooms){
            roomIds.add(room.getId());
        }
        List<String> hordeIds = hordes.stream().map(Horde::getId).toList();
        plugin.levelsFile.getConfig().set(base + ".rooms", roomIds);
        plugin.levelsFile.getConfig().set(base + ".hordes",hordeIds);
        plugin.levelsFile.saveConfig();
    }

    public void delete(){
        levels.remove(this);
        String base = "levels." + id;
        plugin.levelsFile.getConfig().set(base, null);
        plugin.levelsFile.saveConfig();
    }
    /**
     *
     *
     * @return 0 = minor, 1 = agitated, 2 = severe
     **/
    public int agitationLevel(){
        float ratio = ((float) this.getCompleted()) / this.getObjectives().size();
        if (ratio < 0.5) return 0;
        else if (ratio < 0.8) return 1;
        else return 2;
    }

    public void monitorLevel(){
        new BukkitRunnable(){
            int old = players.size();
            int emptyCounter = 1240;
            @Override
            public void run(){
                if (!start){
                    this.cancel();
                    return;
                }
                if (players.size() == 0){
                    if (offline.size() > 0) {
                        if (emptyCounter <= 0) {
                            endGame();
                        }
                        emptyCounter--;
                    }else {
                        players.addAll(dead);
                        for (UUID uuid : players){
                            Player p = Bukkit.getPlayer(uuid);
                            p.sendMessage(ChatColor.RED + "Extraction failed, party deceased.");
                        }
                        endGame();
                    }
                }else emptyCounter = 1240;
                if (old != players.size()){
                    for (int a = 0; a < rooms.size(); a++){
                        Room room = rooms.get(a);
                        for (int b = 0; b < room.getNodes().size(); b++){
                            Node node = room.getNodes().get(b);
                            for (int c = 0; c < node.getEntities().size(); c++){
                                PiglinEntity piglinEntity = node.getEntities().get(c);
                                piglinEntity.getPlayerWhitelist().clear();
                                for (UUID uuid : players){
                                    piglinEntity.getPlayerWhitelist().add(uuid);
                                }
                            }
                        }
                    }
                    old = players.size();
                }
            }
        }.runTaskTimer(plugin, 5, 1);
    }

    public void checkExtraction(){
        World world = spawn.getWorld();
        new BukkitRunnable(){
            int timer = 0;
            final int max = 200;
            @Override
            public void run(){
                if (!start){
                    this.cancel();
                    return;
                }

                if (timer >= max){
                    players.addAll(dead);
                    for (UUID uuid : players){
                        Player p = Bukkit.getPlayer(uuid);
                        p.sendMessage(ChatColor.AQUA + "Mission Completed");
                    }
                    endGame();
                }
                Collection<Entity> entities = world.getNearbyEntities(spawn, 3, 3, 3);
                int complete = 0;
                for (Entity entity : entities){
                    if (entity instanceof Player player){
                        for (Objective objective : objectives){
                            if (objective.isComplete() && objective.getWhoCompleted().equals(player))complete++;
                        }
                        if (complete >= objectives.size()) {
                            if (timer % 20 == 0){
                                for (UUID uuid : players){
                                    Player p = Bukkit.getPlayer(uuid);
                                    p.sendActionBar(ChatColor.AQUA + "" + (max - timer) / 20 + " seconds until extraction");
                                }
                            }
                            timer+=5;
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 100, 5);
    }

    public void endGame(){
        start = false;
        for (UUID uuid : offline){
            plugin.rangedWeaponsP.remove(uuid);
            plugin.meleWeapons.remove(uuid);
            Flashlight light = plugin.flashlights.remove(uuid);
            if (light != null) light.toggle(false);
        }
        for (UUID uuid : dead){
            if (Bukkit.getOfflinePlayer(uuid).isOnline()){
                Player player = Bukkit.getPlayer(uuid);
                player.getInventory().clear();
                player.teleport(plugin.hub);
                for (PotionEffect effect : player.getActivePotionEffects()){
                    player.removePotionEffect(effect.getType());
                }
                plugin.rangedWeaponsP.remove(player.getUniqueId());
                plugin.meleWeapons.remove(player.getUniqueId());
                Flashlight light = plugin.flashlights.remove(uuid);
                if (light != null) light.toggle(false);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                player.setHealth(20);
                player.setExp(0);
                player.setLevel(0);
            }
        }
        for (UUID uuid : players){
            if (Bukkit.getOfflinePlayer(uuid).isOnline()){
                Player player = Bukkit.getPlayer(uuid);
                player.getInventory().clear();
                player.teleport(plugin.hub);
                for (PotionEffect effect : player.getActivePotionEffects()){
                    player.removePotionEffect(effect.getType());
                }
                player.openInventory(stats.loadMenu());
                plugin.rangedWeaponsP.remove(player.getUniqueId());
                plugin.meleWeapons.remove(player.getUniqueId());
                Flashlight light = plugin.flashlights.remove(uuid);
                if (light != null) light.toggle(false);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                player.setHealth(20);
                player.setExp(0);
                player.setLevel(0);
            }

        }
        offline.clear();
        dead.clear();
        unload();
        Bukkit.broadcastMessage("unloaded level");
        Level.levels.remove(this);
        for (Room room : rooms){
            for (Door door : room.getDoors()){
                door.resetDoor();
            }
            room.setKill(true);
            Room.BY_IDS.remove(room.getId());
        }
        for (Objective obj : objectives){
            obj.setKill(true);
        }
        rooms.clear();
        objectives.clear();
        Bukkit.broadcastMessage("Removed references of level object!");
    }




    public HashMap<UUID, PiglinEntity> getSpawned(){ return spawned; }

    public String getName(){ return name; }
    public String getId(){ return id; }
    public Material getIcon(){ return icon; }
    public void setIcon(Material icon){ this.icon = icon; }

    public void addRoom(Room room){
        rooms.add(room);
    }
    public void setRooms(List<Room> rooms){
        this.rooms = rooms;
    }
    public void removeRoom(Room room){
        rooms.remove(room);
    }
    public List<Room> getRooms(){ return rooms; }
    public boolean isStarted(){ return start; }
    public void start(boolean start){ this.start = start;}
    public Location getLobby(){ return lobby; }
    public void setLobby(Location lobby){ this.lobby = lobby; }
    public Location getSpawn(){ return spawn; }
    public void setSpawn(Location spawn){ this.spawn = spawn; }

    public List<UUID> getPlayers(){ return players; }
    public List<UUID> getOffline(){ return offline; }
    public List<UUID> getDead(){ return dead; }
    public List<Objective> getObjectives(){ return objectives; }
    public List<Horde> getHordes(){ return hordes; }

    public boolean hasObjective(Objective objective){
        return objectives.contains(objective);
    }
    public int getCompleted(){
        int completed = 0;
        for (Objective obj : objectives){
            if (obj.isComplete()) completed++;
        }
        return completed;
    }
    public void addObjective(Objective objective){ objectives.add(objective); }
    public void removeObjective(Objective objective){ objectives.remove(objective); }

    public WaitingRoom getWaitingRoom(){ return waitingRoom; }
    public void setWaitingRoom(WaitingRoom waitingRoom){ this.waitingRoom = waitingRoom; }

    public GameStats getStats(){
        return stats;
    }
    public List<Encounter> getEncounters(){ return encounters; }


    public void addHorde(Horde horde){
        hordes.add(horde);
    }

    public int loadSpecialTimer(){ return plugin.levelsFile.getConfig().getInt("levels." + id + ".special-timings"); }
    public int loadHordeTimer(){ return plugin.levelsFile.getConfig().getInt("levels." + id + ".horde-timings"); }
    /**
     * @return chance for hordes to spawn upon timer completion, 0-100
     */
    public int loadHordeChance(){ return plugin.levelsFile.getConfig().getInt("levels." + id + ".horde-chance"); }

    /**
     * @return chance for specials to spawn upon timer completion, 0-100
     */
    public int loadSpecialChance(){ return plugin.levelsFile.getConfig().getInt("levels." + id + ".special-chance"); }


    public static Level fromId(String id){
        for (Level level : Level.levels){
            if (level.getId().equalsIgnoreCase(id))
                return level;
        }
        return null;
    }
}

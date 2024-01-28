package piglinextraction.me.stephenminer;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.combat.Consumables;
import piglinextraction.me.stephenminer.commands.GiveCmd;
import piglinextraction.me.stephenminer.combat.PlayerMele;
import piglinextraction.me.stephenminer.combat.PlayerRanged;
import piglinextraction.me.stephenminer.commands.*;
import piglinextraction.me.stephenminer.events.*;
import piglinextraction.me.stephenminer.events.custom.PlayerNoiseEvent;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.LevelGroup;
import piglinextraction.me.stephenminer.player.DeathMarker;
import piglinextraction.me.stephenminer.player.GameProfile;
import piglinextraction.me.stephenminer.weapons.Flashlight;
import piglinextraction.me.stephenminer.weapons.mele.MeleWeapon;
import piglinextraction.me.stephenminer.weapons.ranged.RangedWeapon;

import java.util.*;

public final class PiglinExtraction extends JavaPlugin {
    public Location hub;

    public HashMap<UUID, RangedWeapon> rangedWeaponsP;
    public HashMap<UUID, MeleWeapon> meleWeapons;
    public HashMap<UUID, Flashlight> flashlights;

    public List<GameProfile> profiles;
    public List<DeathMarker> markers;


    public ConfigFile worldsFile;
    public ConfigFile levelsFile;
    public ConfigFile lockersFile;
    public ConfigFile roomsFile;
    public ConfigFile groupsFile;
    public ConfigFile playersFile;
    public ConfigFile hordesFile;
    @Override
    public void onEnable() {
       this.worldsFile = new ConfigFile(this, "worlds");
       this.levelsFile = new ConfigFile(this, "levels");
       this.lockersFile = new ConfigFile(this, "lockers");
       this.roomsFile = new ConfigFile(this, "rooms");
       this.groupsFile = new ConfigFile(this, "groups");
       this.playersFile = new ConfigFile(this, "players");
       this.hordesFile = new ConfigFile(this, "hordes");
       //loadLevels();
       loadGroups();
       loadHub();
       rangedWeaponsP = new HashMap<>();
       meleWeapons = new HashMap<>();
       flashlights = new HashMap<>();
       profiles = new ArrayList<>();
       markers = new ArrayList<>();
       registerListeners();
       registerCommands();
       fireCollision();
    }

    @Override
    public void onDisable() {
        Level.levels.clear();
        LevelGroup.levelGroups.clear();
        levelsFile.saveConfig();
        roomsFile.saveConfig();
        lockersFile.saveConfig();
        playersFile.saveConfig();
        worldsFile.saveConfig();
        for (UUID uuid : flashlights.keySet()){
            Flashlight flashlight = flashlights.get(uuid);
            flashlight.toggle(false);
            Location loc = flashlight.getPreviousLoc();
            loc.getBlock().setType(flashlight.getPreviousMat());
            loc.getBlock().setBlockData(flashlight.getPreviousData());
        }
        // Plugin shutdown logic
    }

    private void registerListeners(){
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new PlayerMele(this), this);
        manager.registerEvents(new PlayerRanged(this), this);
        manager.registerEvents(new MobEvents(), this);
        manager.registerEvents(new NoiseEvents(this), this);
        manager.registerEvents(new GuiEvents(this), this);
        manager.registerEvents(new LevelSetUpEvents(this), this);
        manager.registerEvents(new PlayerEvents(this), this);
        manager.registerEvents(new DoorEvents(this), this);
        manager.registerEvents(new Consumables(this), this);
    }
    private void registerCommands(){
        Testing testing = new Testing(this);
        RoomCmd roomCmd = new RoomCmd(this);
        LockerCmd lockerCmd = new LockerCmd(this);
        LevelCmd levelCmd = new LevelCmd(this);
        SpawnerCmd spawnerCmd = new SpawnerCmd(this);
        LevelGroupCmd levelGroupCmd = new LevelGroupCmd(this);
        JoinCmd joinCmd = new JoinCmd(this);
        DoorCmd doorCmd = new DoorCmd(this);
        GiveCmd giveCmd = new GiveCmd(this);
        HordeCmd hordeCmd = new HordeCmd(this);



        getCommand("room").setExecutor(roomCmd);
        getCommand("room").setTabCompleter(roomCmd);
        getCommand("testing").setExecutor(testing);
        getCommand("testing").setTabCompleter(testing);
        getCommand("locker").setExecutor(lockerCmd);
        getCommand("locker").setTabCompleter(lockerCmd);
        getCommand("roomWand").setExecutor(new RoomWand(this));
        getCommand("setHub").setExecutor(new SetHub(this));
        getCommand("level").setExecutor(levelCmd);
        getCommand("level").setTabCompleter(levelCmd);
        getCommand("spawner").setExecutor(spawnerCmd);
        getCommand("spawner").setTabCompleter(spawnerCmd);
        getCommand("levelGroup").setExecutor(levelGroupCmd);
        getCommand("levelGroup").setTabCompleter(levelGroupCmd);
        getCommand("join").setExecutor(joinCmd);
        getCommand("join").setTabCompleter(joinCmd);
        getCommand("door").setExecutor(doorCmd);
        getCommand("door").setTabCompleter(doorCmd);
        getCommand("pegive").setExecutor(giveCmd);
        getCommand("pegive").setTabCompleter(giveCmd);
        getCommand("peHorde").setExecutor(hordeCmd);
        getCommand("peHorde").setTabCompleter(hordeCmd);

    }

    public boolean isPluginWorld(World world){
        return worldsFile.getConfig().contains("worlds." + world.getName());
    }

    public String fromLoc(Location loc){
        World world = loc.getWorld();
        return world.getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }
    public String fromBlockLoc(Location loc){
        World world = loc.getWorld();
        return world.getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
    public String fromBlocKLoc(World world, int x, int y, int z){
        return world.getName() + "," + x + "," + y + "," + z;
    }

    public Location fromString(String str){
        String[] split = str.split(",");
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        try{
            World world = Bukkit.getWorld(split[0]);
            return new Location(world, x,y, z);
        }catch (Exception e){
            getLogger().log(java.util.logging.Level.WARNING, "Attempted to get world from config, world is null, attempting to load world now...");
        }
        World world = Bukkit.createWorld(new WorldCreator(split[0]));
        return new Location(world, x, y, z);
    }

    public void loadGroups(){
        if (groupsFile.getConfig().contains("groups")){
            for (String key : groupsFile.getConfig().getConfigurationSection("groups").getKeys(false)){
                LevelGroup group = LevelGroup.fromString(this, key);
            }
        }
    }

    public void loadLevels(){
        for (String key : levelsFile.getConfig().getConfigurationSection("levels").getKeys(false)){
            Level level = Level.fromString(this, key);
        }
    }
    public void loadHub(){
        if (worldsFile.getConfig().contains("hub")){
            Location loc = null;
            try{

            }catch (Exception e){
                getLogger().log(java.util.logging.Level.WARNING, "Something went wrong loading hub location! Make sure world is loaded and file entry isn't damaged!");
            }
            if (loc != null)
                hub = loc;
            else hub = getServer().getWorlds().get(0).getSpawnLocation();
        }
    }


    public void fireCollision(){
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Level level : Level.levels){
                    for (UUID uuid : level.getPlayers()){
                        Player player = Bukkit.getPlayer(uuid);
                        if (player == null) continue;
                        Collection<Entity> entities = player.getNearbyEntities(1.5,1.5,1.5);
                        for (Entity entity : entities){
                            if (entity instanceof Piglin){
                                if (player.getBoundingBox().overlaps(entity.getBoundingBox())){
                                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PIGLIN_ANGRY, 1, 1);
                                    getServer().getPluginManager().callEvent(new PlayerNoiseEvent(player, 3));
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 5);
    }


}

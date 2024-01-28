package piglinextraction.me.stephenminer.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.BoundingBox;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.commands.DoorCmd;
import piglinextraction.me.stephenminer.containers.Items;
import piglinextraction.me.stephenminer.events.custom.UseDoorEvent;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.rooms.Door;
import piglinextraction.me.stephenminer.levels.rooms.Room;

import java.util.*;

public class DoorEvents implements Listener {
    private final PiglinExtraction plugin;
    private final HashMap<UUID, Location> loc1s;
    private final HashMap<UUID, Location> loc2s;
    private final HashMap<UUID, Location> triggers;
    private final HashMap<UUID, String> canCreate;

    private final HashMap<UUID, Long> cooldown;

    public DoorEvents(PiglinExtraction plugin){
        this.plugin = plugin;
        triggers = new HashMap<>();
        loc1s = new HashMap<>();
        loc2s = new HashMap<>();
        canCreate = new HashMap<>();
        cooldown = new HashMap<>();
    }


    @EventHandler
    public void openDoor(PlayerInteractEvent event){
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            String sLoc = plugin.fromBlockLoc(block.getLocation());
            for (Level level : Level.levels){
                for (Room room : level.getRooms()){
                    for (Door door : room.getDoors()){
                        for (String trigger : door.triggers()){
                            if (sLoc.equals(trigger)){
                                if (door.isOpen()){
                                    door.setOpen(false);
                                    door.close();
                                    Bukkit.getServer().getPluginManager().callEvent(new UseDoorEvent(door,false,event.getPlayer()));
                                }else {
                                    door.setOpen(true);
                                    door.open();
                                    Bukkit.getServer().getPluginManager().callEvent(new UseDoorEvent(door,true,event.getPlayer()));
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPosSet(PlayerInteractEvent event){
        Action action = event.getAction();
        Player player = event.getPlayer();
        Location loc = player.getLocation().getBlock().getLocation();
        if (event.getClickedBlock() != null) loc = event.getClickedBlock().getLocation();
        if (getRoomIn(loc).equals("null")) return;
        String region = getRoomIn(loc);
        Items items = new Items(plugin);
        if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) return;
        if (event.hasItem() && items.hasLore(event.getItem(), "door-wand")) {
            switch (action) {
                case LEFT_CLICK_AIR -> {
                    loc1s.put(player.getUniqueId(), player.getLocation().getBlock().getLocation());
                    player.sendMessage(ChatColor.GREEN + "Pos 1 set");

                }
                case RIGHT_CLICK_AIR -> {
                    loc2s.put(player.getUniqueId(), player.getLocation().getBlock().getLocation());
                    player.sendMessage(ChatColor.GREEN + "Pos 2 set");
                }
                case LEFT_CLICK_BLOCK -> {
                    loc1s.put(player.getUniqueId(), loc);
                    player.sendMessage(ChatColor.GREEN + "Pos 1 set");
                }
                case RIGHT_CLICK_BLOCK -> {
                    loc2s.put(player.getUniqueId(), loc);
                    player.sendMessage(ChatColor.GREEN + "Pos 2 set");
                }
            }
            event.setCancelled(true);
            cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 500);
        }else if (loc1s.containsKey(player.getUniqueId()) && loc2s.containsKey(player.getUniqueId())){
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                triggers.put(player.getUniqueId(), loc);
                Bukkit.broadcastMessage("A");
            }
        }
        if (loc1s.containsKey(player.getUniqueId()) && loc2s.containsKey(player.getUniqueId())){
            player.sendMessage(ChatColor.GREEN + "Please right click on the block that you want to trigger or open/close the door!");
        }
        if (triggers.containsKey(player.getUniqueId())){
            canCreate.put(player.getUniqueId(), region);
            player.sendMessage(ChatColor.GREEN + "Please type the name of your door in chat!");
        }
    }



    @EventHandler
    public void addTrigger(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!DoorCmd.adding.containsKey(player.getUniqueId())) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            String doorId = DoorCmd.adding.get(player.getUniqueId());
            String roomId = roomIn(doorId);
            if (roomId.equals("null")) return;
            List<String> triggers = plugin.roomsFile.getConfig().getStringList("rooms." + roomId + ".doors." + doorId + ".triggers");
            triggers.add(plugin.fromBlockLoc(block.getLocation()));
            plugin.roomsFile.getConfig().set("rooms." + roomId + ".doors." + doorId + ".triggers", triggers);
            plugin.roomsFile.saveConfig();
            player.sendMessage(ChatColor.GREEN + "Added trigger to door " + doorId + " in room " + roomId);
            DoorCmd.adding.remove(player.getUniqueId());
        }
    }





    public String roomIn(String door){
        if (plugin.roomsFile.getConfig().contains("rooms")){
            Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
            for (String roomId : roomIds){
                if (plugin.roomsFile.getConfig().contains("rooms." + roomId + ".doors." + door)) return roomId;
            }
        }
        return "null";
    }



    public String getRoomIn(Location loc){
        Set<String> rooms = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
        for (String id : rooms){
            Location loc1 = plugin.fromString(plugin.roomsFile.getConfig().getString("rooms." + id + ".loc1"));
            Location loc2 = plugin.fromString(plugin.roomsFile.getConfig().getString("rooms." + id + ".loc2"));
            if (BoundingBox.of(loc1.clone().add(0.5,0.5,0.5), loc2.clone().add(0.5,0.5,0.5)).overlaps(loc.getBlock().getBoundingBox())){
                return id;
            }
        }
        return "null";
    }

    @EventHandler
    public void nameDoor(AsyncChatEvent event){
        Player player = event.getPlayer();
        if (canCreate.containsKey(player.getUniqueId())){
            String doorName = PlainTextComponentSerializer.plainText().serialize(event.message());
            UUID uuid = player.getUniqueId();
            String roomId = canCreate.get(uuid);
            Location loc1 = loc1s.get(uuid);
            Location loc2 = loc2s.get(uuid);
            Location trigger = triggers.get(uuid);
            saveDoor(roomId, doorName, loc1, loc2, trigger);
            player.sendMessage(ChatColor.GREEN + "Added door to room" + roomId + "!");
            player.sendMessage(ChatColor.YELLOW + "If you want to add more trigger locs, use /door " + doorName + " addTrigger");
            loc1s.remove(uuid);
            loc2s.remove(uuid);
            triggers.remove(uuid);
            canCreate.remove(uuid);
        }
    }



    @EventHandler
    public void clearDataContainers(PlayerQuitEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        loc1s.remove(uuid);
        loc2s.remove(uuid);
        triggers.remove(uuid);
        canCreate.remove(uuid);
        cooldown.remove(uuid);
        DoorCmd.adding.remove(uuid);
    }

    private void saveDoor(String roomId, String doorName, Location loc1, Location loc2, Location trigger){
        String path = "rooms." + roomId + ".doors." + doorName;
        plugin.roomsFile.getConfig().set(path + ".loc1", plugin.fromBlockLoc(loc1));
        plugin.roomsFile.getConfig().set(path + ".loc2", plugin.fromBlockLoc(loc2));
        plugin.roomsFile.getConfig().set(path + ".do-spaces", true);
        List<String> triggers = new ArrayList<>();
        triggers.add(plugin.fromBlockLoc(trigger));
        plugin.roomsFile.getConfig().set(path + ".triggers", triggers);
        plugin.roomsFile.saveConfig();
    }

    // gets regionId that doorId is in
    private String roomId(String doorId){
        Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
        for (String id : roomIds){
            if (plugin.roomsFile.getConfig().contains("rooms." + id + ".doors." + doorId)) return id;
        }
        return null;
    }

    private boolean isInRoom(String roomId, String doorId){
        return plugin.roomsFile.getConfig().contains("rooms." + roomId + ".doors." + doorId);
    }
}

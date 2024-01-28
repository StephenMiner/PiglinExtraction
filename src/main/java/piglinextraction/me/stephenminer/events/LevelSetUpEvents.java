package piglinextraction.me.stephenminer.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.commands.LevelCmd;
import piglinextraction.me.stephenminer.containers.Items;
import piglinextraction.me.stephenminer.containers.Locker;
import piglinextraction.me.stephenminer.levels.objectives.Objective;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.levels.spawners.Node;
import piglinextraction.me.stephenminer.mobs.PiglinType;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LevelSetUpEvents implements Listener {
    private final PiglinExtraction plugin;
    private final Items items;

    public HashMap<UUID, Location> loc1s = new HashMap<>();
    public HashMap<UUID, Location> loc2s = new HashMap<>();
    public HashMap<UUID, Boolean> canMake = new HashMap<>();
    public HashMap<UUID, Long> objCooldown = new HashMap<>();

    public LevelSetUpEvents(PiglinExtraction plugin) {
        this.plugin = plugin;
        items = new Items(plugin);
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        Block block = event.getBlock();
        if (!plugin.roomsFile.getConfig().contains("rooms")) return;
        for (String entry : Room.BY_IDS.keySet()) {
            Room room = Room.BY_IDS.get(entry);
            if (BoundingBox.of(room.getCorner1().clone().add(0.5, 0.5, 0.5), room.getCorner2().clone().add(0.5, 0.5, 0.5)).overlaps(block.getBoundingBox())) {
                if (!room.editModeOn()){
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot build here right now!");
                    return;
                }else if(!player.hasPermission("pe.regions.build")){
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You don't have permission build here right now!");
                    return;
                }
                /*
                for (String id : plugin.lockersFile.getConfig().getConfigurationSection("lockers").getKeys(false)) {
                    if (items.hasLore(item, id)) {
                        BlockData data = block.getBlockData();
                        boolean locked = plugin.lockersFile.getConfig().getBoolean("lockers." + id + ".locked");
                        Locker locker = new Locker(plugin, room.getId(), id, block.getLocation(), block.getType(), locked);
                        locker.getLocation().getBlock().setBlockData(data);
                        locker.save();
                        room.addLocker(locker);
                        player.sendMessage(ChatColor.GREEN + "Added locker to room " + room.getId() + "!");
                        return;
                    }
                }

                 */
            }
        }
        if (!player.hasPermission("pe.regions.build")) return;
        Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
        for (String entry : roomIds){
            String base = "rooms." + entry;
            Location loc1 = plugin.fromString(plugin.roomsFile.getConfig().getString(base + ".loc1"));
            Location loc2 = plugin.fromString(plugin.roomsFile.getConfig().getString(base + ".loc2"));
            BoundingBox box = BoundingBox.of(loc1.clone().add(0.5,0.5,0.5), loc2.clone().add(0.5,0.5,0.5));
            if (box.overlaps(block.getBoundingBox())){
                Set<String> lockerIds = plugin.lockersFile.getConfig().getConfigurationSection("lockers").getKeys(false);
                for (String lockerId : lockerIds){
                    if (items.hasLore(item, lockerId)){
                        String path = "rooms." + entry + ".lockers." + plugin.fromBlockLoc(block.getLocation());
                        plugin.roomsFile.getConfig().set(path + ".type", lockerId);
                        plugin.roomsFile.getConfig().set(path + ".locked", plugin.lockersFile.getConfig().getBoolean("lockers." + lockerId + ".locked"));
                        plugin.roomsFile.getConfig().set(path + ".mat", block.getType().name());
                        plugin.roomsFile.getConfig().set(path + ".data", block.getBlockData().getAsString());
                        plugin.roomsFile.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Added locker to room " + entry + "!");
                        return;
                    }
                }
            }
        }

    }
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!plugin.roomsFile.getConfig().contains("rooms")) return;
        for (String id : Room.BY_IDS.keySet()) {

            Room room = Room.BY_IDS.get(id);
            if (BoundingBox.of(room.getCorner1().clone().add(0.5, 0.5, 0.5), room.getCorner2().clone().add(0.5, 0.5, 0.5)).overlaps(block.getBoundingBox())) {
                Bukkit.broadcastMessage("" + room.editModeOn());
                if (!room.editModeOn()) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot build in " + room.getId() + " right now!");
                    return;
                } else if (!player.hasPermission("pe.regions.build")) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You don't have permission build here right now!");
                    return;
                }
                for (int i = 0; i < room.getNodes().size(); i++){
                    Node node = room.getNodes().get(i);
                    if (plugin.fromBlockLoc(block.getLocation()).equalsIgnoreCase(plugin.fromBlockLoc(node.getBase()))) {
                        room.removeNode(block.getLocation());
                        room.save();
                        return;
                    }
                }
                for (Locker locker : room.getLockers()){
                    if (locker.getLocation().equals(block.getLocation())){
                        plugin.roomsFile.getConfig().set("rooms." + room.getId() + ".lockers." + plugin.fromBlockLoc(locker.getLocation()), null);
                        plugin.roomsFile.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Removed locker from room " + room.getId() + "!");
                        return;
                    }
                }
            }

        }
        if (!plugin.roomsFile.getConfig().contains("rooms")) return;
        Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
        String bLoc = plugin.fromBlockLoc(block.getLocation());
        for (String id : roomIds){
            if (plugin.roomsFile.getConfig().contains("rooms." + id + ".lockers")){
                Set<String> lockers = plugin.roomsFile.getConfig().getConfigurationSection("rooms." + id + ".lockers").getKeys(false);
                for (String locker : lockers){
                    if (bLoc.equals(locker)){
                        plugin.roomsFile.getConfig().set("rooms." + id + ".lockers." + locker, null);
                        plugin.roomsFile.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Removed locker from rooom " + id);
                        return;
                    }
                }
            }
            if (plugin.roomsFile.getConfig().contains("rooms." + id + ".nodes")){
                Set<String> nodes = plugin.roomsFile.getConfig().getConfigurationSection("rooms." + id + ".nodes").getKeys(false);
                for (String node : nodes){
                    if (bLoc.equals(node)){
                        plugin.roomsFile.getConfig().set("rooms." + id + ".nodes." + node, null);
                        plugin.roomsFile.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Removed node from room " + id);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        Action action = event.getAction();
        UUID id = player.getUniqueId();
        if (items.hasLore(item, "room-wand")) {
            event.setCancelled(true);
            switch (action) {
                case RIGHT_CLICK_BLOCK -> {
                    loc2s.put(id, event.getClickedBlock().getLocation());
                    player.sendMessage(ChatColor.GREEN + "Corner 2 set!");
                }
                case LEFT_CLICK_BLOCK -> {
                    loc1s.put(id, event.getClickedBlock().getLocation());
                    player.sendMessage(ChatColor.GREEN + "Corner 1 set!");
                }
                case RIGHT_CLICK_AIR -> {
                    loc2s.put(id, player.getLocation().getBlock().getLocation());
                    player.sendMessage(ChatColor.GREEN + "Corner 2 set!");
                }
                case LEFT_CLICK_AIR -> {
                    loc1s.put(id, player.getLocation().getBlock().getLocation());
                    player.sendMessage(ChatColor.GREEN + "Corner 1 set!");
                }
            }
            if (loc1s.containsKey(id) && loc2s.containsKey(id)) {
                canMake.put(id, true);
                player.sendMessage(ChatColor.GREEN + "Type out the name of your room in chat!");
            }
        }
        if (items.hasLore(item, "node")){
            if (player.getCooldown(Material.STICK) > 0) return;
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Block block = event.getClickedBlock();
                if (!plugin.roomsFile.getConfig().contains("rooms")) return;
                Set<String> roomIds = plugin.roomsFile.getConfig().getConfigurationSection("rooms").getKeys(false);
                for (String entry : roomIds){
                    String base = "rooms." + entry;
                    Location loc1 = plugin.fromString(plugin.roomsFile.getConfig().getString(base + ".loc1")).clone().add(0.5,0.5,0.5);
                    Location loc2 = plugin.fromString(plugin.roomsFile.getConfig().getString(base + ".loc2")).clone().add(0.5,0.5,0.5);
                    BoundingBox box = BoundingBox.of(loc1, loc2);
                    if (box.overlaps(block.getBoundingBox())){
                        setNode(item, entry, block.getLocation());
                        player.setCooldown(Material.STICK, 20);
                        player.sendMessage(ChatColor.GREEN + "Added node to this location in room " + entry + "!");
                        return;
                    }
                }
                /*
                for (String entry : Room.BY_IDS.keySet()) {
                    Room room = Room.BY_IDS.get(entry);
                    if (BoundingBox.of(room.getCorner1().clone().add(0.5,0.5,0.5), room.getCorner2().clone().add(0.5,0.5,0.5)).overlaps(block.getBoundingBox())){
                        setNode(item, room, block.getLocation());
                        player.setCooldown(Material.STICK, 20);
                        player.sendMessage(ChatColor.GREEN + "Added node to this location in room " + room.getId());
                        return;
                    }
                }

                 */
            }
        }

    }

    @EventHandler
    public void defineObjectives(PlayerInteractEvent event){
        if (!event.getAction().isRightClick()) return;
        if (!event.hasBlock()) return;
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (objCooldown.containsKey(player.getUniqueId()) && objCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) return;
        if (LevelCmd.addObj.containsKey(player.getUniqueId())){
            Objective obj = LevelCmd.addObj.get(player.getUniqueId());
            obj.addSpawn(block.getLocation().add(0,1,0));
            player.sendMessage(ChatColor.GREEN + "Added location to potential spawn list for objective");
            objCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 400);
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (canMake.containsKey(id)) {
            String msg = PlainTextComponentSerializer.plainText().serialize(event.message());
            plugin.roomsFile.getConfig().set("rooms." + msg + ".loc1", plugin.fromBlockLoc(loc1s.get(player.getUniqueId())));
            plugin.roomsFile.getConfig().set("rooms." + msg + ".loc2", plugin.fromBlockLoc(loc2s.get(player.getUniqueId())));
            plugin.roomsFile.saveConfig();
            player.sendMessage(ChatColor.GREEN + "Created room " + msg);
            event.setCancelled(true);
        }
        if (LevelCmd.addObj.containsKey(id)){
            String msg = PlainTextComponentSerializer.plainText().serialize(event.message());
            if (msg.equalsIgnoreCase("done")){
                event.setCancelled(true);
                if (!LevelCmd.tiedLevel.containsKey(id)) {
                    player.sendMessage(ChatColor.RED + "Something went wrong! Please try again. If the issue persists, contact Meep or JimmyTheSheep");
                    LevelCmd.tiedLevel.remove(id);
                    LevelCmd.addObj.remove(id);
                    player.sendMessage(ChatColor.YELLOW + "You are no longer editing objectives");
                    return;
                }
                Objective obj = LevelCmd.addObj.get(id);
                if (obj.getSpawns().isEmpty()){
                    player.sendMessage(ChatColor.RED + "You need to set potential spawn locations for this objective! If you don't want to create this objective, type 'cancel' instead of done. To actually add spawn locs, right click the block you'd want the objective to spawn on!");
                    return;
                }

                obj.save(LevelCmd.tiedLevel.get(id));
                LevelCmd.addObj.remove(id);
                LevelCmd.tiedLevel.remove(id);
                player.sendMessage(ChatColor.YELLOW + "You are no longer editing objectives");
                player.sendMessage(ChatColor.GREEN + "Now done adding objectives to the level");
            }
        }
    }




    private void setNode(ItemStack item, String roomId, Location loc){
        if (!(item.hasItemMeta() && item.getItemMeta().hasLore())) return;
        List<String> lore = item.getItemMeta().getLore();
        PiglinType type = null;
        int radius = -1;
        int interval = -1;
        for (String entry : lore){
            String temp = ChatColor.stripColor(entry);
            if (temp.contains("Spawning: ")) type = PiglinType.valueOf(temp.replace("Spawning: ", ""));
            if (temp.contains("Radius: ")) radius = Integer.parseInt(temp.replace("Radius: ", ""));
            if (temp.contains("Interval: ")) interval = Integer.parseInt(temp.replace("Interval: ", ""));
        }
        if (type != null && radius >= 0){
            String base = "rooms." + roomId + ".nodes." + plugin.fromBlockLoc(loc);
            plugin.roomsFile.getConfig().set(base + ".entity", type.name());
            plugin.roomsFile.getConfig().set(base + ".radius", radius);
            if (interval >= 0){
                plugin.roomsFile.getConfig().set(base + ".interval", interval);
                /*
                Repeater repeater = new Repeater(plugin, loc, radius, interval, room, type);
                room.addNode(repeater);
                room.save();
                 */
            }
            plugin.roomsFile.saveConfig();
            /*
            Node node = new Node(plugin, loc, radius, room, type);
            room.addNode(node);
            room.save();
            */
        }
    }



}

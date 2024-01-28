package piglinextraction.me.stephenminer.levels;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.player.DisplayItems;

import java.util.HashMap;
import java.util.UUID;

public class WaitingRoom {
    private final PiglinExtraction plugin;
    private final Level level;
    private HashMap<UUID, Boolean> readyPlayers;
    public WaitingRoom(PiglinExtraction plugin, Level level){
        readyPlayers = new HashMap<>();
        this.plugin = plugin;
        this.level = level;
        monitorLobby();
    }

    public boolean addPlayer(Player player){
        if (level.isStarted()){
            level.spawnPlayer(player);
            player.sendMessage(ChatColor.GREEN + "Dropping you into the zone");
            return true;
        }
        if (readyPlayers.size() == 2){
            player.sendMessage(ChatColor.RED + "Lobby is full!");
            return false;
        }
        for (UUID uuid : readyPlayers.keySet()){
            plugin.getServer().getPlayer(uuid).sendMessage(ChatColor.GREEN + player.getName() + " has joined! (" + readyPlayers.size() + "/4)");
        }
        player.sendMessage(ChatColor.GREEN + "Joined Lobby " + level.getName());
        readyPlayers.put(player.getUniqueId(), false);
        DisplayItems displayItems = new DisplayItems();
        player.teleport(level.getLobby());
        player.getInventory().setItem(0, readyUp(player.getUniqueId()));
        player.getInventory().setItem(4, displayItems.selectorItem());
        player.getInventory().setItem(8, quit());
        return true;
    }
    public void removePlayer(Player player){
        player.getInventory().clear();
        readyPlayer(player, false);
        readyPlayers.remove(player.getUniqueId());
        for (UUID uuid : readyPlayers.keySet()){
            plugin.getServer().getPlayer(uuid).sendMessage(ChatColor.RED + player.getName() + " has Quit! (" + readyPlayers.size() + "/4)");
        }
        player.teleport(plugin.hub);
    }

    public boolean hasPlayer(Player player){
        return readyPlayers.containsKey(player.getUniqueId());
    }


    public void readyPlayer(Player player, boolean ready){
        Material mat = ready ? Material.LIME_DYE : Material.RED_DYE;
        String msg = ready ? ChatColor.GREEN + player.getName() + " has readied!" : ChatColor.RED + player.getName() + " has unreadied!";
        readyPlayers.put(player.getUniqueId(), ready);
        msg += " (" + playersReady() + "/4)";
        for(UUID uuid : readyPlayers.keySet()){
            plugin.getServer().getPlayer(uuid).sendMessage(msg);
        }
        int slot = player.getInventory().first(mat);
        player.getInventory().setItem(slot, readyUp(player.getUniqueId()));
    }


    public void monitorLobby(){
        new BukkitRunnable(){
            int titleCount = 200;
            @Override
            public void run(){
                if (level.isStarted()) {
                    this.cancel();
                    return;
                }
                if (readyPlayers.size() == 2){
                    if (allReady()){
                        if (titleCount % 20 == 0) {
                            for (UUID uuid : readyPlayers.keySet()) {
                                Player player = plugin.getServer().getPlayer(uuid);
                                player.sendTitle("Starting in " + titleCount/20, "");
                            }
                        }
                        if (titleCount == 0){
                            for (UUID uuid : readyPlayers.keySet()){
                                Player player = plugin.getServer().getPlayer(uuid);
                                level.spawnPlayer(player);
                            }
                            level.load();
                            level.start(true);
                            level.monitorLevel();
                            level.checkExtraction();
                        }
                        titleCount--;
                    }else titleCount = 200;
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    public boolean allReady(){
        return playersReady() == readyPlayers.size();
    }

    public int playersReady(){
        int ready = 0;
        for (UUID uuid : readyPlayers.keySet()){
            if (readyPlayers.get(uuid)) ready++;
        }
        return ready;
    }

    public HashMap<UUID, Boolean> getReadyPlayers(){ return readyPlayers; }


    public ItemStack readyUp(UUID uuid){
        boolean ready = readyPlayers.get(uuid);
        Material mat = ready ? Material.RED_DYE : Material.LIME_DYE;
        String name = ready ? ChatColor.RED + "Click to unready" : ChatColor.GREEN + "Click to ready";
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack quit(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Leave to hub");
        item.setItemMeta(meta);
        return item;
    }
}

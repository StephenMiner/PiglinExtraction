package piglinextraction.me.stephenminer.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import piglinextraction.me.stephenminer.levels.Level;

import java.util.HashMap;
import java.util.UUID;

public class GameStats {
    private final Level level;

    private final HashMap<UUID, Integer> kills;
    private final HashMap<UUID, Integer> revives;
    private final HashMap<UUID, Integer> downed;


    public GameStats(Level level){
        this.level = level;
        kills = new HashMap<>();
        revives = new HashMap<>();
        downed = new HashMap<>();

    }






    public ItemStack kills(UUID uuid){
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "kills: " + kills.get(uuid));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack revives(UUID uuid){
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "revives: " + revives.get(uuid));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack downed(UUID uuid){
        ItemStack item = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "downed: " + downed.get(uuid));
        item.setItemMeta(meta);
        return item;
    }

    public Inventory loadMenu(){
        Inventory inv = Bukkit.createInventory(null, 45, "Piglin Stats");
        for (int i = 0; i < level.getPlayers().size(); i++){
            UUID uuid = level.getPlayers().get(i);
            inv.setItem(i*9 + 0, playerHead(uuid));
            inv.setItem(i*9 + 1, kills(uuid));
            inv.setItem(i*9 + 2, revives(uuid));
            inv.setItem(i*9 + 3, downed(uuid));
        }
        return inv;
    }


    public void addKill(UUID uuid, int add){
        if (kills.containsKey(uuid))
            kills.put(uuid, kills.get(uuid) + add);
        else kills.put(uuid, add);
    }

    public void addRevived(UUID uuid, int add){
        if (revives.containsKey(uuid))
            revives.put(uuid, revives.get(uuid) + add);
        else revives.put(uuid, add);
    }

    public void addDowned(UUID uuid, int add){
        if (downed.containsKey(uuid))
            downed.put(uuid, downed.get(uuid) + add);
        else downed.put(uuid, add);
    }

    public ItemStack playerHead(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName(ChatColor.AQUA + player.getName());
        item.setItemMeta(meta);
        return item;
    }



    public Level getLevel(){
        return level;
    }
}

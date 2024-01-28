package piglinextraction.me.stephenminer.events;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import piglinextraction.me.stephenminer.PiglinExtraction;

public class ProtectWorlds implements Listener {

    private final PiglinExtraction plugin;

    public ProtectWorlds(PiglinExtraction plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        World world = player.getWorld();
        event.setCancelled(isInWorld(world));
        player.sendMessage(ChatColor.RED + "You cannot break blocks here");
    }
    @EventHandler
    public void onBreak(BlockPlaceEvent event){
        Player player = event.getPlayer();
        World world = player.getWorld();
        event.setCancelled(isInWorld(world));
        player.sendMessage(ChatColor.RED + "You cannot place blocks here");
    }

    private boolean isInWorld(World world){
        String name = world.getName();
        if (plugin.worldsFile.getConfig().contains("worlds"))
            for (String key : plugin.worldsFile.getConfig().getConfigurationSection("worlds").getKeys(false)){
                if (key.equalsIgnoreCase(name)) return true;
            }
        return false;
    }
}

package piglinextraction.me.stephenminer.events;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.containers.Items;
import piglinextraction.me.stephenminer.containers.Locker;
import piglinextraction.me.stephenminer.events.custom.PlayerNoiseEvent;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.weapons.mele.MeleWeapon;

public class NoiseEvents implements Listener {
    private final PiglinExtraction plugin;
    public NoiseEvents(PiglinExtraction plugin){ this.plugin = plugin; }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if (player.isSneaking()) return;
        if (locDifferent(event.getFrom(), event.getTo())){
            int intensity = player.isSprinting() ? 9 : 4;
            plugin.getServer().getPluginManager().callEvent(new PlayerNoiseEvent(player, intensity));
        }
    }

    @EventHandler
    public void openLocker(PlayerInteractEvent event){
        if (!event.hasBlock()) return;
        World world = event.getPlayer().getWorld();
        Player player = event.getPlayer();
        Items items = new Items(plugin);
        Block block = event.getClickedBlock();
        for (Level level : Level.levels){
            for (Room room : level.getRooms()){
                for (Locker locker : room.getLockers()){
                    if (block.getLocation().equals(locker.getLocation()) && locker.isLocked()){
                        int sound;
                        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.hasItem() && items.hasLore(event.getItem(), "breaking")){
                            player.sendMessage(ChatColor.GREEN + "Unlocked Contained");
                            world.playSound(block.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                            world.spawnParticle(Particle.LAVA, block.getLocation().clone().add(0.5,1,0.5), 50);
                            ItemStack hand = event.getItem();
                            hand.setAmount(hand.getAmount() - 1);
                            player.getInventory().setItemInMainHand(hand);
                            locker.unlock(true);
                            sound = 1;
                        }else if(event.getAction().equals(Action.LEFT_CLICK_BLOCK) && event.hasItem() &&  plugin.meleWeapons.containsKey(player.getUniqueId())){
                            MeleWeapon weapon = plugin.meleWeapons.get(player.getUniqueId());
                            items.hasLore(event.getItem(), weapon.getId());
                            world.playSound(block.getLocation(), Sound.BLOCK_ANVIL_LAND, 5, 1);
                            world.spawnParticle(Particle.REDSTONE, block.getLocation().clone().add(0.5,1,0.5), 50, new Particle.DustOptions(Color.WHITE, 1));
                            sound = 10;
                            locker.unlock(true);
                        }else{
                            player.sendMessage(ChatColor.RED + "Container is locked!");
                            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 2, 2);
                            event.setCancelled(true);
                            return;
                        }
                        plugin.getServer().getPluginManager().callEvent(new PlayerNoiseEvent(player, sound));
                    }
                }
                }
            }

    }

    private boolean locDifferent(Location loc1, Location loc2){
        return loc1.getX() != loc2.getX() || loc1.getZ() != loc2.getZ();
    }
}

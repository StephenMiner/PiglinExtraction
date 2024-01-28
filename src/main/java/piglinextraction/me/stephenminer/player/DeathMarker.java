package piglinextraction.me.stephenminer.player;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;

public class DeathMarker {
    private final PiglinExtraction plugin;
    private Level level;
    private OfflinePlayer player;
    private ArmorStand armorStand;
    private Location loc;
    public DeathMarker(PiglinExtraction plugin, Level level, OfflinePlayer player, Location loc){
        this.plugin = plugin;
        this.level = level;
        this.player = player;
        this.loc = loc;
        plugin.markers.add(this);
        spawnArmorStand();
        tick();
    }


    public void spawnArmorStand(){
        armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        for (EquipmentSlot slot : EquipmentSlot.values()){
            armorStand.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
            armorStand.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
        }
        armorStand.setCollidable(false);
        armorStand.setCanMove(true);
        armorStand.setCustomName(player.getName());
        armorStand.setInvulnerable(true);
        armorStand.getEquipment().setHelmet(playerHead());
    }

    public void tick(){
        DeathMarker instance = this;
        new BukkitRunnable(){
            @Override
            public void run(){
                if (armorStand.isDead()){
                    plugin.markers.remove(instance);
                    this.cancel();
                    return;
                }
                if(!level.isStarted()){
                    this.cancel();
                    plugin.markers.remove(instance);
                    armorStand.remove();
                    return;
                }
                if (!level.getOffline().contains(player.getUniqueId()) && !level.getDead().contains(player.getUniqueId())){
                    plugin.markers.remove(instance);
                    this.cancel();
                    armorStand.remove();
                    return;
                }
                float yaw = armorStand.getLocation().getYaw() + 1;
                armorStand.setRotation(yaw, armorStand.getLocation().getPitch());
            }
        }.runTaskTimer(plugin, 5, 2);
    }

    public ItemStack playerHead(){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);
        item.setItemMeta(meta);
        return item;
    }

    public ArmorStand getArmorStand(){
        return armorStand;
    }
    public OfflinePlayer getPlayer(){
        return player;
    }
    public Location getLoc(){
        return loc;
    }

}

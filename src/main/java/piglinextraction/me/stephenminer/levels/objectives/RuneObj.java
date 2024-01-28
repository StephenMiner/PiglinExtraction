package piglinextraction.me.stephenminer.levels.objectives;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RuneObj extends Objective{
    private ArmorStand displayer;
    private String name;
    public RuneObj(PiglinExtraction plugin){
        super(plugin, ObjectiveType.RUNE_COLLECTION);
        name = defineRuneType();
    }


    @Override
    public void init(){
        initArmorStand();
        playEffect();
        monitorCollection();
    }


    public void initArmorStand(){
        Location loc = potentialSpawns.get(ThreadLocalRandom.current().nextInt(potentialSpawns.size()));
        World world = loc.getWorld();
        displayer = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
        displayer.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.ADDING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.REMOVING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.REMOVING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.REMOVING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
        displayer.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        displayer.setVisible(false);
        displayer.setInvulnerable(true);
        displayer.getEquipment().setHelmet(runeDisplay(name));
    }

    public void playEffect(){
        new BukkitRunnable(){
            final double ogY = displayer.getLocation().getY();
            double y = 0;
            boolean decrease = false;
            @Override
            public void run(){
                if (complete || kill){
                    this.cancel();
                    displayer.remove();
                    return;
                }
                displayer.setRotation(displayer.getLocation().getYaw() + 1.5f, displayer.getLocation().getPitch());
                //handle operations
                if (y >= 1) decrease = true;
                else if(y <= -1) decrease = false;

                //act out operations
                if (decrease) y -=0.05;
                else y+=0.05;
                Location loc = displayer.getLocation();
                loc.setY(ogY + y);
                displayer.teleport(loc);
            }
        }.runTaskTimer(plugin, 1, 1);
    }
    public void monitorCollection(){
        World world = displayer.getWorld();
        Objective obj = this;
        new BukkitRunnable(){
            final int max = 100;
            int count = 0;
            @Override
            public void run(){
                if (kill || complete){
                    this.cancel();
                    return;
                }
                Collection<Entity> entities = world.getNearbyEntities(displayer.getLocation(), 1.5,1.5,1.5);
                boolean dead = false;
                for (Entity entity : entities){
                    if (entity instanceof Player player){
                        Bukkit.broadcastMessage("" + count);
                        for (Level level : Level.levels){
                            if (level.getDead().contains(player.getUniqueId())) {
                                dead = true;
                                break;
                            }
                        }
                        if (dead) continue;
                        if (count >= max){
                            collected = player;
                            complete = true;
                            notifyDone();
                            this.cancel();
                            return;
                        }
                        if (count % 20 == 0) {
                            for (Level level : Level.levels) {
                                if (level.getObjectives().contains(obj)) {
                                    for (UUID uuid : level.getPlayers()) {
                                        Player p = Bukkit.getPlayer(uuid);
                                        p.sendActionBar(ChatColor.AQUA + "" + ((max -count)/20) + " remaining until collection");
                                    }
                                }
                            }
                        }
                        count++;

                    }
                }
            }
        }.runTaskTimer(plugin, 100, 1);
    }

    public String defineRuneType(){
        String[] runes = new String[3];
        runes[0] = "Legendary Rune";
        runes[1] = "Legendary Rune of Replacement";
        runes[2] = "Legendary Rune of Spite";
        return runes[ThreadLocalRandom.current().nextInt(runes.length)];
    }
    public ItemStack runeDisplay(String type){
        Material mat = switch (type){
            case "Legendary Rune" -> Material.GOLD_BLOCK;
            case "Legendary Rune of Replacement" -> Material.LAPIS_BLOCK;
            case "Legendary Rune of Spite" -> Material.COPPER_BLOCK;
            default -> Material.WHITE_CONCRETE;
        };
        ItemStack item = new ItemStack(mat);
        return item;
    }

    public ItemStack runeItem(String type){
        Material mat = switch (type){
            case "Legendary Rune" -> Material.GOLD_INGOT;
            case "Legendary Rune of Replacement" -> Material.LAPIS_LAZULI;
            case "Legendary Rune of Spite" -> Material.COPPER_INGOT;
            default -> Material.WHITE_DYE;
        };
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + type);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Quite a legendary rune!");
        lore.add(ChatColor.ITALIC + "Don't let it fall into the hands");
        lore.add(ChatColor.ITALIC + "of a chieftain!");
        lore.add(ChatColor.BLACK + this.type.collectionId());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

}

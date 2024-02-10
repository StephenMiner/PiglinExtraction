package piglinextraction.me.stephenminer.mobs;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.weapons.ArmorPiercing;

import java.util.Random;

public class PiglinGrunt extends PiglinEntity{
    private final Random random;

    public PiglinGrunt(PiglinExtraction plugin, Location spawn){
        super(plugin, PiglinType.GRUNT, ArmorPiercing.LIGHT,spawn, 40);
        lightLevel = 10;
        random = new Random();
       // mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20000,2));
        addEquipment();
        target();
        PiglinEntity.cache.add(this);
    }

    public void addEquipment(){
        if (random.nextInt() < 40)
            mob.getEquipment().setHelmet(helmets()[random.nextInt(helmets().length)]);
        if (random.nextInt() < 40)
            mob.getEquipment().setChestplate(chestplates()[random.nextInt(chestplates().length)]);
        if (random.nextInt() < 40)
            mob.getEquipment().setLeggings(pants()[random.nextInt(pants().length)]);
        if (random.nextInt() < 40)
            mob.getEquipment().setBoots(boots()[random.nextInt(boots().length)]);
        mob.getEquipment().setItemInMainHand(mainhand()[random.nextInt(mainhand().length)]);
    }

    @Override
    public void target(){

        new BukkitRunnable(){
            @Override
            public void run(){
                if (mob.isDead()){
                    this.cancel();
                    return;
                }
                checkLight();
/*
                if (mob.getTarget() != null){
                    if (mob.getTarget().isDead() || mob.getTarget().hasPotionEffect(PotionEffectType.INVISIBILITY)){
                        Bukkit.broadcastMessage("I");
                        target = null;
                        mob.setTarget(null);
                    }
                }
                if (hasTarget()) {
                    if (target.isDead() || target.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                        Bukkit.broadcastMessage("F");
                        target = null;
                        mob.setTarget(null);
                        return;
                    }else if (target instanceof Player player){
                        if (isDead(player)){
                            Bukkit.broadcastMessage("D");
                            target = null;
                            return;
                        }
                    }else if (mob.getTarget().hasPotionEffect(PotionEffectType.INVISIBILITY)){
                        Bukkit.broadcastMessage("H");
                        target = null;
                        mob.setTarget(null);
                        return;
                    }
                    mob.setAI(true);
                    mob.setAware(true);
                    mob.lookAt(target);
                    path.moveTo(target, 1);
                    mob.setTarget(target);
                }else{
                    mob.setAI(true);
                    mob.setAware(true);
                    Player nearest = null;
                    for (Entity entity : mob.getNearbyEntities(50,50,50)){
                        if (entity instanceof Player player && isPlayerTargetWhitelist(player) && !isDead(player)) {
                            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) continue;
                            if (nearest == null)
                                nearest = player;
                            else if (getDistance(nearest) < getDistance(player))
                                nearest = player;
                        }
                    }
                    if (nearest != null) {
                        Bukkit.broadcastMessage(nearest.getName());
                        target = nearest;
                    }
                    mob.setTarget(target);
                }




 */

            }



        }.runTaskTimer(plugin, 5, 1);
    }

    public double getDistance(Entity entity){
        Location mloc = mob.getLocation();
        Location eloc = entity.getLocation();
        return Math.hypot(mloc.getX() - eloc.getX(), mloc.getZ() - eloc.getZ());
    }

    private ItemStack[] helmets(){
        ItemStack[] items = new ItemStack[6];
        items[0] = enchantedItem(new ItemStack(Material.LEATHER_HELMET), Enchantment.PROTECTION_PROJECTILE, random.nextInt(3) + 1);
        items[1] = enchantedItem(new ItemStack(Material.LEATHER_HELMET), Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3) + 1);
        items[2] = new ItemStack(Material.LEATHER_HELMET);
        items[3] = enchantedItem(new ItemStack(Material.GOLDEN_HELMET), Enchantment.PROTECTION_PROJECTILE, random.nextInt(3) + 1);
        items[4] = enchantedItem(new ItemStack(Material.GOLDEN_HELMET), Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3) + 1);
        items[5] = new ItemStack(Material.GOLDEN_HELMET);
        return items;
    }
    private ItemStack[] chestplates(){
        ItemStack[] items = new ItemStack[6];
        items[0] = enchantedItem(new ItemStack(Material.LEATHER_CHESTPLATE), Enchantment.PROTECTION_PROJECTILE, random.nextInt(3) + 1);
        items[1] = enchantedItem(new ItemStack(Material.LEATHER_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3) + 1);
        items[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
        items[3] = enchantedItem(new ItemStack(Material.GOLDEN_CHESTPLATE), Enchantment.PROTECTION_PROJECTILE, random.nextInt(3) + 1);
        items[4] = enchantedItem(new ItemStack(Material.GOLDEN_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3) + 1);
        items[5] = new ItemStack(Material.GOLDEN_CHESTPLATE);
        return items;
    }
    private ItemStack[] pants(){
        ItemStack[] items = new ItemStack[6];
        items[0] = enchantedItem(new ItemStack(Material.LEATHER_LEGGINGS), Enchantment.PROTECTION_PROJECTILE, random.nextInt(3) + 1);
        items[1] = enchantedItem(new ItemStack(Material.LEATHER_LEGGINGS), Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3) + 1);
        items[2] = new ItemStack(Material.LEATHER_LEGGINGS);
        items[3] = enchantedItem(new ItemStack(Material.GOLDEN_LEGGINGS), Enchantment.PROTECTION_PROJECTILE, random.nextInt(3) + 1);
        items[4] = enchantedItem(new ItemStack(Material.GOLDEN_LEGGINGS), Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3) + 1);
        items[5] = new ItemStack(Material.GOLDEN_LEGGINGS);
        return items;
    }
    private ItemStack[] boots(){
        ItemStack[] items = new ItemStack[6];
        items[0] = enchantedItem(new ItemStack(Material.LEATHER_BOOTS), Enchantment.PROTECTION_PROJECTILE, random.nextInt(3) + 1);
        items[1] = enchantedItem(new ItemStack(Material.LEATHER_BOOTS), Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3) + 1);
        items[2] = new ItemStack(Material.LEATHER_BOOTS);
        items[3] = enchantedItem(new ItemStack(Material.GOLDEN_BOOTS), Enchantment.PROTECTION_PROJECTILE, random.nextInt(3) + 1);
        items[4] = enchantedItem(new ItemStack(Material.GOLDEN_BOOTS), Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3) + 1);
        items[5] = new ItemStack(Material.GOLDEN_BOOTS);
        return items;
    }
    private ItemStack[] mainhand(){
        ItemStack[] items = new ItemStack[7];
        items[0] = new ItemStack(Material.STONE_SWORD);
        items[1] = enchantedItem(new ItemStack(Material.STONE_SWORD), Enchantment.DAMAGE_ALL, random.nextInt(2) + 1);
        items[2] = new ItemStack(Material.GOLDEN_SWORD);
        items[3] = new ItemStack(Material.WOODEN_SWORD);
        items[4] = new ItemStack(Material.STONE_SWORD);
        items[5] = new ItemStack(Material.CROSSBOW);
        items[6] = enchantedItem(new ItemStack(Material.CROSSBOW), Enchantment.QUICK_CHARGE, 4);
        return items;
    }

    private ItemStack enchantedItem(ItemStack item, Enchantment ench, int level){
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(ench, level, true);
        item.setItemMeta(meta);
        return item;
    }




}

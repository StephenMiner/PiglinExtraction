package piglinextraction.me.stephenminer.mobs;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Necromancer extends PiglinEntity {
    private final List<Zombie> zombies;
    public Necromancer(PiglinExtraction plugin, Location spawn){
        super(plugin, PiglinType.NECROMANCER, spawn, 60);
        lightLevel = 6;
        zombies = new ArrayList<>();
        equip();
        target();
        PiglinEntity.cache.add(this);
    }




    @Override
    public void target(){
        new BukkitRunnable(){
            int countTo = 80;
            int count = 0;
            double height = mob.getLocation().getY();
            Location container = mob.getLocation().clone();
            @Override
            public void run(){
                if (mob.isDead()){
                    for (Zombie zombie : zombies){
                        zombie.setHealth(0);
                    }
                    this.cancel();
                    return;
                }
                Block block = mob.getLocation().getBlock();
                int light = block.getLightFromBlocks() + block.getLightFromSky() + block.getLightLevel();
                if (!activated && light >= 6) {
                    irritation++;
                    mob.getWorld().spawnParticle(Particle.REDSTONE, mob.getEyeLocation().clone().add(0,1,0), 0, new Particle.DustOptions(Color.RED, 1));
                    if (irritation >= lightActivation) {
                        activated = true;
                        irritation = 0;
                    }
                }else irritation--;
                if (activated){
                    mob.setAI(true);
                    mob.setAware(true);
                }else{
                    mob.setAI(false);
                    mob.setAware(false);
                    return;
                }
                if (activated){
                    mob.setAI(true);
                    mob.setAware(true);
                    if (count >= countTo){
                        count = 0;
                        countTo = ThreadLocalRandom.current().nextInt(161) + 100;
                        World world = mob.getWorld();
                        for (int i = 0; i < 2; i++) {
                            Zombie zombie = (Zombie) world.spawnEntity(mob.getLocation(), EntityType.ZOMBIE);
                            if (ThreadLocalRandom.current().nextBoolean()) zombie.setAdult();
                            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 2));
                            zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100000, 0));
                            zombies.add(zombie);
                        }
                        int radius = 2;
                        for (double y = 0; y < 5; y+=0.5){
                            double x = radius * Math.cos(y);
                            double z = radius * Math.sin(y);
                            container.setX(x);
                            container.setY(y + height);
                            container.setZ(z);
                            world.spawnParticle(Particle.REDSTONE, container, 1, new Particle.DustOptions(Color.PURPLE, 1));
                        }
                    }
                    count++;
                }else{
                    mob.setAI(false);
                    mob.setAware(false);
                }
            }
        }.runTaskTimer(plugin, 5, 1);
    }

    public void kill(){
        mob.setHealth(0);
        for (Zombie zombie : zombies){
            zombie.setHealth(0);
        }
    }


    private void equip(){
        EntityEquipment equipment = mob.getEquipment();
        equipment.setChestplate(chest());
        equipment.setLeggings(legs());
        equipment.setBoots(boots());
    }


    private ItemStack chest(){
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.BLACK);
        meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 9, true);
        AttributeModifier armor = new AttributeModifier("generic.armor", 9, AttributeModifier.Operation.ADD_NUMBER);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
        AttributeModifier toughness = new AttributeModifier("generic.armorToughness", 4, AttributeModifier.Operation.ADD_NUMBER);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack legs(){
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.BLACK);
        meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 9, true);
        AttributeModifier armor = new AttributeModifier("generic.armor", 9, AttributeModifier.Operation.ADD_NUMBER);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
        AttributeModifier toughness = new AttributeModifier("generic.armorToughness", 4, AttributeModifier.Operation.ADD_NUMBER);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack boots(){
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.BLACK);
        meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 9, true);
        AttributeModifier armor = new AttributeModifier("generic.armor", 9, AttributeModifier.Operation.ADD_NUMBER);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
        AttributeModifier toughness = new AttributeModifier("generic.armorToughness", 4, AttributeModifier.Operation.ADD_NUMBER);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
        item.setItemMeta(meta);
        return item;
    }



}

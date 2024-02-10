package piglinextraction.me.stephenminer.mobs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.weapons.ArmorPiercing;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PiglinKnight extends PiglinEntity{

    public PiglinKnight(PiglinExtraction plugin,  Location spawn) {
        super(plugin,PiglinType.KNIGHT, ArmorPiercing.MEDIUM,spawn, 10);
        mob.setCustomNameVisible(false);
        mob.setMetadata("knight",new FixedMetadataValue(plugin,"knight"));
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        mob.setHealth(40);
        EntityEquipment equipment = mob.getEquipment();
        equipment.setHelmet(helm());
        equipment.setChestplate(chest());
        equipment.setLeggings(legs());
        equipment.setBoots(boots());
        Material mat = ThreadLocalRandom.current().nextBoolean() ? Material.GOLDEN_SWORD : Material.GOLDEN_AXE;
        equipment.setItemInMainHand(new ItemStack(mat));
        equipment.setItemInOffHand(new ItemStack(Material.SHIELD));
        target();
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
            }
        }.runTaskTimer(plugin,1,1);
    }

    private ItemStack helm(){
        ItemStack item = new ItemStack(Material.GOLDEN_HELMET);
        ArmorMeta meta = (ArmorMeta) item.getItemMeta();
        meta.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.SNOUT));
        AttributeModifier armor = new AttributeModifier(UUID.randomUUID(), "generic.armor", 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
        AttributeModifier armorToughness = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 1.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);

        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, armorToughness);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack chest(){
        ItemStack item = new ItemStack(Material.GOLDEN_CHESTPLATE);
        ArmorMeta meta = (ArmorMeta) item.getItemMeta();
        meta.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.SNOUT));
        AttributeModifier armor = new AttributeModifier(UUID.randomUUID(), "generic.armor", 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
        AttributeModifier armorToughness = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 1.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);

        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, armorToughness);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack legs(){
        ItemStack item = new ItemStack(Material.GOLDEN_LEGGINGS);
        ArmorMeta meta = (ArmorMeta) item.getItemMeta();
        meta.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.SNOUT));
        AttributeModifier armor = new AttributeModifier(UUID.randomUUID(), "generic.armor", 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
        AttributeModifier armorToughness = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 1.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);

        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, armorToughness);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack boots(){
        ItemStack item = new ItemStack(Material.GOLDEN_BOOTS);
        ArmorMeta meta = (ArmorMeta) item.getItemMeta();
        meta.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.SNOUT));
        AttributeModifier armor = new AttributeModifier(UUID.randomUUID(), "generic.armor", 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
        AttributeModifier armorToughness = new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 1.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);

        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, armor);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, armorToughness);
        item.setItemMeta(meta);
        return item;
    }



}

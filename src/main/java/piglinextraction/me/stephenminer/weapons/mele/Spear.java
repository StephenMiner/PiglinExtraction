package piglinextraction.me.stephenminer.weapons.mele;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Spear extends MeleWeapon{

    public Spear(PiglinExtraction plugin, Player owner){
        super(plugin, owner);
        this.range = 6;
        this.damage = 7;
        this.headsotMultiplier = 2.79f;
        hitStagger = 50;
        attackSpeed = -3.3;
        chargeStagger = false;
        this.id = "spear";
        this.item = spear();
        giveItem();
        trackAttackCooldown();
    }


    public ItemStack spear(){
        ItemStack item = new ItemStack(Material.TRIDENT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Spear");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A tried and true weapon");
        lore.add(ChatColor.ITALIC + "High range, good damage");
        lore.add("");
        lore.add(ChatColor.GRAY + "When in Main Hand:");
        lore.add(ChatColor.DARK_GREEN + "" + damage + " Attack Damage");
        lore.add(ChatColor.DARK_GREEN + "" + getRealSpeed() + " Attack Speed");
        lore.add(ChatColor.DARK_GREEN + "" + range + " Attack Range");
        lore.add(ChatColor.BLACK + id);
        meta.setLore(lore);
        AttributeModifier AttackDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", dummyDmg, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier AttackSpeed = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -3.3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, AttackDamage);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, AttackSpeed);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void attack(LivingEntity entity, boolean headshot, boolean blocked) {
        if (charged && entity instanceof Mob mob){
            staggerEntity(mob);
        }
        super.attack(entity, headshot, blocked);
    }

}

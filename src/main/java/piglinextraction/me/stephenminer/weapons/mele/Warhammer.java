package piglinextraction.me.stephenminer.weapons.mele;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
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

public class Warhammer extends MeleWeapon{


    public Warhammer(PiglinExtraction plugin, Player owner){
        super(plugin, owner);
        range = 4;
        damage = 8;
        headsotMultiplier = 3.0f;
        attackSpeed = -3.4;
        hitStagger = 25;
        id = "warhammer";
        item = warhammer();
        giveItem();
        trackAttackCooldown();
    }



    public ItemStack warhammer(){
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "Dwarven Warhammer");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A classic sweeping weapon");
        lore.add(ChatColor.ITALIC + "Good damage, high charge time");
        lore.add("");
        lore.add(ChatColor.GRAY + "When in Main Hand:");
        lore.add(ChatColor.DARK_GREEN + "" + damage + " attack damage");
        lore.add(ChatColor.DARK_GREEN + "" + getRealSpeed() + " attack speed");
        lore.add(ChatColor.DARK_GREEN + "" + range + " Attack Range");
        lore.add(ChatColor.BLACK + "warhammer");
        meta.setLore(lore);
        AttributeModifier AttackDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", dummyDmg, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier AttackSpeed = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", attackSpeed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, AttackDamage);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, AttackSpeed);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void attack(LivingEntity entity, boolean headshot, boolean blocked){
        super.attack(entity, headshot, false);
        List<Entity> entities = entity.getNearbyEntities(3, 3, 3);
        for (Entity e : entities){
            if (e instanceof Mob mob) {
                super.attack(mob, false, false);
            }
        }
    }
}

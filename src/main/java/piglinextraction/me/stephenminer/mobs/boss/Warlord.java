package piglinextraction.me.stephenminer.mobs.boss;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;
import piglinextraction.me.stephenminer.mobs.PiglinType;
import piglinextraction.me.stephenminer.weapons.ArmorPiercing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Warlord extends PiglinEntity {
    private List<Reinforcement> reinforcements;
    private Level level;

    /**
     * How many times the warlord will be able to call reinforcements
     */
    private int reinforceUses;
    /**
     * tick interval between reinforcement spawns (first one happens automatically 5 seconds [20 ticks] after activation)
     */
    private int reinforceInt;
    public Warlord(PiglinExtraction plugin, Location spawn) {
        this(plugin, spawn, new ArrayList<>());
        reinforcements = new ArrayList<>();

    }

    public Warlord(PiglinExtraction plugin, Location spawn, List<Reinforcement> reinforcements) {
        super(plugin, PiglinType.WARLORD, ArmorPiercing.HIGH,spawn, 10);
        this.reinforcements = reinforcements;
        reinforceInt = 1*60*20;
        reinforceUses = 4;
        count = reinforceInt;
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(450);
        mob.setHealth(450);
        mob.setMetadata("medium-armor", new FixedMetadataValue(plugin,"medium-armor"));
        ((PiglinBrute) mob).setImmuneToZombification(true);
        equip();
        target();
    }

    /**
     * uses tracker
     */
    private int uses;

    private int count;

    @Override
    public void target() {
        new BukkitRunnable(){
            @Override
            public void run(){
                if (mob.isDead()){
                    this.cancel();
                    return;
                }
                checkLight();
                if (!activated) return;
                if (count >= reinforceInt && uses < reinforceUses){
                    count = 0;
                    uses++;
                    reinforce();
                }else count++;
            }
        }.runTaskTimer(plugin,1,1);

    }


    public void reinforce(){
        Reinforcement random = reinforcements.get(ThreadLocalRandom.current().nextInt(reinforcements.size()));
        random.reinforce(level);
    }

    public void equip(){
        EntityEquipment equipment = mob.getEquipment();
        equipment.setHelmet(helmet());
        equipment.setChestplate(chestplate());
        equipment.setLeggings(legs());
        equipment.setBoots(boots());
        equipment.setItemInMainHand(sword());
        for (EquipmentSlot slot : EquipmentSlot.values()){
            equipment.setDropChance(slot,0);
        }

    }

    private ItemStack helmet(){
        ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,true);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack chestplate(){
        ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,true);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack legs(){
        ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,true);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack boots(){
        ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,true);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack sword(){
        ItemStack item = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DAMAGE_ALL,1,true);
        item.setItemMeta(meta);
        return item;
    }


    /**
     *
     * @return Game-Level warlord is in. May be null
     */
    public Level getLevel(){ return level; }

    public void setLevel(Level level){ this.level = level; }

    public void addReinforcements(Reinforcement reinforcement){ this.reinforcements.add(reinforcement); }
    public void removeReinforcements(Reinforcement reinforcement){ this.reinforcements.remove(reinforcement); }

    public void setReinforcements(List<Reinforcement> reinforcements){ this.reinforcements = reinforcements; }




}

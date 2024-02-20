package piglinextraction.me.stephenminer.mobs;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.weapons.ArmorPiercing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PiglinEntity{
    public static List<PiglinEntity> cache = new ArrayList<>();



    protected final PiglinType type;
    protected final PiglinExtraction plugin;
    protected final Mob mob;
    /**
     * How many ticks it takes to activate a piglin via light
     */
    protected final int lightActivation;
    /**
     * Light level where piglin will get irritated
     */
    protected int lightLevel;
    /**
     * how irritated a piglin is
     */
    protected int irritation;
    /**
     * Manages whether mob has ai or not
     */
    protected boolean activated;

    protected LivingEntity target;
    protected List<UUID> playerTargetWhitelist;
    protected ArmorPiercing armor;


    public PiglinEntity(PiglinExtraction plugin, PiglinType type, ArmorPiercing armor, Location spawn, int lightActivation){
        this.armor = armor;
        mob = (Mob) spawn.getWorld().spawnEntity(spawn, type.getType());
        mob.setMetadata(armor.tag(),armor.data());
        mob.setMetadata("mobId",new FixedMetadataValue(plugin,type.getId()));

        this.lightActivation = lightActivation;
        playerTargetWhitelist = new ArrayList<>();
        if (mob instanceof Piglin piglin){
            piglin.setImmuneToZombification(true);
            piglin.setAdult();
        }
        if (mob instanceof PiglinBrute brute){
            brute.setAdult();
            brute.setImmuneToZombification(true);
        }
        this.type = type;
        this.plugin = plugin;
    }

    public void target(){
    }

    protected void checkLight(){
        Block block = mob.getLocation().getBlock();
        int light = block.getLightFromBlocks() + block.getLightFromSky() + block.getLightLevel();
        if (!activated && light >= lightLevel) {
            irritation++;
            mob.getWorld().spawnParticle(Particle.REDSTONE, mob.getEyeLocation().clone().add(0,1,0), 0, new Particle.DustOptions(Color.RED, 1));
            if (irritation >= lightActivation) {
                activated = true;
                irritation = 0;
            }
        }else if (irritation > 0) irritation--;
        if (activated){
            mob.setAI(true);
        }else{
            mob.setAI(false);
            return;
        }
    }

    public boolean isDead(Player player){
        for (Level level : Level.levels){
            if (level.getDead().contains(player.getUniqueId())) return true;
        }
        return false;
    }

    public boolean isPlayerTargetWhitelist(Player player){
        if (playerTargetWhitelist.size() > 0) return playerTargetWhitelist.contains(player.getUniqueId());
        else return true;
    }

    public LivingEntity getTarget(){
        return target;
    }
    public boolean hasTarget(){
        return target != null;
    }
    public void setTarget(LivingEntity target){
        this.target = target;
    }
    public boolean isActivated(){
        return activated;
    }
    public void activate(boolean activated){
        this.activated = activated;
    }
    public Mob getMob(){return mob; }

    public boolean entityIsSimilar(Entity entity){
        return mob.equals(entity);
    }
    public List<UUID> getPlayerWhitelist(){ return playerTargetWhitelist; }
    public void addPlayerWhitelist(Player player){ playerTargetWhitelist.add(player.getUniqueId()); }
    public void removePlayerWhitelist(Player player){ playerTargetWhitelist.remove(player.getUniqueId()); }

}

package piglinextraction.me.stephenminer.levels.spawners;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.mobs.PiglinType;

public class Repeater extends Node{
    private final int interval;
    public Repeater(PiglinExtraction plugin, Location loc, int radius, int interval, Room room, PiglinType toSpawn) {
        super(plugin, loc, radius, room, toSpawn);
        this.interval = interval;
    }

    @Override
    public void spawn() {
        new BukkitRunnable() {

            @Override
            public void run() {

            }
        }.runTaskTimer(plugin, 0,1);
        new BukkitRunnable(){
            int count = 0;
            Location loc = base;
            Location first, second;
            double var = 0;
            World world = loc.getWorld();
            @Override
            public void run(){
                if (room.kill()){
                    this.cancel();
                    return;
                }
                if (count >= interval){
                    for (Entity entity : base.getNearbyEntities(radius, radius, radius)){
                        if (entity instanceof Player player && player.getGameMode().equals(GameMode.SURVIVAL)) {
                            spawnEntity();
                            count = 0;
                        }
                        return;
                    }

                }
                loc = base;
                var += Math.PI / 16;
                first = loc.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                second = loc.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1, Math.sin(var + Math.PI));
                world.spawnParticle(Particle.REDSTONE, first, 0, new Particle.DustOptions(Color.YELLOW, 1));
                count++;
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    @Override
    public void save(){
        super.save();
        plugin.roomsFile.getConfig().set("rooms." + room.getId() + ".nodes." + ".interval", interval);
    }
}

package piglinextraction.me.stephenminer.levels;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.boss.encounters.Encounter;
import piglinextraction.me.stephenminer.mobs.boss.encounters.RandomHorde;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTimer {
    private final Level level;
    private final PiglinExtraction plugin;

    private int hordeTimer, specialTimer, hordeChance, specialChance;
    private int hordeCount, specialCount;

    private List<RandomHorde> pool;

    public RandomTimer(Level level){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        this.level = level;
        hordeTimer = level.loadHordeTimer();
        specialTimer = level.loadSpecialTimer();
        hordeChance = level.loadHordeChance();
        specialChance = level.loadSpecialChance();

        initPool();
    }

    private void initPool(){
        pool = new ArrayList<>();
        for (Encounter encounter : level.getEncounters())
            if (encounter instanceof RandomHorde rand) pool.add(rand);
    }



    public void runTimer(){

        new BukkitRunnable(){
            @Override
            public void run(){
                if (hordeCount >= hordeTimer){
                    Random random = new Random();
                    int roll = random.nextInt(100);
                    if (roll < hordeCount) pool.get(ThreadLocalRandom.current().nextInt(pool.size())).trigger();
                }
                hordeCount++;
                specialCount++;
            }
        }.runTaskTimer(plugin,1,1);
    }
}

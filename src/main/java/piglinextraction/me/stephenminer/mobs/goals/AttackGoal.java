package piglinextraction.me.stephenminer.mobs.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.destroystokyo.paper.entity.ai.MobGoals;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import piglinextraction.me.stephenminer.PiglinExtraction;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class AttackGoal implements Goal<Piglin> {
    private final GoalKey<Piglin> key;
    private final Mob mob;
    private Player closestPlayer;
    private int cooldown;
    public AttackGoal(Plugin plugin, Mob mob){
        this.key = GoalKey.of(Piglin.class, new NamespacedKey(plugin, "custom_attack"));
        this.mob = mob;
    }
    @Override
    public boolean shouldActivate() {
        return false;
    }

    @Override
    public @NotNull GoalKey<Piglin> getKey() {
        return null;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return null;
    }

    @Override
    public void stop() {
        Goal.super.stop();
    }

    @Override
    public void tick(){

    }


}

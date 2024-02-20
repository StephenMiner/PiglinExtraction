package piglinextraction.me.stephenminer.levels.objectives;

import org.bukkit.entity.LivingEntity;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.PiglinType;

public class SlayingObj extends Objective{
    private PiglinType piglin;
    private int needed;
    private int current;
    public SlayingObj(PiglinExtraction plugin, String id, PiglinType piglin, int needed) {
        super(plugin, id, ObjectiveType.SLAYING);
        this.piglin = piglin;
        this.needed = needed;
    }

    public SlayingObj(PiglinExtraction plugin, String id, String levelId){
        super(plugin,id,ObjectiveType.SLAYING);
        loadData(levelId);
    }

    private void loadData(String levelId){
        String path = "levels." + levelId + ".objs." + id;
        piglin = PiglinType.valueOf(plugin.levelsFile.getConfig().getString(path  + ".piglin"));
        needed = plugin.levelsFile.getConfig().getInt(path + ".needed");
    }

    /**
     *
     * @param living dead monster, should already have been confirmed to be within a certain Level boundary
     */
    public void checkKill(LivingEntity living){
        if (complete) return;
        if (living.hasMetadata("mobId")){

            int data = living.getMetadata("mobId").get(0).asInt();
            if (piglin.getId() == -1) current++;
            else if (piglin.getId() == data) current++;
            checkCompletion();
        }
    }

    private void checkCompletion(){
        if (current >= needed){
            complete = true;
            notifyDone();
        }
    }


    @Override
    public boolean save(String levelId) {
        super.save(levelId);
        String path = "levels." + levelId;
        if (!plugin.levelsFile.getConfig().contains(path)) return false;
        path += ".objs." + id;
        plugin.levelsFile.getConfig().set(path + ".piglin", piglin);
        plugin.levelsFile.getConfig().set(path + ".needed", needed);
        plugin.levelsFile.saveConfig();
        return true;
    }


    public PiglinType getPiglin(){ return piglin; }

    /**
     *
     * @return (original) amount of kills needed to complete objective
     */
    public int getNeeded(){ return needed; }

    /**
     *
     * @return How many kills have been achieved so far
     */
    public int getCurrent(){ return current; }

    @Override
    public String getDisplay(){ return piglin.getName(); }

    @Override
    public String getStatus(){ return ": " + current + "/" + needed; }
}

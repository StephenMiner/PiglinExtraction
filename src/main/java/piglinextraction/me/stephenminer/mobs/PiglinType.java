package piglinextraction.me.stephenminer.mobs;

import org.bukkit.entity.EntityType;

public enum PiglinType {
    /**
     * Generic Type is meant to be used as a case for if you want all enemies to be the objective for a slaying quest
     */
    GENERIC(EntityType.PIGLIN,"Generic", -1),
    GRUNT(EntityType.PIGLIN, "Grunt", 0),
    GUARD(EntityType.PIGLIN, "Guard", 1),
    NECROMANCER(EntityType.PIGLIN, "Necromancer", 2),
    KNIGHT(EntityType.PIGLIN,"Knight",3),
    BLAZE(EntityType.BLAZE,"Blaze",4),
    WARLORD(EntityType.PIGLIN_BRUTE, "Warlord", 5);

    private final EntityType type;
    private final String name;
    private final int id;

    /**
     *
     * @param type
     * @param name CANNOT EXCEED 16 CHARACTERS - Used for scoreboard display
     * @param id tbh I forgot what I use this for
     */
    PiglinType(EntityType type, String name, int id){
        this.type = type;
        this.name = name;
        this.id = id;
    }

    public String getName(){ return name; }
    public int getId(){ return id; }
    public EntityType getType(){ return type; }
}

package piglinextraction.me.stephenminer.mobs;

import org.bukkit.entity.EntityType;

public enum PiglinType {
    GRUNT(EntityType.PIGLIN, "Grunt", 0),
    GUARD(EntityType.PIGLIN, "Guard", 1),
    NECROMANCER(EntityType.PIGLIN, "Necromancer", 2),
    KNIGHT(EntityType.PIGLIN,"Knight",3),
    BLAZE(EntityType.BLAZE,"Blaze",4),
    WARLORD(EntityType.PIGLIN_BRUTE, "Warlord", 5);

    private final EntityType type;
    private final String name;
    private final int id;
    PiglinType(EntityType type, String name, int id){
        this.type = type;
        this.name = name;
        this.id = id;
    }

    public String getName(){ return name; }
    public int getId(){ return id; }
    public EntityType getType(){ return type; }
}

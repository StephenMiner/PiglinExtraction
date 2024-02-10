package piglinextraction.me.stephenminer.weapons;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import piglinextraction.me.stephenminer.PiglinExtraction;

/**
 * Damage class of weapons attacks, requires headshot to deal damage if no pierce.
 */
public enum ArmorPiercing {
    LIGHT("light-armor"),
    MEDIUM("medium-armor"),
    HIGH("high-armor");

    private final String tag;
    private final FixedMetadataValue data;

    private ArmorPiercing(String tag){
        this.tag = tag;

        data = new FixedMetadataValue(JavaPlugin.getPlugin(PiglinExtraction.class),tag);
    }

    public String tag(){ return tag; }
    public FixedMetadataValue data(){ return data; }
}

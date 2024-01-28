package piglinextraction.me.stephenminer.mobs.hordes;

public enum TriggerType {
    COLLECT_MINOR(0),
    COLLECT_AGITATED(1),
    COLLECT_SEVERE(2),
    DOOR(-1);

    TriggerType(int agitationLevel){
        this.agitationLevel = agitationLevel;
    }
    private final int agitationLevel;
    public int agitationlevel(){ return agitationLevel; }
}

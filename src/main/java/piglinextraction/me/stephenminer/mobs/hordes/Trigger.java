package piglinextraction.me.stephenminer.mobs.hordes;


public  class Trigger {
    private final Horde host;
    private final String triggerId;
    private final TriggerType type;
    /**
     * @param host      host is the Horde object that the Trigger belongs to
     * @param triggerId triggerId is the id of the artifact, door, etc that will trip the Horde object
     * @param type help to know conditions under which the horde should be triggered
     */
    public Trigger(Horde host, String triggerId, TriggerType type) {
        this.host = host;
        this.triggerId = triggerId;
        this.type = type;
    }

    public void trigger(){
        host.triggerHorde();
    }

    public Horde host(){ return host; }
    public String triggerId(){ return triggerId; }
    public TriggerType type(){ return type; }

    @Override
    public String toString(){
        return triggerId + "/" + type.toString();
    }

}

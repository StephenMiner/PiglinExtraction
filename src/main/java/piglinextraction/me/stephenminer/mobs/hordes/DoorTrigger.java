package piglinextraction.me.stephenminer.mobs.hordes;

public class DoorTrigger extends Trigger{
    private final String roomId, doorId;
    /**
     * @param host      host is the Horde object that the Trigger belongs to
     * @param triggerId triggerId is the id of the artifact, door, etc that will trip the Horde object
     * @param type      help to know conditions under which the horde should be triggered
     * @param roomId the room the door is in
     * @param doorId the id for the door
     */
    public DoorTrigger(Horde host, String triggerId, TriggerType type, String roomId, String doorId) {
        super(host, triggerId, type);
        this.roomId = roomId;
        this.doorId = doorId;
    }




    public String roomId(){ return roomId; }
    public String doorId(){ return doorId; }
    @Override
    public String toString(){
        return super.toString() + "/" + roomId + "/" + doorId;
    }
}

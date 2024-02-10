package piglinextraction.me.stephenminer.mobs.boss.encounters;

import piglinextraction.me.stephenminer.levels.rooms.Room;

public class RoomEncounter extends Encounter {
    protected Room room;
    protected RoomEncounter.Flag flag;
    protected boolean activated;

    public RoomEncounter(String id) {
        super(id);
    }


    @Override
    public boolean trigger(){
        if (activated) return false;
        activated = true;
        return true;
    }




    public Room getRoom(){ return room; }
    public Flag getFlag(){ return flag; }
    public boolean hasActivated(){ return activated; }

    @Override
    public String toString(){
        return super.toString() + '=' + flag.toString() + '=';
    }


    public enum Flag{
        ON_ENTER(),
        ON_EXIT();
    }
}

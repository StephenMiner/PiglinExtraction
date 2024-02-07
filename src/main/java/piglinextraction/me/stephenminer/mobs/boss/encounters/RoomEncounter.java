package piglinextraction.me.stephenminer.mobs.boss.encounters;

import piglinextraction.me.stephenminer.levels.rooms.Room;

public class RoomEncounter extends Encounter {
    protected Room room;
    protected RoomEncounter.Flag flag;

    public RoomEncounter(String id) {
        super(id);
    }





    public Room getRoom(){ return room; }
    public Flag getFlag(){ return flag; }

    @Override
    public String toString(){
        return super.toString() + '=' + flag.toString() + '=';
    }

    public enum Flag{
        ON_ENTER(),
        ON_EXIT();
    }
}

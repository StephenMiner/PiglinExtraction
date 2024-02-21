PIGLIN EXTRACTION

/nThe UNWRITTEN RULES:
To create encounters in levels, do the following
(example for warlord)
levels:
  level_id:
    encounters:
      - warlord=room_id=ON_ENTER=world,215,60,231=world,24,34,43/world,23,42,342%Necromancer,PiglinKnight,PiglinGrunt

Current Piglin Types:
Grunt
Necromancer
Warlord
BlazeGunner
Knight


Encounter Types:
WARLORD:
flag = ON_ENTER or ON_EXIT
loc = reinforcement locations for warlord
type = type of piglin to spawn
warlord=room_id=flag=spawn_loc=loc1/loc2/loc3/...%type1,type2,type3,...
HORDE_ROOM:
flag = ON_ENTER or ON_EXIT
open-doors = whether doors near horde spawns will be opened automatically or not
cooldown = cooldown before this event can be triggered again 
horde = horde_id
horderoom=room=flag=open-doors=cooldown=horde1%horde2%horde3

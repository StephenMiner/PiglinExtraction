package piglinextraction.me.stephenminer.levels.objectives;

public enum ObjectiveType {
    RUNE_COLLECTION("rune"),
    SLAY_ILLUSIONER("illusioner"),
    SLAY_WARLORD("warlord");


    private final String collectionId;

    private ObjectiveType(String collectionId){
        this.collectionId = collectionId;
    }



    public final String collectionId(){ return collectionId; }




}

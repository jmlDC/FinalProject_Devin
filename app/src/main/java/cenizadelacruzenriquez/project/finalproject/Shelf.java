package cenizadelacruzenriquez.project.finalproject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// 1 What are we trying to save?
public class Shelf extends RealmObject {

    // 3 Primary Key
    @PrimaryKey
    String uuid;

    // 2 What does it contain?
    private String shelfName;
    private String shelfDescription;

    private String ownerUUID; // tied to the uuid of User object

    // 6 Custom Constructor

    public Shelf(String uuid, String shelfName, String shelfDescription, String ownerUUID) {
        this.uuid = uuid;
        this.shelfName = shelfName;
        this.shelfDescription = shelfDescription;
        this.ownerUUID = ownerUUID;
    }


    // 6.5 Default Constructor

    public Shelf(){
        // Default
    }

    // 4 Getters and Setters

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
    }

    public String getShelfDescription() {
        return shelfDescription;
    }

    public void setShelfDescription(String shelfDescription) {
        this.shelfDescription = shelfDescription;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    // 5 ToString

    @Override
    public String toString() {
        return "Shelf{" +
                "uuid='" + uuid + '\'' +
                ", shelfName='" + shelfName + '\'' +
                ", shelfDescription='" + shelfDescription + '\'' +
                ", ownerID='" + ownerUUID + '\'' +
                '}';
    }
}

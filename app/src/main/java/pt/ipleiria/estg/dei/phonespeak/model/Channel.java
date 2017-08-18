package pt.ipleiria.estg.dei.phonespeak.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by magalhaes on 28/10/2016.
 */

public class Channel implements Serializable{

    private int id;
    private String name;
    private String description;
    private float latitude;
    private float longitude;
    private Calendar createdAt;
    private String elevation;
    private int lastEntryId;
    private int ranking;
    private Tag[] tags;
    private LinkedList<Field> fields;
    private boolean privateChannel;
    private String writeAPI;
    private String readAPI;

    public Channel(int id, String name, String description, float latitude, float longitude, Calendar createdAt,
                   String elevation, int lastEntryId, int ranking, Tag[] tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.elevation = elevation;
        this.lastEntryId = lastEntryId;
        this.ranking = ranking;
        this.tags = tags;
        this.privateChannel = false;
        this.fields = new LinkedList<>();

    }

    public String getWriteAPI() {
        return writeAPI;
    }

    public void setWriteAPI(String writeAPI) {
        this.writeAPI = writeAPI;
    }

    public String getReadAPI() {
        return readAPI;
    }

    public void setReadAPI(String readAPI) {
        this.readAPI = readAPI;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public int getLastEntryId() {
        return lastEntryId;
    }

    public void setLastEntryId(int lastEntryId) {
        this.lastEntryId = lastEntryId;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public Tag[] getTags() {
        return tags;
    }

    public void setTags(Tag[] tags) {
        this.tags = tags;
    }

    public boolean isPrivateChannel() {
        return privateChannel;
    }

    public void setPrivateChannel() {
        this.privateChannel = true;
    }

    public LinkedList<Field> getAllFields() {
        return fields;
    }

    public Field getField(int index) {
        return fields.get(index);
    }

    public void setFields(LinkedList<Field> fields) {
        this.fields = fields;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Channel) {
            Channel c = (Channel) obj;
            return this.id == c.getId();
        }
        return false;
    }

    @Override
    public String toString() {
        return  id + " - " + name;
    }
}

package pt.ipleiria.estg.dei.phonespeak.model;

import java.io.Serializable;

/**
 * Created by Evilbrain on 28-11-2016.
 */

public class Field implements Serializable{

    private int id;
    private String name;

    public Field(int id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return name;
    }
}

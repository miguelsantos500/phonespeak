package pt.ipleiria.estg.dei.phonespeak.model;

import java.util.Calendar;

/**
 * Created by Evilbrain on 28-11-2016.
 */

public class Entry {

    private Calendar created_at;
    private int id;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;
    private String field6;
    private String field7;
    private String field8;

    public Entry(Calendar created_at, int id, String field1, String field2, String field3,
                 String field4, String field5, String field6, String field7, String field8) {
        this.created_at = created_at;
        this.id = id;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.field6 = field6;
        this.field7 = field7;
        this.field8 = field8;
    }

    public Calendar getCreated_at() {
        return created_at;
    }

    public int getId() {
        return id;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    public String getField3() {
        return field3;
    }

    public String getField4() {
        return field4;
    }

    public String getField5() {
        return field5;
    }

    public String getField6() {
        return field6;
    }

    public String getField7() {
        return field7;
    }

    public String getField8() {
        return field8;
    }
}

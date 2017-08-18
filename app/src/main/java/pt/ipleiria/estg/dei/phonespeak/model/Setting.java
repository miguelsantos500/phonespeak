package pt.ipleiria.estg.dei.phonespeak.model;

import android.hardware.Sensor;

/**
 * Created by Joaquim on 25/11/2016.
 */

public class Setting
{
    private Channel channel;
    private Sensor sensor;
    private String coordinate;
    private Field field;

    public Setting(Channel channel, Sensor sensor, Field field) {
        this.channel = channel;
        this.sensor = sensor;
        this.field = field;
        this.coordinate = " ";
    }

    public Setting(Channel channel, Sensor sensor, String coordinate, Field field) {
        this.channel = channel;
        this.sensor = sensor;
        this.field = field;
        this.coordinate = coordinate;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getCoordinate() {
        return coordinate!=null?coordinate:"";
    }
}

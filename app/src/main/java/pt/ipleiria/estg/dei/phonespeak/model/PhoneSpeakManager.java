package pt.ipleiria.estg.dei.phonespeak.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Switch;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.dei.phonespeak.DashBoardActivity;
import pt.ipleiria.estg.dei.phonespeak.R;
import pt.ipleiria.estg.dei.phonespeak.SendDataActivity;
import pt.ipleiria.estg.dei.phonespeak.SendResultListener;
import pt.ipleiria.estg.dei.phonespeak.SensorReader;

/**
 * Created by magalhaes on 28/10/2016.
 */

public enum PhoneSpeakManager {
    INSTANCE;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mPrefsEditor;
    private SendResultListener sendResultListener;


    private boolean usernameVerified;
    private boolean apiKeyVerified;

    private boolean userDataVerified = false;

    private boolean loadingChannels = false;
    private Calendar loadingChannelsStartTime;

    public String getSensorsWithValue(String sensorName) {
        for (Sensor s : sensorsWithValues.keySet()) {
            if (s.getName().equals(sensorName)) {
                Log.d(DashBoardActivity.DEBUGTAG, String.valueOf(sensorsWithValues.get(s)[0]));
                return String.valueOf(sensorsWithValues.get(s)[0]);
            }
        }
        return null;
    }

    public String getSensorsWithValueXYZ(String sensorName, String coordinate) {
        for (Sensor s : sensorsWithValues.keySet()) {
            if (s.getName().equals(sensorName)) {

                switch (coordinate) {
                    case "X":
                        return String.valueOf(sensorsWithValues.get(s)[0]);
                    case "Y":
                        return String.valueOf(sensorsWithValues.get(s)[1]);
                    case "Z":
                        return String.valueOf(sensorsWithValues.get(s)[2]);
                }
            }
        }
        return null;
    }

    public void setSendResultListener(SendResultListener sendResultListener) {
        this.sendResultListener = sendResultListener;
    }

    public void setSendResult(boolean sendResult) {
        this.sendResultListener.onSendResult(sendResult);
    }

    public enum ChannelsLists {
        PUBLIC, FAV, OWN_PRIVATE, OWN_PUBLIC
    }

    private LinkedList<Channel>[] channels;

    private HashMap<Sensor, float[]> sensorsNA;
    private HashMap<Sensor, float[]> sensorsUnreliable;
    private HashMap<Sensor, float[]> sensorsWithValues;

    private SensorReader sensorReader;

    PhoneSpeakManager() {
        channels = new LinkedList[ChannelsLists.values().length];

        for (ChannelsLists channelsLists : ChannelsLists.values()) {
            channels[channelsLists.ordinal()] = new LinkedList<>();
        }


        sensorsNA = new HashMap<>();
        sensorsWithValues = new HashMap<>();
        sensorsUnreliable = new HashMap<>();
    }


    public Calendar getLoadingChannelsStartTime() {
        return loadingChannelsStartTime;
    }

    public void setLoadingChannelsStartTime(Calendar loadingChannelsStartTime) {
        this.loadingChannelsStartTime = loadingChannelsStartTime;
    }

    public boolean isLoadingChannels() {
        return loadingChannels;
    }

    public void setLoadingChannels(boolean loadingChannels) {
        this.loadingChannels = loadingChannels;
    }

    public HashMap<Sensor, float[]> getSensorsNA() {
        return sensorsNA;
    }

    public void setSensorsNA(HashMap<Sensor, float[]> sensorsNA) {
        this.sensorsNA = sensorsNA;
    }

    public HashMap<Sensor, float[]> getSensorsUnreliable() {
        return sensorsUnreliable;
    }

    public void setSensorsUnreliable(HashMap<Sensor, float[]> sensorsUnreliable) {
        this.sensorsUnreliable = sensorsUnreliable;
    }

    public HashMap<Sensor, float[]> getSensorsWithValues() {
        return sensorsWithValues;
    }

    public void setSensorsWithValues(HashMap<Sensor, float[]> sensorsWithValues) {
        this.sensorsWithValues = sensorsWithValues;
    }

    public SensorReader getSensorReader() {
        return sensorReader;
    }

    public void setSensorReader(SensorReader sensorReader) {
        this.sensorReader = sensorReader;
    }

    public LinkedList<Channel> getChannels(ChannelsLists list) {
        return channels[list.ordinal()];
    }

    public boolean isUserDataVerified() {
        return userDataVerified;
    }

    public void setUserDataVerified(boolean userDataVerified) {
        this.userDataVerified = userDataVerified;
        this.usernameVerified = userDataVerified;
        this.apiKeyVerified = userDataVerified;
    }

    public void addChannel(Channel channel, ChannelsLists list) {
        if (!channels[list.ordinal()].contains(channel)) {
            channels[list.ordinal()].add(channel);
        }
    }

    public void removeChannel(Channel c, ChannelsLists list) {
        if (channels[list.ordinal()].contains(c)) {
            channels[list.ordinal()].remove(getChannelById(c.getId()));
        }
    }


    public Channel getChannelById(int id) {
        return getById(id, channels[ChannelsLists.PUBLIC.ordinal()]) != null ? getById(id, channels[ChannelsLists.PUBLIC.ordinal()]) :
                getById(id, channels[ChannelsLists.OWN_PRIVATE.ordinal()]) != null ? getById(id, channels[ChannelsLists.OWN_PRIVATE.ordinal()])
                        : getById(id, channels[ChannelsLists.OWN_PUBLIC.ordinal()]);
    }

    public int ownPublicChannelExists(int id) {
        return getById(id, channels[ChannelsLists.OWN_PUBLIC.ordinal()]) != null ? id : -1;
    }

    public Channel getFavChannelById(int id) {
        return getById(id, channels[ChannelsLists.FAV.ordinal()]);
    }

    private Channel getById(int id, List<Channel> channels) {
        for (Channel channel : channels) {
            if (channel.getId() == id) {
                return channel;
            }
        }
        return null;
    }

    public boolean isFavorite(int id) {
        if (channels[ChannelsLists.FAV.ordinal()].size() > 0) {
            for (Channel ch : channels[ChannelsLists.FAV.ordinal()]) {
                if (ch.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public void cleanList(ChannelsLists list) {
        while (channels[list.ordinal()].size() > 0) {
            channels[list.ordinal()].remove(0);
        }
    }

    public LinkedList<Channel> getOwnChannels() {
        LinkedList<Channel> ownChannels = new LinkedList<Channel>();
        for (Channel c : this.channels[ChannelsLists.OWN_PRIVATE.ordinal()]) {
            ownChannels.add(c);
        }
        for (Channel c : this.channels[ChannelsLists.OWN_PUBLIC.ordinal()]) {
            ownChannels.add(c);
        }
        return ownChannels;
    }

    public boolean isUsernameVerified() {
        return usernameVerified;
    }

    public void setUsernameVerified(boolean usernameVerified) {
        this.usernameVerified = usernameVerified;
    }

    public boolean isApiKeyVerified() {
        return apiKeyVerified;
    }

    public void setApiKeyVerified(boolean apiKeyVerified) {
        this.apiKeyVerified = apiKeyVerified;
    }

    public LinkedList<Setting> getSettings(Activity activity, String string) {
            LinkedList<Setting> settings = new LinkedList<>();
            Gson gson = new Gson();
            mPrefs = getPreferences(activity);
            String json = "";

            json = mPrefs.getString(string, "");

            Setting settingsArray[] = gson.fromJson(json, Setting[].class);

            if (settingsArray != null) {
                for (Setting s : settingsArray) {
                    settings.add(s);
                }
            }


            return settings;
    }

    public SharedPreferences getPreferences(Activity activity) {
        return PreferenceManager.getDefaultSharedPreferences(activity);

    }

}

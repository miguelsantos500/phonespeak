package pt.ipleiria.estg.dei.phonespeak;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import pt.ipleiria.estg.dei.phonespeak.executeURL.ExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.Field;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;
import pt.ipleiria.estg.dei.phonespeak.model.Setting;

import static pt.ipleiria.estg.dei.phonespeak.R.string.settings;

/**
 * Created by joao on 05-12-2016.
 */

public class ScheduledSync extends BroadcastReceiver {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private Setting[] settingsArray;
    private ArrayList<Channel> channels = new ArrayList<>();
    private ArrayList<Field> fields = new ArrayList<>();
    private ArrayList<String> coordinates = new ArrayList<>();
    private ArrayList<Sensor> sensors = new ArrayList<>();
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(DashBoardActivity.DEBUGTAG, Calendar.getInstance().getTimeInMillis()+"");
/*
        WORKS ON API 23 BUT NOT ON API 25!.....
        ArrayList<Channel> channels = (ArrayList<Channel>)intent.getSerializableExtra("channels");
        ArrayList<Field> fields = (ArrayList<Field>)intent.getSerializableExtra("fields");
        ArrayList<String> coordinates = intent.getStringArrayListExtra("coordinates");
        ArrayList<String> sensorNames = intent.getStringArrayListExtra("sensorNames");*/
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        this.context = context;
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String settingsTest = sharedPreferences.getString("settingsToBeSent", "");
        settingsArray = gson.fromJson(settingsTest, Setting[].class);
        if(settingsArray != null) { // in case user deletes settings lists
            for (Setting s : settingsArray) {
                channels.add(s.getChannel());
                fields.add(s.getField());
                coordinates.add(s.getCoordinate());
                if (sensors.isEmpty()) {
                    sensors.add(s.getSensor());
                } else {
                    boolean notOnTheList = true;
                    for (Sensor sensor : sensors) {
                        if (sensor.getName().equals(s.getSensor().getName())) {
                            notOnTheList = false;
                        }
                    }
                    if (notOnTheList) {
                        sensors.add(s.getSensor());
                    }
                }
            }

            syncData();



        }
        else
        {
            DateFormat dF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Calendar c = Calendar.getInstance();
            Log.d(DashBoardActivity.DEBUGTAG, "CAN'T SEND EMPTY SETTINGS");
            editor.putString(context.getString(R.string.last_sync), "Error! Service running with no settings at\n" + dF.format(c.getTime()));
            editor.commit();

        }

    }

    private void syncData() {
        Log.d(DashBoardActivity.DEBUGTAG, "INITIALIZATION OF UPDATESENSORS");
        new UpdateSensors(sensors, context, this);
    }

    public void sendData(HashMap<String, float[]> sensorsAndValues){
        Log.d(DashBoardActivity.DEBUGTAG, "VALUES ARRIVED!!");

        ArrayList<String> sensorNames = new ArrayList<>();
        for (Setting s : settingsArray){
            sensorNames.add(s.getSensor().getName());
        }


        if (!channels.isEmpty()) {
            String values;
            HashMap<Integer, String> channelsAndUrls = new HashMap<>();
            for(Channel c : channels){
                channelsAndUrls.put(c.getId(), "https://api.thingspeak.com/update.json"
                        + "?api_key=" + c.getWriteAPI());
            }
            for (int i = 0; i<channels.size(); i++){
                if (coordinates.get(i).equals(" ")) {
                    values = sensorsAndValues.get(sensorNames.get(i))[0]+"";
                } else {
                    switch (coordinates.get(i)){
                        case " ": case "X":
                            values = sensorsAndValues.get(sensorNames.get(i))[0]+"";
                            break;
                        case "Y":
                            values = sensorsAndValues.get(sensorNames.get(i))[1]+"";
                            break;
                        case "Z":
                            values = sensorsAndValues.get(sensorNames.get(i))[2]+"";
                            break;
                        default:
                            values = "0";
                    }
                }
                channelsAndUrls.put(channels.get(i).getId(),
                        channelsAndUrls.get(channels.get(i).getId())+ "&field" + fields.get(i).getId() + "=" + values);
            }
            for (String url : channelsAndUrls.values()){

                new ExecuteURL().execute(url);
                Log.d(DashBoardActivity.DEBUGTAG, url);
                DateFormat dF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                Calendar c = Calendar.getInstance();
                try {
                    editor.putString("Last Sync", "(A) Last sync: " + dF.format(c.getTime()));
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    }
}

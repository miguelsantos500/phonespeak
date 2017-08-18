package pt.ipleiria.estg.dei.phonespeak;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.Field;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;
import pt.ipleiria.estg.dei.phonespeak.model.Setting;

public class SyncDataActivity extends SettingsActivity {


    private static final int REQUEST_SYNC = 2;
    private static final int REQUEST_SYNC_NO_CHANGES = 3;
    private SharedPreferences prefs;
    private boolean listChanged;
    private Button btnSave;
    private int selectedDate = 0;
    private String selected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sync_data);
        super.onCreate(savedInstanceState);
        selected = prefs.getString(getString(R.string.selected_granularity), "");
        if (selected.isEmpty()) {
            selected = getIntent().getStringExtra("selected");
            if (selected.equals("month") || selected.equals("year")) {
                selectedDate = getIntent().getIntExtra("selectedDate", 0);
            }
        }

    }

    public void initialize() {
        super.initialize();
        prefs = PhoneSpeakManager.INSTANCE.getPreferences(this);


        listChanged = prefs.getBoolean(getString(R.string.list_changed), false);

        if (listChanged) {
            settings = PhoneSpeakManager.INSTANCE.getSettings(this, getString(R.string.autoSync));

        } else {
            settings = PhoneSpeakManager.INSTANCE.getSettings(this, getString(R.string.manualSync));
        }
        listView.setAdapter(new SettingAdapter(this, settings));


        btnSave = (Button) findViewById(R.id.btnSend);
    }

    public void onClickAddSettings(View view) {
        super.onClickAddSettings(view);

        listChanged = true;
    }

    public void onClickBtnDeleteSetting(View view) {
        super.onClickBtnDeleteSetting(view);

        listChanged = true;
    }

    @Override
    protected void putData(SharedPreferences.Editor prefsEditor, String json) {

        listChanged = false;
        for (Setting s : settings) {
            if (!PhoneSpeakManager.INSTANCE.getSettings(this, getString(R.string.manualSync)).contains(s)) {
                listChanged = true;
                changeSavedList(prefsEditor, json);
                break;
            }
        }

        if (!listChanged) {

            prefsEditor.putString(getString(R.string.manualSync), json);
            listChanged = false;
            prefsEditor.putBoolean(getString(R.string.list_changed), listChanged);
        }

    }

    public void changeSavedList(SharedPreferences.Editor prefsEditor, String json) {
        prefsEditor.putString(getString(R.string.autoSync), json);
        listChanged = true;
        prefsEditor.putBoolean(getString(R.string.list_changed), listChanged);
    }


    public void onClickSave(View view) {

        if (listChanged) {
            saveSettings();
            setResult(REQUEST_SYNC);

        } else {
            //Toast.makeText(this, "There were no changes to be saved.", Toast.LENGTH_SHORT).show();
            setResult(REQUEST_SYNC_NO_CHANGES);
        }

        startService();

        finish();
    }

    private void startService() {
        PendingIntent pendingIntent;
        AlarmManager manager;

        long alarmInterval = 60;
        long startTime;
        Calendar start = Calendar.getInstance();

        switch (selected) {
            case "minute":
                start.set(Calendar.MINUTE, start.get(Calendar.MINUTE) + 1);
                start.set(Calendar.SECOND, 0);
                start.set(Calendar.MILLISECOND, 0);
                alarmInterval = 60;
                break;
            case "hour":
                start.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY) + 1);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                start.set(Calendar.MILLISECOND, 0);
                alarmInterval = 60 * 60;
                break;
            case "day":
                start.set(Calendar.DAY_OF_MONTH, start.get(Calendar.DAY_OF_MONTH) + 1);
                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                start.set(Calendar.MILLISECOND, 0);
                alarmInterval = 60 * 60 * 24;
                break;
            case "week":
                start.set(Calendar.WEEK_OF_YEAR, start.get(Calendar.WEEK_OF_YEAR) + 1);
                start.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                start.set(Calendar.MILLISECOND, 0);
                break;
            case "month":
                if (selectedDate < start.get(Calendar.DAY_OF_MONTH)) {
                    start.set(Calendar.MONTH,
                            start.get(Calendar.MONTH) + 1);
                }
                start.set(Calendar.DAY_OF_MONTH, 1);
                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                start.set(Calendar.MILLISECOND, 0);
                break;
            case "year":
                if (selectedDate < start.get(Calendar.MONTH)) {
                    start.set(Calendar.YEAR,
                            start.get(Calendar.YEAR) + 1);
                }
                start.set(Calendar.MONTH, Calendar.JANUARY);
                start.set(Calendar.DAY_OF_MONTH, 1);
                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                start.set(Calendar.MILLISECOND, 0);
                alarmInterval = 60 * 60 * 24 * 366;
        }
        startTime = start.getTimeInMillis();

        Intent scheduleIntent = new Intent(this, ScheduledSync.class);

        ArrayList<Channel> channels = new ArrayList<>();
        ArrayList<Field> fields = new ArrayList<>();
        ArrayList<String> coordinates = new ArrayList<>();
        ArrayList<String> sensorNames = new ArrayList<>();

        for (Setting s : settings) {
            channels.add(s.getChannel());
            fields.add(s.getField());
            coordinates.add(s.getCoordinate());
            sensorNames.add(s.getSensor().getName());
        }


/*
        WORKS ON API 23 BUT NOT ON API 25!.....
        scheduleIntent.putExtra("channels", channels);
        scheduleIntent.putExtra("fields", fields);
        scheduleIntent.putExtra("coordinates", coordinates);
        scheduleIntent.putExtra("sensorNames", sensorNames);*/
        SharedPreferences preferences = PhoneSpeakManager.INSTANCE.getPreferences(this);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putString(getString(R.string.selected_granularity), selected);

        Gson gson = new Gson();
        Setting settingsArray[] = settings.toArray(new Setting[settings.size()]);
        String json = gson.toJson(settingsArray);
        edit.putString("settingsToBeSent", json);
        edit.commit();


        pendingIntent = PendingIntent.getBroadcast(this, 0, scheduleIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager = (AlarmManager)

                getSystemService(Context.ALARM_SERVICE);

        Log.d(DashBoardActivity.DEBUGTAG, "INTERVAL: " + alarmInterval);
        Log.d(DashBoardActivity.DEBUGTAG, "DATE: " + start.toString());
        Log.d(DashBoardActivity.DEBUGTAG, "MONTH: " + start.get(Calendar.MONTH));
        Log.d(DashBoardActivity.DEBUGTAG, "DAY: " + start.get(Calendar.DAY_OF_MONTH));
        Log.d(DashBoardActivity.DEBUGTAG, "HOUR: " + start.get(Calendar.HOUR_OF_DAY));
        Log.d(DashBoardActivity.DEBUGTAG, "MINUTE: " + start.get(Calendar.MINUTE));
        Log.d(DashBoardActivity.DEBUGTAG, "SECOND: " + start.get(Calendar.SECOND));
        Log.d(DashBoardActivity.DEBUGTAG, "MILLISECOND: " + start.get(Calendar.MILLISECOND));

        manager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, alarmInterval * 1000, pendingIntent);

    }
}
package pt.ipleiria.estg.dei.phonespeak;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.dei.phonespeak.executeURL.ExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.executeURL.PostExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.Field;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;
import pt.ipleiria.estg.dei.phonespeak.model.Setting;

public class SendDataActivity extends SettingsActivity {


    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);


        listView.setAdapter(new SettingAdapter(this, settings));


    }

    public void onClickSend(View view) {

        if (!settings.isEmpty()) {
            for (Setting s : settings) {
                String url = "https://api.thingspeak.com/update.json";
                url += "?api_key=" + s.getChannel().getWriteAPI();


                String values;
                if (s.getCoordinate().equals(" ")) {
                    values = PhoneSpeakManager.INSTANCE.getSensorsWithValue(s.getSensor().getName());
                } else {
                    values = PhoneSpeakManager.INSTANCE.getSensorsWithValueXYZ(s.getSensor().getName(), s.getCoordinate());
                }

                url += "&field" + s.getField().getId() + "=" + values;

                Log.d(DashBoardActivity.DEBUGTAG, url);


                new PostExecuteURL().execute(url);

                saveSettings();

            }
            setResult(Activity.RESULT_OK);
            finish();
        }

    }
    /// ASK PO - MAKE ALERT ?
    // new ExecutePostURL().execute("https://api.thingspeak.com/update.json?api_key=ACCOT8O8Z6QMHRSS&field1=75");


    public void initialize() {
        super.initialize();

        settings = PhoneSpeakManager.INSTANCE.getSettings(this, getString(R.string.manualSync));

        btnSend = (Button) findViewById(R.id.btnSend);
    }


    @Override
    protected void putData(SharedPreferences.Editor prefsEditor, String json) {
        prefsEditor.putString(getString(R.string.manualSync), json);

    }


}

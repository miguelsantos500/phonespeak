package pt.ipleiria.estg.dei.phonespeak;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.Field;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;
import pt.ipleiria.estg.dei.phonespeak.model.Setting;

/**
 * Created by joaoo on 01/12/2016.
 */
public abstract class SettingsActivity extends AppCompatActivity {

    protected Spinner spinnerSensor;
    protected Spinner spinnerChannel;
    protected Spinner spinnerField;
    protected Spinner spinnerXYZ;

    List<Sensor> spinnerArraySensors;
    List<String> spinnerArrayCoordinates;

    protected Button btnAddSettings;
    protected List<Setting> settings;
    protected ListView listView;
    protected SharedPreferences mPrefs;
    protected boolean changed;
    private SensorReader sensorReader;
    protected TextView txtErrorLogSet;


    protected void updateListView() {
        populateSpinners();

        listView.setAdapter(new SettingAdapter(this, settings));
    }

    protected void populateSpinners() {
        //Sensores

        sensorReader = new SensorReader(this);
        HashMap<Sensor, float[]> hashMap = PhoneSpeakManager.INSTANCE.getSensorsWithValues();

        spinnerArraySensors = new ArrayList<>();
        spinnerArrayCoordinates = new ArrayList<>();

        //spinnerArraySensors.add(getString(R.string.choose_sensor));
        spinnerArrayCoordinates.add("X");
        spinnerArrayCoordinates.add("Y");
        spinnerArrayCoordinates.add("Z");
        for (Sensor s : hashMap.keySet()) {
            float[] values = hashMap.get(s);

//            Log.d(DashBoardActivity.DEBUGTAG, "Length: " + values.length);
            if (values != null) {
                if (values.length == 3) {
                    spinnerXYZ.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArrayCoordinates));
                    spinnerArraySensors.add(s);
                } else if (values.length == 1) {
                    spinnerArraySensors.add(s);
                }
            }
        }

        if (!settings.isEmpty()) {
            for (Setting s : settings) {

                Sensor sensorFromHash = null;
                for (Sensor sensor : hashMap.keySet()) {
                    if (s.getSensor().getName().equals(sensor.getName())) {
                        sensorFromHash = sensor;
                    }
                }

                int number = settingsCoordinatesCount(sensorFromHash, hashMap);

                if (number == -1 || number == 3) { // -1 or -2 if only have one value, -1 if is already in use

                    if (spinnerArraySensors.contains(sensorFromHash)) {
                        spinnerArraySensors.remove(spinnerArraySensors.get(spinnerArraySensors.indexOf(sensorFromHash)));
                    }
                }

            }
        }

        SpinnerSensorsAdapter adapterSensors = new SpinnerSensorsAdapter(
                this, spinnerArraySensors);
        adapterSensors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSensor.setAdapter(adapterSensors);
        if (spinnerArraySensors.size() == 0) {
            spinnerSensor.setEnabled(false);
            this.txtErrorLogSet.setText("No sensors to show");
        } else {
            spinnerSensor.setEnabled(true);
            this.txtErrorLogSet.setText("");

        }
        //Canais
        List<Channel> spinnerArrayChannels = new ArrayList<>();
        //spinnerArrayChannels.add(getString(R.string.choose_channel));
        for (Channel c : PhoneSpeakManager.INSTANCE.getOwnChannels()) {
            if (c.getAllFields().size() != settingsFieldsCount(c)) {
                spinnerArrayChannels.add(c);
            }
        }


        //Fields
        if (spinnerArrayChannels.isEmpty()) {
            List<Field> spinnerArrayFields = new ArrayList<>();

            ArrayAdapter<Field> adapterField = new ArrayAdapter<>(
                    SettingsActivity.this, android.R.layout.simple_spinner_item, spinnerArrayFields);

            adapterField.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerField.setAdapter(adapterField);
            spinnerField.setEnabled(false);
        }
        if (spinnerArrayChannels.size() == 0) {
            spinnerChannel.setEnabled(false);
            txtErrorLogSet.setText(R.string.no_channels_to_show);
        } else {
            spinnerChannel.setEnabled(true);
            txtErrorLogSet.setText("");
        }
        ArrayAdapter<Channel> adapterChannel = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArrayChannels);

        adapterChannel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerChannel.setAdapter(adapterChannel);


    }

    protected int settingsFieldsCount(Channel c) {
        int count = 0;
        for (Setting s : settings) {
            if (s.getChannel().getId() == c.getId()) {
                count++;
            }
        }
        return count;
    }

    protected int settingsCoordinatesCount(Sensor sensor, HashMap<Sensor, float[]> hashMap) {
        float[] values = hashMap.get(sensor);
        if (values != null) {
            if (values.length == 1) {

                for (Setting s : settings) {
                    if (sensor.getName().equals(s.getSensor().getName())) {
                        return -1;
                    }
                }

                return -2;

            }
        } else {
            return Integer.MIN_VALUE;
        }

        int count = 0;
        for (Setting s : settings) {
            if (sensor.getName().equals(s.getSensor().getName())) {
                count++;
            }
        }
        return count;
    }

    public void onClickAddSettings(View view) {
        if (spinnerChannel.getSelectedItem() == null || spinnerField.getSelectedItem() == null || spinnerSensor.getSelectedItem() == null) {
            txtErrorLogSet.setText("Some spinners are empty");
            //Toast.makeText(this, "Some spinners are empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Sensor sensor = (Sensor) spinnerSensor.getSelectedItem();

        Channel channel = (Channel) spinnerChannel.getSelectedItem();
        Field field = (Field) spinnerField.getSelectedItem();

        Boolean bool = isMultipleValues(sensor);

        if (bool != null) {
            if (bool) {
                String xyz = (String) spinnerXYZ.getSelectedItem();
                settings.add(new Setting(channel, sensor, xyz, field));
            } else {
                settings.add(new Setting(channel, sensor, field));
            }

        } else {
            txtErrorLogSet.setText("This sensor don't have values");
            //Toast.makeText(this, "This sensor don't have values", Toast.LENGTH_SHORT).show();
        }
        this.changed = true;

        updateListView();

    }

    public Boolean isMultipleValues(Sensor s) {
        HashMap<Sensor, float[]> hashMap = PhoneSpeakManager.INSTANCE.getSensorsWithValues();
        float[] values = hashMap.get(s);
        if (values != null) {
            if (values.length == 1) {
                return false;
            } else {
                return true;
            }
        }
        return null;
    }

    public void onClickBtnDeleteSetting(View view) {
        ImageButton btn = (ImageButton) view.findViewById(R.id.btnDeleteSetting);
        Setting s = (Setting) btn.getTag();

        for (Setting setting : settings) {
            if (setting.getSensor().getName().equals(s.getSensor().getName())
                    && setting.getCoordinate().equals(s.getCoordinate())) {
                this.changed = true;
                settings.remove(settings.get(settings.indexOf(setting)));
                updateListView();
                return;
            }
        }
    }

    protected void saveSettings() {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        Setting settingsArray[] = settings.toArray(new Setting[settings.size()]);
        String json = gson.toJson(settingsArray);
        putData(prefsEditor, json);
        prefsEditor.commit();
    }

    protected abstract void putData(SharedPreferences.Editor prefsEditor, String json);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();
        txtErrorLogSet.setText("");

        populateSpinners();

        updateListView();

    }

    public void initialize() {
        this.changed = false;

        spinnerXYZ = (Spinner) findViewById(R.id.spinnerXYZ);
        spinnerXYZ.setVisibility(View.INVISIBLE);

        mPrefs = PhoneSpeakManager.INSTANCE.getPreferences(this);
        spinnerSensor = (Spinner) findViewById(R.id.spinnerSensor);
        spinnerSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Sensor sensor = (Sensor) adapterView.getItemAtPosition(i);

                float[] values = PhoneSpeakManager.INSTANCE.getSensorsWithValues().get(sensor);

                //           Log.d(DashBoardActivity.DEBUGTAG, "Length: " + values.length);

                if(values==null){
                    spinnerXYZ.setVisibility(View.INVISIBLE);
                }else if (values.length == 3) {
                    spinnerXYZ.setVisibility(View.VISIBLE);
                } else {
                    spinnerXYZ.setVisibility(View.INVISIBLE);
                }

                if (!settings.isEmpty()) {
                    for (Setting s : settings) {
                        //           Log.d(DashBoardActivity.DEBUGTAG, s.getCoordinate());
                        if (s.getSensor().getName().equals(sensor.getName())) {
                            if (spinnerArrayCoordinates.contains(s.getCoordinate())) {
                                spinnerArrayCoordinates.remove(
                                        spinnerArrayCoordinates.get(
                                                spinnerArrayCoordinates.indexOf(s.getCoordinate())));
                            }
                        }
                    }
                    ArrayAdapter<String> adapterField = new ArrayAdapter<>(
                            SettingsActivity.this, android.R.layout.simple_spinner_item, spinnerArrayCoordinates);

                    adapterField.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerXYZ.setAdapter(adapterField);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerXYZ.setVisibility(View.INVISIBLE);
            }
        });
        spinnerChannel = (Spinner) findViewById(R.id.spinnerChannel);
        spinnerField = (Spinner) findViewById(R.id.spinnerField);

        listView = (ListView) findViewById(R.id.listViewSettings);
        btnAddSettings = (Button) findViewById(R.id.btnAddSettings);

        txtErrorLogSet = (TextView) findViewById(R.id.txtErrorLogSet);

        spinnerChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Channel channel = (Channel) adapterView.getItemAtPosition(i);

                List<Field> spinnerArrayFields = new ArrayList<>();
                //spinnerArrayFields.add(getString(R.string.choose_field));
                if (channel.getAllFields().isEmpty() || channel.getAllFields().size() == settingsFieldsCount(channel)) {
                    spinnerField.setEnabled(false);
                    txtErrorLogSet.setText("No fields to show");
                } else {

                    spinnerField.setEnabled(true);
                    txtErrorLogSet.setText("");
                    for (Field f : channel.getAllFields()) {
                        spinnerArrayFields.add(f);
                    }
                    if (!settings.isEmpty()) {
                        for (Setting s : settings) {
                            if (s.getChannel().getId() == channel.getId()) {
                                if (spinnerArrayFields.contains(s.getField())) {
                                    spinnerArrayFields.remove(
                                            spinnerArrayFields.get(spinnerArrayFields.indexOf(s.getField())));
                                }
                            }
                        }
                    }
                }

                ArrayAdapter<Field> adapterField = new ArrayAdapter<>(
                        SettingsActivity.this, android.R.layout.simple_spinner_item, spinnerArrayFields);

                adapterField.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerField.setAdapter(adapterField);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }

    @Override
    public void onBackPressed() {

        if (changed) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.goBackTitle)
                    .setMessage(R.string.goBackConfirmation)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            saveSettings();
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(R.drawable.ic_action_warning)
                    .show();
        } else {
            finish();
        }


    }
}
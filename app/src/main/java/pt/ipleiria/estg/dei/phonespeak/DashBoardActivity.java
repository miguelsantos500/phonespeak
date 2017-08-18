package pt.ipleiria.estg.dei.phonespeak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;

public class DashBoardActivity extends AppCompatActivity implements SendResultListener {

    public static final String DEBUGTAG = "PHONESPEAK";

    public static final int DATA_SENT_MANUAL = 1;
    private static final int REQUEST_SYNC = 2;
    private static final int REQUEST_SYNC_NO_CHANGES = 3;
    public static final int RESULT_ERROR = -1;

    private SensorReader sensorReader;
    private SensorManager sensorManager;
    private HashMap<Sensor, float[]> sensorsNA;
    private HashMap<Sensor, float[]> sensorsUnreliable;
    private HashMap<Sensor, float[]> sensorsWithValues;
    private ListView listSensors;
    private TextView txtResultDate;
    private TextView txtErrorLog;
    private TextView txtDashboardLog;
    private boolean realTime;
    private Switch switchRealTime;
    private Spinner spinnerSensors;
    private ImageButton btnSync;
    private TextView txtSyncStatus;
    private UpdateSensors updateSensors;
    protected SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        prefs = PhoneSpeakManager.INSTANCE.getPreferences(this);
        editor = prefs.edit();


        txtSyncStatus = (TextView) findViewById(R.id.txtSyncStatus);
        txtSyncStatus.setText(prefs.getString(getString(R.string.last_sync), ""));


        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtSyncStatus.setText(prefs.getString(getString(R.string.last_sync), ""));
                                if (txtSyncStatus.getText().equals("")) {
                                    txtSyncStatus.setText(getString(R.string.last_sync_na));
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };


        t.start();


        switchRealTime = (Switch) findViewById(R.id.switchRealTime);
        switchRealTime.setChecked(true);
        switchRealTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                realTime = isChecked;
                toggleSensorsListeners(isChecked);
                if (!realTime) {
                    DateFormat dF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    txtResultDate.setVisibility(View.VISIBLE);
                    Calendar c = Calendar.getInstance();
                    try {
                        txtResultDate.setText("Sensor Values Date: " + dF.format(c.getTime()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    txtResultDate.setText("");
                    txtResultDate.setVisibility(View.INVISIBLE);
                }
            }

        });
        realTime = false;

        txtErrorLog = (TextView) findViewById(R.id.txtDashboardLog);;
        txtSyncStatus = (TextView) findViewById(R.id.txtSyncStatus);
        txtResultDate = (TextView) findViewById(R.id.txtResultsDate);
        txtResultDate.setText(getString(R.string.empty_text));
        txtDashboardLog = (TextView) findViewById(R.id.txtDashboardLog);
        btnSync = (ImageButton) findViewById(R.id.btnManualSync);
        listSensors = (ListView) findViewById(R.id.listSensors);
        sensorReader = new SensorReader(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        PhoneSpeakManager.INSTANCE.setSensorReader(sensorReader);

        PhoneSpeakManager.INSTANCE.setSensorsNA(sensorReader.getSensorList());
        sensorsNA = PhoneSpeakManager.INSTANCE.getSensorsNA();
        sensorsWithValues = PhoneSpeakManager.INSTANCE.getSensorsWithValues();
        sensorsUnreliable = PhoneSpeakManager.INSTANCE.getSensorsUnreliable();

        listSensors.setAdapter(new SensorsAdapter(sensorsWithValues));

        populateSpinnerSensorsList();
        spinnerSensors.setSelection(0);

        updateSensors = new UpdateSensors(DashBoardActivity.this);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (switchRealTime.isChecked()) {
                    realTime = true;
                    toggleSensorsListeners(true);
                }
            }
        }, 1000);


        txtResultDate = (TextView) findViewById(R.id.txtResultsDate);
        txtResultDate.setText(getString(R.string.empty_text));
        PhoneSpeakManager.INSTANCE.setSendResultListener(this);

    }

    public void setSpinnerListener() {
        spinnerSensors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                updateSensors.updateSensorsList();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void populateSpinnerSensorsList() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(getString(R.string.sensors_with_values));
        spinnerArray.add(getString(R.string.unreliable_sensors));
        spinnerArray.add(getString(R.string.sensors_without_values));



        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSensors = (Spinner) findViewById(R.id.spinnerSensors);
        spinnerSensors.setAdapter(adapter);
    }


    public void onClickListPublicChannels(View view) {
        startActivity(new Intent(this, ListChannelsActivity.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
        toggleSensorsListeners(true);
    }


    @Override
    protected void onPause() {
        super.onPause();
        toggleSensorsListeners(false);
    }

    public void setRealTime(boolean flag) {
        realTime = flag;
        toggleSensorsListeners(flag);
    }

    private void toggleSensorsListeners(boolean flag) {
        if (realTime && flag) {
            for (Sensor sensor : sensorReader.getSensorList().keySet()) {
                Sensor s = sensorManager.getDefaultSensor(sensor.getType());
                sensorManager.registerListener(updateSensors, s, SensorManager.SENSOR_DELAY_NORMAL, 1000);
            }
        } else {
            sensorManager.unregisterListener(updateSensors);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_header, menu);
        return true;
    }

    private boolean isLoadingChannels() {
        if (PhoneSpeakManager.INSTANCE.isUserDataVerified()) {
            if (PhoneSpeakManager.INSTANCE.isLoadingChannels()) {
                long startLoadingInSeconds = PhoneSpeakManager.INSTANCE.getLoadingChannelsStartTime().getTimeInMillis() / 1000;
                long secondsElapsed = (Calendar.getInstance().getTimeInMillis() / 1000) - startLoadingInSeconds;
                if (secondsElapsed <= 10) {
                    setError(getString(R.string.still_loading_channels));
                } else {
                    setError(getString(R.string.error_loading_channels));
                }
                return true;
            } else {
                return false;
            }
        } else {
            setError(getString(R.string.loading_channels_verify));
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionAccount) {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }
        if (id == R.id.actionAutoSend) {
          String selected = prefs.getString(getString(R.string.selected_granularity),"");
            if(!selected.isEmpty())
            {
                Log.d(DashBoardActivity.DEBUGTAG, selected);
                Intent intent = new Intent(this, SyncDataActivity.class);
                startActivityForResult(intent, REQUEST_SYNC);
            }
            else if (!isLoadingChannels()){
                Intent intent = new Intent(this, GranularityActivity.class);
                startActivityForResult(intent, REQUEST_SYNC);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void setError(String error) {
        txtErrorLog.setText(error);
    }

    public void onClickBtnManualSync(View view) {
        if (!isLoadingChannels()) {
            Intent intent = new Intent(DashBoardActivity.this, SendDataActivity.class);
            startActivityForResult(intent, DATA_SENT_MANUAL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.txtDashboardLog.setText(R.string.empty_text);
        if (resultCode == RESULT_OK) {
            if (requestCode == DATA_SENT_MANUAL) {
                DateFormat dF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                Calendar c = Calendar.getInstance();
                try {
                    editor.putString(getString(R.string.last_sync), "(M) Last sync: " + dF.format(c.getTime()));
                    editor.commit();

//                    txtSyncStatus.setText(prefs.getString(getString(R.string.last_sync),""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            }
        if (requestCode == REQUEST_SYNC) {

            if (resultCode == REQUEST_SYNC_NO_CHANGES) {
                setError("No changes to be saved.");
            } else {
                setError("Synchronization data saved successfuly.");
            }

        }
    }
    @Override
    public void onSendResult(boolean sendResult) {
        String text;
        if(sendResult){
            text = getString(R.string.data_sent_success);
        }else {
            text = getString(R.string.send_data_error);
        }
        txtDashboardLog.setText(text);

        txtDashboardLog.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtDashboardLog.setText(R.string.empty_text);
            }
        }, 10000);

    }

    static class UpdateSensors extends AsyncTask<Void, Void, Void> implements SensorEventListener {

        public UpdateSensors(DashBoardActivity activity) {
            this.activity = activity;
            updateSensorsList();
            this.activity.setSpinnerListener();

        }

        private DashBoardActivity activity;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            HashMap<Sensor, float[]> sensorsAux = activity.sensorsWithValues;
            //if sensor is unreliable
            if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                sensorsAux = activity.sensorsUnreliable;
            } else {
                activity.sensorsWithValues = sensorsAux;
            }
            HashMap<Sensor, float[]> aux = new HashMap<>(sensorsAux);
            for (Sensor s : aux.keySet()) {
                if (s.getName().equals(sensorEvent.sensor.getName())) {
                    sensorsAux.remove(s);
                }
            }

            switch (sensorEvent.sensor.getType()) {
                /*case Sensor.TYPE_ACCELEROMETER:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readAccelerometerValue(sensorEvent));
                    break;*/
                case Sensor.TYPE_LIGHT:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readLightValue(sensorEvent));
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readAmbientTemperatureValue(sensorEvent));
                    break;
                case Sensor.TYPE_GRAVITY:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readGravityValue(sensorEvent));
                    break;
                case Sensor.TYPE_ORIENTATION:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readOrientationValue());
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readLinearAccelerationValue(sensorEvent));
                    break;
                case Sensor.TYPE_PROXIMITY:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readProximityValue(sensorEvent));
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readGyroscopeValue(sensorEvent));
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readMagneticFieldValue(sensorEvent));
                    break;
                case Sensor.TYPE_PRESSURE:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readPressureValue(sensorEvent));
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readRelativeHumidityValue(sensorEvent));
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readRotationVectorValue(sensorEvent));
                    break;
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readGameRotationVectorValue(sensorEvent));
                    break;
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    sensorsAux.put(sensorEvent.sensor, activity.sensorReader.readGeomagneticRotationVectorValue(sensorEvent));
                    break;
                default:
                    return;
            }
            if (activity.sensorsNA.containsKey(sensorEvent.sensor)) {
                activity.sensorsNA.remove(sensorEvent.sensor);
            }


            updateSensorsList();

        }

        private void updateSensorsList() {

            String selected = activity.spinnerSensors.getSelectedItem().toString();

            if (selected.equals("Sensors With Values")) {
                activity.listSensors.setAdapter(new SensorsAdapter(activity.sensorsWithValues));
            } else if (selected.equals("Unreliable Sensors")) {
                activity.listSensors.setAdapter(new SensorsAdapter(activity.sensorsUnreliable));
            } else {
                activity.listSensors.setAdapter(new SensorsAdapter(activity.sensorsNA));
            }
        }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do nothing.
    }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
package pt.ipleiria.estg.dei.phonespeak;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Evilbrain on 12-12-2016.
 */

public class UpdateSensors implements SensorEventListener {

    private HashMap<Sensor, float[]> sensorsAndValues;
    private SensorManager sensorManager;
    private Context context;
    private SensorReader sensorReader;
    private ScheduledSync scheduledSync;
    private Calendar start;

    public UpdateSensors(ArrayList<Sensor> sensors, Context context, ScheduledSync scheduledSync) {
        this.scheduledSync = scheduledSync;
        this.start = Calendar.getInstance();
        this.sensorsAndValues = new HashMap<>();
        for (Sensor s : sensors){
            this.sensorsAndValues.put(s, new float []{Float.MIN_VALUE,Float.MIN_VALUE, Float.MIN_VALUE });
        }
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.context=context;
        this.sensorReader=new SensorReader(context);
        registerListeners();
    }

    private void registerListeners() {
        for (Sensor sensor : sensorsAndValues.keySet()) {
            Sensor s = sensorManager.getDefaultSensor(sensor.getType());
            sensorManager.registerListener(this,  s, SensorManager.SENSOR_DELAY_NORMAL, 1000);
        }
    }
    private void unregisterListeners(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(DashBoardActivity.DEBUGTAG, "onSensorChanged");
        Sensor sensor = event.sensor;
        for (Sensor s : sensorsAndValues.keySet()){
            if(s.getName().equals(event.sensor.getName())){
                sensor=s;
                break;
            }
        }
        sensorsAndValues.put(sensor, sensorReader.readValues(event));

        if (Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis() < 5000){
            for (Sensor s : sensorsAndValues.keySet()){
                if(sensorsAndValues.get(s)[0]==Float.MIN_VALUE){
                    return;
                }
            }
        }else {
            for (Sensor s : sensorsAndValues.keySet()){
                if(sensorsAndValues.get(s)[0]==Float.MIN_VALUE){
                    sensorsAndValues.put(s,  new float []{0, 0, 0 });
                }
        }
        }


        unregisterListeners();
        HashMap<String, float[]> sensorNamesAndValues = new HashMap<>();
        for (Sensor s : sensorsAndValues.keySet()){
            sensorNamesAndValues.put(s.getName(), sensorsAndValues.get(s));
        }
        scheduledSync.sendData(sensorNamesAndValues);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do nothing
    }
}

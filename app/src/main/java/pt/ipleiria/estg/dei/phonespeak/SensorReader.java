package pt.ipleiria.estg.dei.phonespeak;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Created by joao on 20-10-2016.
 */

public class SensorReader {

    private SensorManager sensorManager;
    private Context context;


    public SensorReader(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public HashMap<Sensor, float[]> getSensorList() {
        HashMap<Sensor, float[]> sensors = new HashMap<>();

        for (Sensor sensor: sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            sensors.put(sensor, null);
        }

        return sensors;
    }
    public float[] readValues(SensorEvent event) {
        if(event.values.length!=1){
            if(event.values[1]==0 && event.values[2]==0){
                return new float[]{event.values[0]};
            }
            return new float[]{event.values[0], event.values[1], event.values[2]};
        }
        return new float[]{event.values[0]};
    }
    public float[] readAccelerometerValue(SensorEvent event) {


        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float total = Float.parseFloat(Math.sqrt(x * x + y * y + z * z) + "");

        return new float[]{total};
    }

    public float[] readLightValue(SensorEvent event) {
        return new float[]{event.values[0]};
    }

    public float[] readAmbientTemperatureValue(SensorEvent event) {
        return new float[]{event.values[0]};
    }

    public float[] readGravityValue(SensorEvent event) {
        DecimalFormat dFormat = new DecimalFormat("#.00");

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float total = Float.parseFloat(Math.sqrt(x * x + y * y + z * z) + "");

        return new float[]{total};
    }

    public float[]  readOrientationValue() {
        return new float[]{Float.parseFloat(context.getResources().getConfiguration().orientation + "")};
    }

    public float[]  readLinearAccelerationValue(SensorEvent event) {
        DecimalFormat dFormat = new DecimalFormat("0.00");

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float total = Float.parseFloat(Math.sqrt(x * x + y * y + z * z) + "");

        return new float[]{total};

    }

    public float[]  readProximityValue(SensorEvent event) {

            return new float[]{event.values[0]};
    }

    public float[]  readGyroscopeValue(SensorEvent event) {
        DecimalFormat dFormat = new DecimalFormat("0.00");

        float[] values = new float[]{event.values[0], event.values[1], event.values[2]};

        return values;
    }

    public float[]  readPressureValue(SensorEvent event) {
        return new float[]{event.values[0]};
    }

    public float[]  readRelativeHumidityValue(SensorEvent event) {
        return new float[]{event.values[0]};
    }

    public float[]  readMagneticFieldValue(SensorEvent event) {
        float[] values = new float[]{event.values[0], event.values[1], event.values[2]};

        return  values;
    }

    public float[]  readRotationVectorValue(SensorEvent event) {
        float[] values = new float[]{event.values[0], event.values[1], event.values[2]};

        return  values;
    }

    public float[]  readGameRotationVectorValue(SensorEvent event) {
        float[] values = new float[]{event.values[0], event.values[1], event.values[2]};

        return  values;
    }

    public float[]  readGeomagneticRotationVectorValue(SensorEvent event) {
        float[] values = new float[]{event.values[0], event.values[1], event.values[2]};

        return  values;
    }
}

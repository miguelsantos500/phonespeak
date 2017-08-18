package pt.ipleiria.estg.dei.phonespeak;

import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joao on 20-10-2016.
 */

public class SensorsAdapter extends BaseAdapter {

    private HashMap<Sensor, float[]> sensorsWithValues;
    private ArrayList<Sensor> sensors;

    public SensorsAdapter(HashMap<Sensor, float[]> sensors) {
        this.sensorsWithValues = sensors;
        this.sensors = new ArrayList<>();
        this.sensors.addAll(sensors.keySet());
    }

    @Override
    public int getCount() {
        return sensors.size();
    }

    @Override
    public Sensor getItem(int position) {
        return  sensors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;

        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor, parent, false);
        } else {
            view = convertView;
        }

        int i = 0;
        Sensor sensor = null;
        for (Sensor s: sensorsWithValues.keySet()) {
            if (position == i){
                sensor = s;
            }
            i++;
        }
        float[] value = sensorsWithValues.get(sensor);
        String result;
        if(value == null)
        {
            result = "N/A";
        }
        else if (value.length == 3)
        {
            result = "X: " + value[0] + "\nY: " +  value[1] + "\nZ: " + value[2];
        }
        else
        {
            result = value[0] + "";
        }


        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder.updateView(sensor, result);

        return view;
    }

    private class ViewHolder {

        private TextView txtSensorName;
        private TextView txtSensorValue;

        public ViewHolder(View view) {
            initialize(view);
        }

        private void initialize(View view) {
            txtSensorName = (TextView) view.findViewById(R.id.txtSensorName);
            txtSensorValue = (TextView) view.findViewById(R.id.txtSensorValue);

        }

        public void updateView(Sensor sensor, String value) {
            txtSensorName.setText(sensor.getName());
            txtSensorValue.setText(value);

        }

    }

}


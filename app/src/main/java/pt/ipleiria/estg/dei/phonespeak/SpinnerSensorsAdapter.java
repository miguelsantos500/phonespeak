package pt.ipleiria.estg.dei.phonespeak;

import android.content.Context;
import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joao on 20-10-2016.
 */

public class SpinnerSensorsAdapter extends ArrayAdapter<Sensor> {


    public SpinnerSensorsAdapter(Context context, List<Sensor> sensors) {
        super(context,R.layout.spinner_item_sensor, R.id.txtSensorName, sensors);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position,convertView,parent);
/*
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_sensor, parent, false);
        } else {
            view = convertView;
        }
*/
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder.updateView(getItem(position));

        return view;
    }

    private class ViewHolder {

        private TextView txtSensorName;

        public ViewHolder(View view) {
            initialize(view);
        }

        private void initialize(View view) {
            txtSensorName = (TextView) view.findViewById(R.id.txtSensorName);

        }

        public void updateView(Sensor sensor) {
            txtSensorName.setText(sensor.getName());

        }

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position,convertView,parent);
/*
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_sensor, parent, false);
        } else {
            view = convertView;
        }
*/
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder.updateView(getItem(position));

        return view;
    }
}

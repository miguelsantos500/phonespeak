package pt.ipleiria.estg.dei.phonespeak;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;
import pt.ipleiria.estg.dei.phonespeak.model.Setting;

/**
 * Created by Joaquim on 25/11/2016.
 */

public class SettingAdapter extends ArrayAdapter<Setting>
{
    public SettingAdapter(Context context, List<Setting> settings) {
        super(context, R.layout.item_setting, R.id.textViewSensor, settings);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        Setting setting = getItem(position);
        SettingAdapter.ViewHolder holder = (SettingAdapter.ViewHolder) v.getTag();

        if (holder == null) {
            holder = new SettingAdapter.ViewHolder(v);
            v.setTag(holder);
        }
        holder.updateView(setting);
        return v;
    }

    static class ViewHolder {
        public TextView textViewSensor;
        public TextView textViewXYZ;
        public TextView textViewChannel;
        public TextView textViewField;
        public ImageButton btnDeleteSetting;


        public ViewHolder(View v) {
            textViewSensor = (TextView) v.findViewById(R.id.textViewSensor);
            textViewXYZ = (TextView) v.findViewById(R.id.textViewXYZ);
            textViewChannel = (TextView) v.findViewById(R.id.textViewChannel);
            textViewField = (TextView) v.findViewById(R.id.textViewField);
            btnDeleteSetting = (ImageButton) v.findViewById(R.id.btnDeleteSetting);

        }

        public void updateView(Setting setting) {
            textViewSensor.setText(setting.getSensor().getName());
            textViewXYZ.setText(setting.getCoordinate());
            textViewChannel.setText(setting.getChannel().toString());
            textViewField.setText(setting.getField().toString());
            btnDeleteSetting.setTag(setting);
        }

    }
}

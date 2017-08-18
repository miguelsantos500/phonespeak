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

/**
 * Created by Evilbrain on 22-10-2016.
 */
public class ChannelAdapter extends ArrayAdapter<Channel> {


    public ChannelAdapter(Context context, List<Channel> channels) {
        super(context, R.layout.item_channel, R.id.textViewID, channels);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        Channel channel = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(holder);
        }
        holder.updateView(channel);
        return v;
    }

    static class ViewHolder {
        public TextView textViewID;
        public TextView textViewName;
        public ImageButton btn;
        public ImageView lock;

        public ViewHolder(View v) {
            textViewID = (TextView) v.findViewById(R.id.textViewID);
            textViewName = (TextView) v.findViewById(R.id.textViewName);
            btn = (ImageButton) v.findViewById(R.id.btnFav);
            lock = (ImageView) v.findViewById(R.id.imgPrivate);
        }

        public void updateView(Channel channel) {
            textViewID.setText(String.valueOf(channel.getId()));
            btn.setTag(channel.getId() + "");

            //channel.isPrivateChannel() ? lock.setVisibility(View.VISIBLE) : lock.setVisibility(View.INVISIBLE);

            if (channel.isPrivateChannel()) {
                lock.setVisibility(View.VISIBLE);
            } else {
                lock.setVisibility(View.INVISIBLE);
            }

            if (PhoneSpeakManager.INSTANCE.isFavorite(channel.getId())) {
                btn.setImageResource(R.drawable.ic_action_fav);
            } else {
                btn.setImageResource(R.drawable.ic_action_not_fav);
            }
            textViewName.setText(channel.getName());
        }

    }
}

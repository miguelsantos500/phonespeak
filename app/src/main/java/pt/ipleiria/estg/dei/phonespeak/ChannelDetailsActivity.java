package pt.ipleiria.estg.dei.phonespeak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;

public class ChannelDetailsActivity extends AppCompatActivity {

    private Channel channel;
    private TextView id;
    private TextView name;
    private TextView description;
    private TextView latitude;
    private TextView longitude;
    private TextView createdAt;
    private TextView elevation;
    private TextView lastEntryId;
    private TextView ranking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_details);
        Intent intent = getIntent();

        try {
            int id = Integer.parseInt(intent.getStringExtra("id"));
            String mode = intent.getStringExtra("mode");
            if (mode.equals("FAVORITE")) {
                channel = PhoneSpeakManager.INSTANCE.getFavChannelById(id);
            } else {
                channel = PhoneSpeakManager.INSTANCE.getChannelById(id);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        getTextViews();
        showChannelInfo();
    }

    private void getTextViews() {
        id = (TextView) findViewById(R.id.txtChannelId);
        name = (TextView) findViewById(R.id.txtChannelName);
        description = (TextView) findViewById(R.id.txtChannelDescription);
        latitude = (TextView) findViewById(R.id.txtChannelLatitude);
        longitude = (TextView) findViewById(R.id.txtChannelLongitude);
        createdAt = (TextView) findViewById(R.id.txtChannelCreatedAt);
        elevation = (TextView) findViewById(R.id.txtChannelElevation);
        lastEntryId = (TextView) findViewById(R.id.txtChannelLastEntryId);
        ranking = (TextView) findViewById(R.id.txtChannelRanking);
    }

    private void showChannelInfo() {
        id.setText(String.valueOf(channel.getId()));
        name.setText(channel.getName());
        description.setText(channel.getDescription());
        latitude.setText(String.valueOf(channel.getLatitude()));
        longitude.setText(String.valueOf(channel.getLongitude()));
        createdAt.setText(channel.getCreatedAt().getTime().toString());
        elevation.setText(channel.getElevation());
        lastEntryId.setText(String.valueOf(channel.getLastEntryId()));
        ranking.setText(String.valueOf(channel.getRanking()));
    }
}

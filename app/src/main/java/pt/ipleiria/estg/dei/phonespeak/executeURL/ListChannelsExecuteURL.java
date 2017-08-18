package pt.ipleiria.estg.dei.phonespeak.executeURL;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pt.ipleiria.estg.dei.phonespeak.DashBoardActivity;
import pt.ipleiria.estg.dei.phonespeak.ListChannelsActivity;
import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;
import pt.ipleiria.estg.dei.phonespeak.model.Tag;

/**
 * Created by Evilbrain on 22-11-2016.
 */

public class ListChannelsExecuteURL extends ExecuteURL {

    protected PhoneSpeakManager.ChannelsLists list;
    protected Activity activity;

    public ListChannelsExecuteURL(PhoneSpeakManager.ChannelsLists list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(String s) {

        JSONTokener token = new JSONTokener(s);
        try {
            JSONObject object = new JSONObject(token);
            JSONArray channels = object.getJSONArray("channels");

            if (channels.length() == 0) {
                if (activity instanceof ListChannelsActivity) {
                    ListChannelsActivity lstAct = (ListChannelsActivity)activity;
                    lstAct.setError("There are no channels to show");
                    return;
                }
            }

            if (object.has("pagination")) {
                JSONObject pagination = object.getJSONObject("pagination");
                int max_number_per_page = pagination.getInt("per_page");
                if (activity instanceof ListChannelsActivity) {
                    ListChannelsActivity lstAct = (ListChannelsActivity)activity;
                    lstAct.setLastPage((int) Math.ceil((double) pagination.getInt("total_entries") / max_number_per_page));
                }
            }

            PhoneSpeakManager.INSTANCE.cleanList(list);
            int i = 0;
            while (!channels.isNull(i)) {
                JSONObject channel = channels.getJSONObject(i);
                Tag[] tags = {};
                try {
                    Calendar date = Calendar.getInstance();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                    String dateString = channel.get("created_at").toString();
                    date.setTime(df.parse(dateString));
                    PhoneSpeakManager.INSTANCE.addChannel(new Channel(Integer.parseInt(channel.get("id").toString()), channel.getString("name")
                            , channel.getString("description"), Float.parseFloat(channel.get("latitude").toString()),
                            Float.parseFloat(channel.get("longitude").toString()), date,
                            channel.getString("elevation"), channel.get("last_entry_id").toString().compareTo("null") != 0 ? Integer.parseInt(channel.get("last_entry_id").toString()) : 1,
                            Integer.parseInt(channel.get("ranking").toString()), tags), list);

                    if(list== PhoneSpeakManager.ChannelsLists.OWN_PRIVATE){
                        String url = "https://api.thingspeak.com/channels/"
                                +channel.get("id").toString()+".json?api_key=";

                        SharedPreferences sPref = PhoneSpeakManager.INSTANCE.getPreferences(activity);
                        String apiKey = !sPref.getString("apiKey", "").isEmpty() ? sPref.getString("apiKey", "") : "";
                        url += apiKey;
                        Log.d(DashBoardActivity.DEBUGTAG, "OWNPRIVATE: " + url);
                        new GetChannelsAPIsExecuteURL().execute(url);
                    }else if(list== PhoneSpeakManager.ChannelsLists.OWN_PUBLIC){
                        String url = "https://api.thingspeak.com/channels/"
                                +channel.get("id").toString()+"/feeds.json";
                        Log.d(DashBoardActivity.DEBUGTAG, "OWNPUBLIC: " + url);
                        new GetChannelFeedsExecuteURL().execute(url);
                    }


                } catch (ParseException e) {
                    if (activity instanceof ListChannelsActivity) {
                        ListChannelsActivity lstAct = (ListChannelsActivity)activity;
                        lstAct.setError(e.toString());
                    }
                }
                i++;
                if (activity instanceof ListChannelsActivity) {
                    ListChannelsActivity lstAct = (ListChannelsActivity)activity;
                    lstAct.toggleButtons();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package pt.ipleiria.estg.dei.phonespeak.executeURL;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

import pt.ipleiria.estg.dei.phonespeak.DashBoardActivity;
import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.Entry;
import pt.ipleiria.estg.dei.phonespeak.model.Field;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;


/**
 * Created by Evilbrain on 28-11-2016.
 */

public class GetChannelFeedsExecuteURL extends ExecuteURL {

    @Override
    protected void onPostExecute(String s) {


        JSONTokener token = new JSONTokener(s);
        Log.d(DashBoardActivity.DEBUGTAG, "GET FEEDS!!!");
        try {
            JSONObject object = new JSONObject(token);

            JSONObject channelJSON = object.getJSONObject("channel");

            Channel channel = PhoneSpeakManager.INSTANCE.getChannelById(
                    Integer.parseInt(channelJSON.get("id").toString()));

            LinkedList<Field> fields = new LinkedList<>();
            for (int i = 1; i <= 8; i++) {
                if(channelJSON.has("field"+i)){
                    Field f = new Field(i, channelJSON.get("field"+i).toString());
                    fields.add(f);
                }
            }
            channel.setFields(fields);

            PhoneSpeakManager.INSTANCE.setLoadingChannels(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}

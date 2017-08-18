package pt.ipleiria.estg.dei.phonespeak.executeURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;

/**
 * Created by Evilbrain on 28-11-2016.
 */

public class GetChannelsAPIsExecuteURL extends ExecuteURL{


    @Override
    protected void onPostExecute(String s) {


        JSONTokener token = new JSONTokener(s);
        try {
            JSONObject channelJSON = new JSONObject(token);

            Channel channel = PhoneSpeakManager.INSTANCE.getChannelById(
                    Integer.parseInt(channelJSON.get("id").toString()));

            JSONArray api_keys = channelJSON.getJSONArray("api_keys");

            for (int i = 0; i<2;i++){
                JSONObject key = api_keys.getJSONObject(i);
                boolean flag = key.getBoolean("write_flag");
                if(flag){
                    channel.setWriteAPI(key.get("api_key").toString());
                }else{
                    channel.setReadAPI(key.get("api_key").toString());
                }
            }
            getFeeds(channel);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

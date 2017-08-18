package pt.ipleiria.estg.dei.phonespeak.executeURL;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import pt.ipleiria.estg.dei.phonespeak.R;
import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;

/**
 * Created by Evilbrain on 28-11-2016.
 */

public class ListOwnChannelsExecuteURL extends ListChannelsExecuteURL {

    public ListOwnChannelsExecuteURL(PhoneSpeakManager.ChannelsLists list, Activity activity) {
        super(list, activity);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


    }


}

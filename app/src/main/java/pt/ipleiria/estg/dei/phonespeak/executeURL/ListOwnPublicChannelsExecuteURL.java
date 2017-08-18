package pt.ipleiria.estg.dei.phonespeak.executeURL;

import android.app.Activity;

import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;

/**
 * Created by Evilbrain on 28-11-2016.
 */

public class ListOwnPublicChannelsExecuteURL extends ListOwnChannelsExecuteURL {

    public ListOwnPublicChannelsExecuteURL(Activity activity) {
        super(PhoneSpeakManager.ChannelsLists.OWN_PUBLIC, activity);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

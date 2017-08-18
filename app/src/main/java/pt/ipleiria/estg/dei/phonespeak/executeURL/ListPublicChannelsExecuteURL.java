package pt.ipleiria.estg.dei.phonespeak.executeURL;

import android.app.Activity;

import pt.ipleiria.estg.dei.phonespeak.ListChannelsActivity;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;

/**
 * Created by Evilbrain on 22-11-2016.
 */

public class ListPublicChannelsExecuteURL extends ListChannelsExecuteURL {


    public ListPublicChannelsExecuteURL(ListChannelsActivity activity) {
        super(PhoneSpeakManager.ChannelsLists.PUBLIC, activity);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        ListChannelsActivity lChannelsActivity = (ListChannelsActivity) activity;
        lChannelsActivity.updateChannelsList(PhoneSpeakManager.INSTANCE.getChannels(list));

    }
}

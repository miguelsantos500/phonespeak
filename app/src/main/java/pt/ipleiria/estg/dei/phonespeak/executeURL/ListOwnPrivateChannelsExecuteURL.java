package pt.ipleiria.estg.dei.phonespeak.executeURL;

import android.app.Activity;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import pt.ipleiria.estg.dei.phonespeak.ListChannelsActivity;
import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;

/**
 * Created by Evilbrain on 22-11-2016.
 */

public class ListOwnPrivateChannelsExecuteURL extends ListOwnChannelsExecuteURL {
    public ListOwnPrivateChannelsExecuteURL(Activity activity) {
        super(PhoneSpeakManager.ChannelsLists.OWN_PRIVATE, activity);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Iterable<Channel> threadList = new LinkedList<>(PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.OWN_PRIVATE));
        for (Channel c : threadList){
            if (PhoneSpeakManager.INSTANCE.ownPublicChannelExists(c.getId()) == -1) {
                c.setPrivateChannel();
            }else{
                PhoneSpeakManager.INSTANCE.removeChannel(c, PhoneSpeakManager.ChannelsLists.OWN_PRIVATE);
            }
        }
    }
}

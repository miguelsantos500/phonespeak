package pt.ipleiria.estg.dei.phonespeak.executeURL;

import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import pt.ipleiria.estg.dei.phonespeak.AccountActivity;
import pt.ipleiria.estg.dei.phonespeak.DashBoardActivity;
import pt.ipleiria.estg.dei.phonespeak.R;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;

/**
 * Created by Evilbrain on 22-11-2016.
 */

public class VerifyUserExecuteURL extends ExecuteURL {



    private String username;
    private AccountActivity activity;

    public VerifyUserExecuteURL(String username, AccountActivity activity) {
        this.activity=activity;
        this.username=username;
    }


    @Override
    protected void onPostExecute(String s) {
        JSONTokener token = new JSONTokener(s);
        int userId = 0;
        try {
            JSONObject user = new JSONObject(token);
            userId = user.getInt("id");


            if (userId != 0) {
                String message = "User " + username + " has been verified";
                activity.setAccountVariables(message, true, null);
                PhoneSpeakManager.INSTANCE.setUserDataVerified(true);
                Log.d(DashBoardActivity.DEBUGTAG, message);
                activity.loadChannels();
            }

        } catch (JSONException e) {
            String message = "User " + username + " does not exist";
            activity.setAccountVariables(message, false, null);
            PhoneSpeakManager.INSTANCE.setUserDataVerified(false);

        }

    }

}

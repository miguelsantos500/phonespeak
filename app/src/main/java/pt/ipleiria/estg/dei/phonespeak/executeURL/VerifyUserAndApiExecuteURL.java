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

public class VerifyUserAndApiExecuteURL extends ExecuteURL {



    private String username;
    private AccountActivity activity;

    public VerifyUserAndApiExecuteURL(String username, AccountActivity activity) {
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
            String userEmail = user.getString("email");

            if (userId != 0 && userEmail != null) {
                String message = "User " + username + " is verified with API Key";
                activity.setAccountVariables(message, true, true);
                PhoneSpeakManager.INSTANCE.setUserDataVerified(true);
                Log.d(DashBoardActivity.DEBUGTAG, message);
                activity.loadChannels();
            } else if (userId != 0) {
                PhoneSpeakManager.INSTANCE.setUserDataVerified(false);
            }

        } catch (JSONException e) {
            if (userId != 0) {
                String message = "User " + username + " does not have that API Key";
                activity.setAccountVariables(message, true, false);


            } else {
                String message = "Error! could not verify any of the user data.";
                activity.setAccountVariables(message, false, false);
            }

        }

    }

}

package pt.ipleiria.estg.dei.phonespeak.executeURL;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import pt.ipleiria.estg.dei.phonespeak.DashBoardActivity;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;

/**
 * Created by Evilbrain on 05-12-2016.
 */

public class PostExecuteURL extends ExecuteURL {


    @Override
    protected void onPostExecute(String s) {

        boolean success = true;
        JSONTokener token = new JSONTokener(s);
        try {
            JSONObject object = new JSONObject(token);

            for (int i = 1; i<=8; i++){
                String field = "field"+i;
                String fieldJSON = (String) object.get(field);
                if (!fieldJSON.isEmpty()){
                    break;
                }
                success=false;
            }

        } catch (JSONException e) {
            success=false;
        }
        PhoneSpeakManager.INSTANCE.setSendResult(success);
    }
}

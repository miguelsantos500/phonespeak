package pt.ipleiria.estg.dei.phonespeak.executeURL;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.ipleiria.estg.dei.phonespeak.model.Channel;

/**
 * Created by Evilbrain on 22-11-2016.
 */

public class ExecuteURL extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {

        InputStream is = null;
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            is = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }



    protected void getFeeds(Channel channel){
        if (!channel.getReadAPI().isEmpty()){
            new GetChannelFeedsExecuteURL().execute("https://api.thingspeak.com/channels/"
                    + channel.getId() + "/feeds.json?api_key=" + channel.getReadAPI());
        }
    }

}

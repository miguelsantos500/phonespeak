package pt.ipleiria.estg.dei.phonespeak;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import pt.ipleiria.estg.dei.phonespeak.executeURL.ListOwnPrivateChannelsExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.executeURL.ListOwnPublicChannelsExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.executeURL.VerifyUserAndApiExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.executeURL.VerifyUserExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;

public class AccountActivity extends AppCompatActivity {

    private static final String URL_USERNAME = "https://api.thingspeak.com/users/";
    private static final String URL_JSON = ".json";
    private static final String URL_APIKEY = "?api_key=";
    public static final String USERNAME = "usernameKey";
    private static final String APIKEY = "apiKey";
    private static final String USERNAME_VERIFIED = "usernameVerified";
    private static final String APIKEY_VERIFIED = "apiKeyVerified";

    private static final String URL_OWN_CHANNELS = "/channels.json";

    private EditText editTextUsername;
    private EditText editTextApiKey;

    private static String username = "";
    private static String apiKey = "";


    private boolean usernameIsSaved;
    private boolean apiKeyIsSaved;

    private ImageView imgUser;
    private ImageView imgAPI;

    private TextView textViewMessage;

    private TextWatcher usernameWatcher;
    private TextWatcher apiKeyWatcher;

    SharedPreferences sharedPref;


    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        sharedPref = PhoneSpeakManager.INSTANCE.getPreferences(this);
        editor = sharedPref.edit();

        username = sharedPref.getString(USERNAME, "");
        apiKey = sharedPref.getString(APIKEY, "");
        usernameIsSaved = true;
        apiKeyIsSaved = true;
        apiKeyWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                imgAPI.setVisibility(View.INVISIBLE);
                apiKeyIsSaved = false;

            }

            @Override
            public void afterTextChanged(Editable s) {
                apiKeyIsSaved = false;
            }
        };
        usernameWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                imgUser.setVisibility(View.INVISIBLE);
                PhoneSpeakManager.INSTANCE.setUsernameVerified(false);


            }

            @Override
            public void afterTextChanged(Editable s) {
                usernameIsSaved = false;


            }
        };
        this.editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        this.editTextApiKey = (EditText) findViewById(R.id.editTextApiKey);
        this.imgUser = (ImageView) findViewById(R.id.imgUserVerified);
        this.imgAPI = (ImageView) findViewById(R.id.imgAPIVerified);
        editTextUsername.addTextChangedListener(usernameWatcher); //Passa a verificar se o username foi alterado
        editTextApiKey.addTextChangedListener(apiKeyWatcher); //Passa a verificar se a API Key foi alterada
        if (username != null) {
            editTextUsername.setText(username);
            usernameIsSaved = true;

        }
        if (apiKey != null) {
            editTextApiKey.setText(apiKey);
            apiKeyIsSaved = true;

        }

        if (PhoneSpeakManager.INSTANCE.isUsernameVerified()) {
            imgUser.setVisibility(View.VISIBLE);
        }

        if (PhoneSpeakManager.INSTANCE.isApiKeyVerified()) {
            imgAPI.setVisibility(View.VISIBLE);
        }

        textViewMessage = (TextView) findViewById(R.id.textViewMessage);
    }

    public void onClickVerifyBtn(View view) {
        verifyUserData();
    }

    private void verifyUserData() {
        if (editTextUsername.getText().length() != 0 && editTextApiKey.getText().length() != 0) {
            new VerifyUserAndApiExecuteURL(editTextUsername.getText().toString(), this)
                    .execute(URL_USERNAME + editTextUsername.getText().toString()
                            + URL_JSON + URL_APIKEY + editTextApiKey.getText().toString());
        } else if (editTextUsername.getText().length() != 0) {

            new VerifyUserExecuteURL(editTextUsername.getText().toString(), this)
                    .execute(URL_USERNAME + editTextUsername.getText().toString() + URL_JSON);
        }

        if (editTextUsername.getText().length() == 0 && editTextApiKey.getText().length() == 0) {
            textViewMessage.setText(R.string.insertDataBeforeVerifying);
        }

        if (editTextUsername.getText().length() == 0 && editTextApiKey.getText().length() != 0) {
            textViewMessage.setText(R.string.verifyAPIKeyWithoutUsername);
        }

        PhoneSpeakManager.INSTANCE.setLoadingChannels(true);
        PhoneSpeakManager.INSTANCE.setLoadingChannelsStartTime(Calendar.getInstance());
    }

    public void onClickDeleteBtn(View view) {

        if (username.isEmpty() && apiKey.isEmpty()) {
            textViewMessage.setText(getString(R.string.noDataDeleted));
        } else {
            new AlertDialog.Builder(view.getContext())
                    .setTitle(R.string.deleteDataTitle)
                    .setMessage(R.string.deleteDataConfirm)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            editTextApiKey.setText(getString(R.string.empty_text));
                            editTextUsername.setText(getString(R.string.empty_text));
                            imgUser.setVisibility(View.INVISIBLE);
                            imgAPI.setVisibility(View.INVISIBLE);
                            clearOwnChannels();
                            resetFields();
                            editor.putString(USERNAME, getString(R.string.empty_text));
                            editor.putString(APIKEY, getString(R.string.empty_text));

                            editor.commit();
                            username = getString(R.string.empty_text);
                            apiKey = getString(R.string.empty_text);

                            apiKeyIsSaved = true;
                            usernameIsSaved = true;
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_action_warning)
                    .show();
        }


    }

    private void resetFields() {
        //reset data from local files

        if (!username.isEmpty() && !apiKey.isEmpty()) //Se ambos os campos estão guardados
        {
            editor.clear();
            editor.commit();

            apiKeyIsSaved = true; //Verifica que a eliminação destes dados foi guardada
            usernameIsSaved = true;
            username = editTextUsername.getText().toString();
            apiKey = editTextApiKey.getText().toString();

            textViewMessage.setText(getString(R.string.deletedAllData));

        } else {
            if (!username.isEmpty()) //Se apenas o username está guardado
            {
                editor.clear();
                editor.commit();
                username = editTextUsername.getText().toString();


                usernameIsSaved = true;
                //Toast.makeText(AccountActivity.this, R.string.deletedUsername, Toast.LENGTH_SHORT).show();
                textViewMessage.setText(getString(R.string.deletedUsername));
            } else if (!apiKey.isEmpty()) //Se apenas a API Key está guardada
            {
                editor.clear();
                editor.commit();
                apiKeyIsSaved = true;

                apiKey = editTextApiKey.getText().toString();

                //Toast.makeText(AccountActivity.this, R.string.deletedAPIKey, Toast.LENGTH_SHORT).show();
                textViewMessage.setText(getString(R.string.deletedAPIKey));
            }

        }

    }

    public void onClickSaveBtn(View view) {
        if(usernameIsSaved && PhoneSpeakManager.INSTANCE.isUsernameVerified()
                && apiKeyIsSaved && PhoneSpeakManager.INSTANCE.isApiKeyVerified())
        // Caso o utilizador clique no Save com o mesmo user e api key depois de guardar os settings, este método apagá-las-ia
        {
            textViewMessage.setText("Your data is already saved and verified");
        }
        else
        {
            resetFields(); // Para limpar as lista de settings do user anterior na SettingsActivity
            clearOwnChannels();
            verifyUserData();
            saveFields();
        }

    }

    private void clearOwnChannels() {
        if(PhoneSpeakManager.INSTANCE.getOwnChannels() != null)
        {
            PhoneSpeakManager.INSTANCE.cleanList(PhoneSpeakManager.ChannelsLists.OWN_PUBLIC);
            PhoneSpeakManager.INSTANCE.cleanList(PhoneSpeakManager.ChannelsLists.OWN_PRIVATE);
        }

    }

    public void loadChannels() {
        String url = URL_USERNAME + username + URL_OWN_CHANNELS;

        if (PhoneSpeakManager.INSTANCE.isApiKeyVerified()) {
            new ListOwnPrivateChannelsExecuteURL(this).execute(url + URL_APIKEY + apiKey);
        }else
        if (PhoneSpeakManager.INSTANCE.isUsernameVerified()) {
            new ListOwnPublicChannelsExecuteURL(this).execute(url);
        }

    }


    private void saveFields() {

        if (!hasEmptyField(editTextUsername) && !hasEmptyField(editTextApiKey)) {
            editor.putString(USERNAME, editTextUsername.getText().toString());
            editor.putString(APIKEY, editTextApiKey.getText().toString());
            //   editor.putBoolean(USERNAME_VERIFIED, usernameVerified);
            //  editor.putBoolean(APIKEY_VERIFIED, apiKeyVerified);
            editor.commit();
            username = editTextUsername.getText().toString();
            apiKey = editTextApiKey.getText().toString();

            apiKeyIsSaved = true;
            usernameIsSaved = true;
            textViewMessage.setText(getString(R.string.savedAllData));

        } else if (!hasEmptyField(editTextUsername)) {
            editor.putString(USERNAME, editTextUsername.getText().toString());
            editor.commit();
            username = editTextUsername.getText().toString();
            usernameIsSaved = true;
            apiKeyIsSaved = true;

            textViewMessage.setText(getString(R.string.savedUsername));
        } else if (!hasEmptyField(editTextApiKey)) {
            editor.putString(APIKEY, editTextApiKey.getText().toString());
            editor.commit();
            apiKey = editTextApiKey.getText().toString();
            apiKeyIsSaved = true;
            usernameIsSaved = true;

            textViewMessage.setText(getString(R.string.savedApiKey));
        } else {
            textViewMessage.setText(getString(R.string.allEmptyFields));
        }

        // editor.commit();

    }

    private boolean hasEmptyField(EditText editText) {
        return editText.getText().length() == 0;
    }

    @Override
    public void onBackPressed() {

        if (usernameIsSaved && apiKeyIsSaved) {
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.goBackTitle)
                    .setMessage(R.string.goBackWithoutSaving)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_action_warning)
                    .show();
        }


    }

    public void setAccountVariables(String message, boolean userVer, Boolean apiVer) {
        textViewMessage.setText(message);
        if (userVer) {
            imgUser.setImageResource(R.drawable.ic_action_right);
            PhoneSpeakManager.INSTANCE.setUsernameVerified(true);
            PhoneSpeakManager.INSTANCE.setApiKeyVerified(false);
        } else {
            imgUser.setImageResource(R.drawable.ic_action_wrong);
            PhoneSpeakManager.INSTANCE.setUsernameVerified(false);
            PhoneSpeakManager.INSTANCE.setApiKeyVerified(false);
        }
        imgUser.setVisibility(View.VISIBLE);

        if (apiVer != null) {
            if (apiVer) {
                imgAPI.setImageResource(R.drawable.ic_action_right);
                PhoneSpeakManager.INSTANCE.setApiKeyVerified(true);
            } else {
                imgAPI.setImageResource(R.drawable.ic_action_wrong);
                PhoneSpeakManager.INSTANCE.setApiKeyVerified(false);
            }
            imgAPI.setVisibility(View.VISIBLE);
        }



    }



}
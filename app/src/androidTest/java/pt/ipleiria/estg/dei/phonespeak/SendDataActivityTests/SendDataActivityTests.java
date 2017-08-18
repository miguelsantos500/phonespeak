package pt.ipleiria.estg.dei.phonespeak.SendDataActivityTests;

import android.hardware.Sensor;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import pt.ipleiria.estg.dei.phonespeak.AccountActivity;
import pt.ipleiria.estg.dei.phonespeak.DashBoardActivity;
import pt.ipleiria.estg.dei.phonespeak.R;
import pt.ipleiria.estg.dei.phonespeak.SendDataActivity;
import pt.ipleiria.estg.dei.phonespeak.SensorReader;
import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.Field;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;
import pt.ipleiria.estg.dei.phonespeak.model.Setting;

import static org.junit.Assert.*;

/**
 * Created by Evilbrain on 22/11/2016.
 */
@RunWith(AndroidJUnit4.class)
public class SendDataActivityTests {

    @Rule
    public ActivityTestRule<DashBoardActivity> activityChannelRule = new ActivityTestRule<>(DashBoardActivity.class);

    private Solo solo;


    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), activityChannelRule.getActivity());

    }

    private void getfeeds() {
        solo.clickOnView(solo.getView(R.id.actionAccount));
        solo.waitForActivity(AccountActivity.class);
        solo.clearEditText((EditText) solo.getView("editTextUsername"));
        solo.clearEditText((EditText) solo.getView("editTextApiKey"));
        solo.enterText((EditText) solo.getView("editTextUsername"),"MiguelSantos");
        solo.enterText((EditText) solo.getView("editTextApiKey"),"57V6EXFRLRQQR6BQ");
        solo.clickOnView(solo.getView(R.id.btnSaveUserDetails));
        solo.waitForText("User MiguelSantos is verified with API Key", 1, 5000);


        solo.goBack();
        solo.sleep(7000);
        solo.clickOnView(solo.getView(R.id.btnManualSync));
    }
    private void getfeedsWithoutAPIKEY() {
        solo.clickOnView(solo.getView(R.id.actionAccount));
        solo.waitForActivity(AccountActivity.class);
        solo.clearEditText((EditText) solo.getView("editTextUsername"));
        solo.clearEditText((EditText) solo.getView("editTextApiKey"));
        solo.enterText((EditText) solo.getView("editTextUsername"),"MiguelSantos");
        solo.enterText((EditText) solo.getView("editTextApiKey"),"");
        solo.clickOnView(solo.getView(R.id.btnSaveUserDetails));
        solo.waitForText( "User MiguelSantos has been verified", 1, 5000);


        solo.goBack();
        solo.sleep(7000);
        solo.clickOnView(solo.getView(R.id.btnManualSync));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();

    }
    @Test
    public void checkOpenSendDataActivity() throws Exception {

        solo.clickOnView(solo.getView(R.id.btnManualSync));
        solo.waitForActivity(SendDataActivity.class);

        solo.assertCurrentActivity("NOT in Send Data Activity", SendDataActivity.class);

    }

    @Test
    public void checkSpinnersSendDataActivity() throws Exception {

        solo.clickOnView(solo.getView(R.id.btnManualSync));
        ListView v = (ListView)solo.getView(R.id.listViewSettings);
        LinkedList<Setting> listViewSettings = new LinkedList<>();

        for (int i =0; i<v.getAdapter().getCount(); i++){
            listViewSettings.add((Setting)v.getAdapter().getItem(i));
        }
        boolean sensorFound = false;


        HashMap<Sensor, float[]> systemSensors = new SensorReader(activityChannelRule.getActivity()).getSensorList();

        for (Sensor s : systemSensors.keySet()){
            boolean found = false;
            for(Sensor sensor : PhoneSpeakManager.INSTANCE.getSensorsWithValues().keySet()){
                if(sensor.getName().equals(s.getName())){
                    found = true;
                    sensorFound = true;
                }
            }
            if(!found){
                for(Setting setting : listViewSettings){
                    if(setting.getSensor().getName().equals(s.getName())){
                        sensorFound = true;
                    }
                }
            }
            assertTrue("One Sensor was not found", sensorFound);
        }

    }


    @Test
    public void CheckSendData() throws Exception {

        getfeeds();

        solo.waitForActivity(SendDataActivity.class);

        ListView v = (ListView) solo.getView(R.id.listViewSettings);

        if(v.getAdapter().isEmpty()){
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }
        solo.clickOnView(solo.getView(R.id.btnSend));

        solo.waitForActivity(DashBoardActivity.class);

        solo.waitForText(activityChannelRule.getActivity().getString(R.string.data_sent_success),1, 5000);

        assertEquals("Sent message didn't appear!",
                activityChannelRule.getActivity().getString(R.string.data_sent_success),
                ((TextView)solo.getView(R.id.txtDashboardLog)).getText().toString());



    }
    @Test
    public void CheckSendDataError() throws Exception {

        getfeeds();

        solo.waitForActivity(SendDataActivity.class);

        ListView v = (ListView) solo.getView(R.id.listViewSettings);


        if(v.getAdapter().isEmpty()){
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }
        solo.setWiFiData(false);
        solo.setMobileData(false);
        solo.clickOnView(solo.getView(R.id.btnSend));

        solo.waitForActivity(DashBoardActivity.class);

        solo.waitForText(activityChannelRule.getActivity().getString(R.string.send_data_error),1, 5000);

        assertEquals("Error message didn't appear!",
                activityChannelRule.getActivity().getString(R.string.send_data_error),
                ((TextView)solo.getView(R.id.txtDashboardLog)).getText().toString());



    }
    @Test
    public void goBackWithNoChanges() throws Exception {

        solo.clickOnView(solo.getView(R.id.btnManualSync));

        solo.waitForActivity(SendDataActivity.class);

        solo.goBack();

        solo.waitForActivity(DashBoardActivity.class);

        solo.assertCurrentActivity("NOT in Dashboard Activity", DashBoardActivity.class);


    }
    @Test
    public void confirmGoBack() throws Exception {

        getfeeds();
        solo.waitForActivity(SendDataActivity.class);

        ListView v = (ListView) solo.getView(R.id.listViewSettings);

        LinkedList<Setting> oldSettings = PhoneSpeakManager.INSTANCE.getSettings(
                activityChannelRule.getActivity(), solo.getString(R.string.manualSync));

        if(v.getAdapter().isEmpty()){
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }else {
            solo.clickOnView(v.getChildAt(0).findViewById(R.id.btnDeleteSetting));
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }

        solo.goBack();

        solo.waitForDialogToOpen(5000);

        assertTrue("Could not find the dialog!",
                solo.searchText(solo.getString(R.string.goBackTitle)));


        solo.clickOnText(solo.getString(R.string.yes));

        solo.waitForActivity(DashBoardActivity.class);
        LinkedList<Setting> newSettings = PhoneSpeakManager.INSTANCE.getSettings(
                solo.getCurrentActivity(), solo.getString(R.string.manualSync));

        boolean different = false;

        Iterator<Setting> itOld = oldSettings.iterator();
        Iterator<Setting> itNew = newSettings.iterator();

        while ((itOld.hasNext() || itNew.hasNext()) && !different) {
            try {
                if (itOld.next() != itNew.next()) {
                    different = true;
                }
            }catch (NoSuchElementException e){
                different = true;
            }
        }

        assertTrue("The lists are the same!", different);



    }

    @Test
    public void cancelGoBack() throws Exception {

        getfeeds();
        solo.waitForActivity(SendDataActivity.class);

        ListView v = (ListView) solo.getView(R.id.listViewSettings);


        if(v.getAdapter().isEmpty()){
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }else {
            solo.clickOnView(v.getChildAt(0).findViewById(R.id.btnDeleteSetting));
        }

        solo.goBack();

        solo.waitForDialogToOpen(5000);


        assertTrue("Could not find the dialog!",
                solo.searchText(solo.getString(R.string.goBackTitle)));


        solo.clickOnText(solo.getString(R.string.no));

        solo.waitForActivity(DashBoardActivity.class);

        solo.assertCurrentActivity("Not in Dashboard Activity!", DashBoardActivity.class);
    }


    @Test
    public void checkChannelField() throws Exception {
        getfeeds();
        solo.waitForActivity(SendDataActivity.class);

        ListView v = (ListView) solo.getView(R.id.listViewSettings);

        while(!v.getAdapter().isEmpty()){
            solo.clickOnView(v.getChildAt(0).findViewById(R.id.btnDeleteSetting));
        }

        assertTrue("List is not empty!", v.getAdapter().isEmpty());

        Channel selectedChannel = (Channel)((Spinner)solo.getView(R.id.spinnerChannel))
                .getSelectedItem();

        LinkedList<Field> selectedChannelFields = selectedChannel.getAllFields();

        LinkedList<Field> fieldsFromSpinner = new LinkedList<>();

        Spinner fieldSpinner = (Spinner)solo.getView(R.id.spinnerField);

        for( int i = 0; i <fieldSpinner.getAdapter().getCount(); i++){
            fieldsFromSpinner.add((Field)fieldSpinner.getItemAtPosition(i));
        }
        assertArrayEquals("Fields lists are different!", selectedChannelFields.toArray(), fieldsFromSpinner.toArray());
    }


    @Test
    public void noAvailableFields() throws Exception {
        getfeeds();
        solo.waitForActivity(SendDataActivity.class);

        ListView v = (ListView) solo.getView(R.id.listViewSettings);

        while(!v.getAdapter().isEmpty()){
            solo.clickOnView(v.getChildAt(0).findViewById(R.id.btnDeleteSetting));
        }

        assertTrue("List is not empty!", v.getAdapter().isEmpty());

        Spinner fieldSpinner = (Spinner)solo.getView(R.id.spinnerField);

        int clicks = fieldSpinner.getAdapter().getCount();

        Spinner spinnerChannel = (Spinner)solo.getView(R.id.spinnerChannel);

        Channel channel = (Channel)spinnerChannel.getSelectedItem();

        for(int i = 0; i< clicks; i++){
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }

        solo.sleep(1000);

        for( int i = 0; i <spinnerChannel.getAdapter().getCount(); i++){
            assertNotEquals("Channel exists in Spinner!",
                    spinnerChannel.getItemAtPosition(i), channel);
        }

    }


    @Test
    public void noChannelsToShow() throws Exception {
        getfeeds();
        solo.waitForActivity(SendDataActivity.class);


        Spinner fieldSpinner = (Spinner) solo.getView(R.id.spinnerField);
        Spinner spinnerChannel = (Spinner) solo.getView(R.id.spinnerChannel);

        TextView textView = (TextView)solo.getView(R.id.txtErrorLogSet);
        while (textView.getText().toString().equals(solo.getString(R.string.empty_text))) {
            int clicks = fieldSpinner.getAdapter().getCount();
            for (int i = 0; i < clicks; i++) {
                solo.clickOnView(solo.getView(R.id.btnAddSettings));
            }
        }


        assertTrue("No such string!(No channels)",solo.searchText("Some spinners are empty"));

    }



}
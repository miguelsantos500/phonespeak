package pt.ipleiria.estg.dei.phonespeak.SyncDataActivityTests;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import pt.ipleiria.estg.dei.phonespeak.AccountActivity;
import pt.ipleiria.estg.dei.phonespeak.DashBoardActivity;
import pt.ipleiria.estg.dei.phonespeak.GranularityActivity;
import pt.ipleiria.estg.dei.phonespeak.R;
import pt.ipleiria.estg.dei.phonespeak.SendDataActivity;
import pt.ipleiria.estg.dei.phonespeak.SyncDataActivity;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;
import pt.ipleiria.estg.dei.phonespeak.model.Setting;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by dinis on 05-12-2016.
 */


@RunWith(AndroidJUnit4.class)

public class SyncDataActivityTests {
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
        solo.enterText((EditText) solo.getView("editTextUsername"), "MiguelSantos");
        solo.enterText((EditText) solo.getView("editTextApiKey"), "57V6EXFRLRQQR6BQ");
        solo.clickOnView(solo.getView(R.id.btnSaveUserDetails));
        solo.waitForText("User MiguelSantos is verified with API Key", 1, 5000);


        solo.goBack();
    }

    private void waitForChannelsAndFields() {
        solo.sleep(30000);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();

    }

    @Test
    public void checkAutoSyncMessage() throws Exception {
        getfeeds();
        waitForChannelsAndFields();

        //envio primeiro um manual para forçar a txtSyncStatus a ter um valor M
        solo.clickOnView(solo.getView(R.id.btnManualSync));

        solo.waitForActivity(SendDataActivity.class);

        ListView v = (ListView) solo.getView(R.id.listViewSettings);

        if (v.getAdapter().isEmpty()) {
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }
        solo.clickOnView(solo.getView(R.id.btnSend));

        solo.waitForActivity(DashBoardActivity.class);

        TextView textView = (TextView) solo.getView(R.id.txtSyncStatus);

        String s = textView.getText().toString();
        assertEquals("M didn't show", s.charAt(1), 'M');
        //end
        //start test
        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.waitForActivity(GranularityActivity.class);

        solo.clickOnView(solo.getView(R.id.rdBtnMinute));

        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);

        ListView v2 = (ListView) solo.getView(R.id.listViewSettings);

        if (v2.getAdapter().isEmpty()) {
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }

        solo.clickOnView(solo.getView(R.id.btnSendAuto));

        solo.waitForActivity(DashBoardActivity.class);

        solo.sleep(61001);

        TextView textView2 = (TextView) solo.getView(R.id.txtSyncStatus);

        String s2 = textView2.getText().toString();
        assertEquals("A didn't show", s2.charAt(1), 'A');

    }

    @Test
    public void checkMessageAccountNotVerified() throws Exception {


        solo.clickOnView(solo.getView(R.id.actionAccount));
        solo.waitForActivity(AccountActivity.class);
        solo.clearEditText((EditText) solo.getView("editTextUsername"));
        solo.clearEditText((EditText) solo.getView("editTextApiKey"));
        solo.enterText((EditText) solo.getView("editTextUsername"), "MiguelSantos");
        solo.enterText((EditText) solo.getView("editTextApiKey"), "");
        solo.clickOnView(solo.getView(R.id.btnSaveUserDetails));

        solo.goBack();
        solo.sleep(10000);
        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        assertTrue("Message not show.", solo.searchText(solo.getString(R.string.error_loading_channels)));

        // and if

        solo.clickOnView(solo.getView(R.id.btnManualSync));

        assertTrue("Message not show.", solo.searchText(solo.getString(R.string.error_loading_channels)));


    }


    @Test
    public void checkOpenGranularityActivity() throws Exception {

        getfeeds();

        waitForChannelsAndFields();

        solo.clickOnView(solo.getView(R.id.actionAutoSend));
        solo.waitForActivity(GranularityActivity.class);

        solo.assertCurrentActivity("NOT in Granularity Activity", GranularityActivity.class);

    }


    @Test
    public void CheckSendData() throws Exception {

        getfeeds();
        waitForChannelsAndFields();
        solo.clickOnView(solo.getView(R.id.btnManualSync));

        solo.waitForActivity(SendDataActivity.class);

        ListView v = (ListView) solo.getView(R.id.listViewSettings);

        if (v.getAdapter().isEmpty()) {
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }
        solo.clickOnView(solo.getView(R.id.btnSend));

        solo.waitForActivity(DashBoardActivity.class);

        TextView textView = (TextView) solo.getView(R.id.txtSyncStatus);

        String s = textView.getText().toString();
        assertEquals("M didn't show", s.charAt(1), 'M');
    }

    @Test
    public void CancelChooseMonth() throws Exception {
        getfeeds();
        waitForChannelsAndFields();
        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.clickOnView(solo.getView(R.id.rdBtnYear));

        solo.waitForDialogToOpen();

        solo.clickOnView(solo.getView(R.id.btnCancelDialog));

        solo.assertCurrentActivity("Don't stay at Granularity Activity", GranularityActivity.class);
    }

    @Test
    public void CancelChooseDay() throws Exception {
        getfeeds();
        waitForChannelsAndFields();
        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.clickOnView(solo.getView(R.id.rdBtnMonth));

        solo.waitForDialogToOpen();

        solo.clickOnView(solo.getView(R.id.btnCancelDialog));

        solo.assertCurrentActivity("Don't stay at Granularity Activity", GranularityActivity.class);
    }

    @Test
    public void ShowChooseDayDialog() throws Exception {
        getfeeds();
        waitForChannelsAndFields();
        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.clickOnView(solo.getView(R.id.rdBtnMonth));

        solo.waitForDialogToOpen();

        assertTrue("Could not find the dialog!", solo.searchText(solo.getString(R.string.choose_day)));
        assertTrue("Could not find the cancel button!", solo.searchButton("Cancel"));
        assertTrue("Could not find the set button!", solo.searchButton("Set"));

    }

    @Test
    public void ShowChooseMonthDialog() throws Exception {
        getfeeds();
        waitForChannelsAndFields();
        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.clickOnView(solo.getView(R.id.rdBtnYear));

        solo.waitForDialogToOpen();

        assertTrue("Could not find the dialog!", solo.searchText(solo.getString(R.string.choose_month)));
        assertTrue("Could not find the cancel button!", solo.searchButton("Cancel"));
        assertTrue("Could not find the set button!", solo.searchButton("Set"));

    }

    @Test
    public void verifySyncState() throws Exception {

        getfeeds();
        waitForChannelsAndFields();
        //when start application

        assertNotEquals("Sync Text is empty", solo.getView(R.id.txtSyncStatus), solo.getString(R.string.empty_text));

        //when return to dashboard

        solo.clickOnView(solo.getView(R.id.btnListChannels));
        solo.sleep(1000);
        solo.goBack();

        solo.waitForActivity(DashBoardActivity.class);

        assertNotEquals("Sync Text is empty", solo.getView(R.id.txtSyncStatus), solo.getString(R.string.empty_text));
    }

    @Test
    public void goBackWithoutSaving() throws Exception {
        getfeeds();
        waitForChannelsAndFields();

        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.waitForActivity(GranularityActivity.class);

        solo.clickOnText(solo.getString(R.string.day));
        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);

        ListView lvSettings = (ListView) solo.getView(R.id.listViewSettings);

        if (lvSettings.getCount() == 0) {
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        } else {
            solo.clickOnView(lvSettings.getChildAt(0).findViewById(R.id.btnDeleteSetting));
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }
        solo.goBack();

        solo.waitForDialogToOpen(5000);

        assertTrue("Could not find the dialog!",
                solo.searchText(solo.getString(R.string.goBackTitle)));
    }

    @Test
    public void confirmGoBackWithoutSaving() throws Exception {
        getfeeds();
        waitForChannelsAndFields();

        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.waitForActivity(GranularityActivity.class);

        solo.clickOnText(solo.getString(R.string.day));
        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);
//GET OLD LIST
        ListView lvSettings = (ListView) solo.getView(R.id.listViewSettings);

        LinkedList<Setting> oldSettings = new LinkedList<>();
        for (int i = 0; i < lvSettings.getCount(); i++) {
            oldSettings.add((Setting) lvSettings.getAdapter().getItem(i));
        }
//
        if (lvSettings.getCount() == 0) {
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        } else {
            solo.clickOnView(lvSettings.getChildAt(0).findViewById(R.id.btnDeleteSetting));
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }
        solo.goBack();

        solo.waitForDialogToOpen(5000);

        assertTrue("Could not find the dialog!",
                solo.searchText(solo.getString(R.string.goBackTitle)));

        solo.clickOnText(solo.getString(android.R.string.yes));

//GET NEW LIST
        //solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.waitForActivity(GranularityActivity.class);

        solo.clickOnText(solo.getString(R.string.day));
        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);

        lvSettings = (ListView) solo.getView(R.id.listViewSettings);

        LinkedList<Setting> newSettings = new LinkedList<>();
        for (int i = 0; i < lvSettings.getCount(); i++) {
            newSettings.add((Setting) lvSettings.getAdapter().getItem(i));
        }
//
        boolean different = false;

        Iterator<Setting> itOld = oldSettings.iterator();
        Iterator<Setting> itNew = newSettings.iterator();

        while ((itOld.hasNext() || itNew.hasNext()) && !different) {
            try {
                if (itOld.next() != itNew.next()) {
                    different = true;
                }
            } catch (NoSuchElementException e) {
                different = true;
            }
        }

        assertTrue("The lists are the same!", different);

    }

    @Test
    public void cancelGoBackWithoutSaving() throws Exception {
        getfeeds();
        waitForChannelsAndFields();

        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.waitForActivity(GranularityActivity.class);

        solo.clickOnText(solo.getString(R.string.day));
        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);
//GET OLD LIST
        ListView lvSettings = (ListView) solo.getView(R.id.listViewSettings);

        LinkedList<Setting> oldSettings = new LinkedList<>();
        for (int i = 0; i < lvSettings.getCount(); i++) {
            oldSettings.add((Setting) lvSettings.getAdapter().getItem(i));
        }
//
        if (lvSettings.getCount() == 0) {
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        } else {
            solo.clickOnView(lvSettings.getChildAt(0).findViewById(R.id.btnDeleteSetting));
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }
        solo.goBack();

        solo.waitForDialogToOpen(5000);

        assertTrue("Could not find the dialog!",
                solo.searchText(solo.getString(R.string.goBackTitle)));

        solo.clickOnText(solo.getString(android.R.string.no));

//GET NEW LIST
        //solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.waitForActivity(GranularityActivity.class);

        solo.clickOnText(solo.getString(R.string.day));
        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);

        lvSettings = (ListView) solo.getView(R.id.listViewSettings);

        LinkedList<Setting> newSettings = new LinkedList<>();
        for (int i = 0; i < lvSettings.getCount(); i++) {
            newSettings.add((Setting) lvSettings.getAdapter().getItem(i));
        }
//
        boolean different = false;

        Iterator<Setting> itOld = oldSettings.iterator();
        Iterator<Setting> itNew = newSettings.iterator();

        while ((itOld.hasNext() || itNew.hasNext()) && !different) {
            try {
                Setting oldS = itOld.next();
                Setting newS = itNew.next();
                if (!oldS.getSensor().getName().equals(newS.getSensor().getName()) ||
                        oldS.getField().getId() != newS.getField().getId() ||
                        oldS.getChannel().getId() != newS.getChannel().getId() ||
                        !oldS.getCoordinate().equals(newS.getCoordinate())) {
                    different = true;
                }
            } catch (NoSuchElementException e) {
                different = true;
            }
        }


        assertTrue("The lists are NOT the same!", !different);

    }

    @Test
    public void confirmYear() throws Exception {

        getfeeds();
        waitForChannelsAndFields();

        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.clickOnView(solo.getView(R.id.rdBtnYear));

        solo.waitForDialogToOpen(5000);

        assertTrue("Could not find the dialog!", solo.searchText(solo.getString(R.string.choose_month)));

        solo.clickOnText("Set");

        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);

        solo.assertCurrentActivity("Not in SyncDataActivity!", SyncDataActivity.class);

    }

    @Test
    public void confirmMonth() throws Exception {

        getfeeds();
        waitForChannelsAndFields();

        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.clickOnView(solo.getView(R.id.rdBtnMonth));

        solo.waitForDialogToOpen(5000);

        assertTrue("Could not find the dialog!", solo.searchText(solo.getString(R.string.choose_day)));

        solo.clickOnText("Set");

        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);

        solo.assertCurrentActivity("Not in SyncDataActivity!", SyncDataActivity.class);

    }

    @Test
    public void confirmHour() throws Exception {

        getfeeds();

        waitForChannelsAndFields();

        solo.clickOnView(solo.getView(R.id.btnListChannels));
        solo.goBack();

        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.clickOnView(solo.getView(R.id.rdBtnHour));

        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);

        solo.assertCurrentActivity("Not in SyncDataActivity!", SyncDataActivity.class);

        solo.clickOnView(solo.getView(R.id.btnSendAuto));

        solo.waitForActivity(DashBoardActivity.class);

        String granularity = PhoneSpeakManager.INSTANCE.getPreferences(solo.getCurrentActivity()).getString(solo.getString(R.string.selected_granularity), "");
        assertEquals("Granularity is not defined as Hour", "HOUR", granularity.toUpperCase());
    }

    @Test
    public void confirmWeek() throws Exception {

        getfeeds();

        waitForChannelsAndFields();

        solo.clickOnView(solo.getView(R.id.btnListChannels));
        solo.goBack();

        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.clickOnView(solo.getView(R.id.rdBtnWeek));

        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);

        solo.assertCurrentActivity("Not in SyncDataActivity!", SyncDataActivity.class);

        solo.clickOnView(solo.getView(R.id.btnSendAuto));

        solo.waitForActivity(DashBoardActivity.class);

        String granularity = PhoneSpeakManager.INSTANCE.getPreferences(solo.getCurrentActivity()).getString(solo.getString(R.string.selected_granularity), "");
        assertEquals("Granularity is not defined as Week", "WEEK", granularity.toUpperCase());
    }

    @Test
    public void confirmMinute() throws Exception {
        getfeeds();

        waitForChannelsAndFields();

        solo.clickOnView(solo.getView(R.id.btnListChannels));
        solo.goBack();

        solo.clickOnView(solo.getView(R.id.actionAutoSend));

        solo.clickOnView(solo.getView(R.id.rdBtnMinute));

        solo.clickOnView(solo.getView(R.id.btnNextActivity));

        solo.waitForActivity(SyncDataActivity.class);

        solo.assertCurrentActivity("Not in SyncDataActivity!", SyncDataActivity.class);

        solo.clickOnView(solo.getView(R.id.btnSendAuto));

        solo.waitForActivity(DashBoardActivity.class);

        String granularity = PhoneSpeakManager.INSTANCE.getPreferences(solo.getCurrentActivity()).getString(solo.getString(R.string.selected_granularity), "");
        assertEquals("Granularity is not defined as Minute", "MINUTE", granularity.toUpperCase());
    }

    @Test
    public void saveSettings() throws Exception {
        getfeeds();
        waitForChannelsAndFields();

        //Send Auto
        solo.clickOnView(solo.getView(R.id.actionAutoSend));
        solo.waitForActivity(GranularityActivity.class);
        solo.clickOnText(solo.getString(R.string.minute));
        solo.clickOnView(solo.getView(R.id.btnNextActivity));
        solo.waitForActivity(SyncDataActivity.class);

        solo.clickOnView(solo.getView(R.id.btnAddSettings));
        solo.clickOnView(solo.getView(R.id.btnSendAuto));

        solo.waitForActivity(DashBoardActivity.class);

        assertTrue("Message not shown", solo.searchText(solo.getString(R.string.sync_data_saved)));
    }

    @Test
    public void channelsAndFieldsNotLoaded() throws Exception {
        getfeeds();

        solo.clickOnView(solo.getView(R.id.btnManualSync));

        assertTrue("Could not find 'wait and try again!'!", solo.searchText(solo.getString(R.string.loading_channels_wait)));

    }

    @Test
    public void channelsAndFieldsNotLoadedTimeout() throws Exception {
        //set android device internet after click verify for the test to work
        getfeeds();
        solo.setWiFiData(false);
        solo.setMobileData(false);

        solo.sleep(11000);
        solo.clickOnView(solo.getView(R.id.btnManualSync));
        solo.sleep(5000);
        assertTrue("Could not find 'Error loading channels!'!", solo.searchText(solo.getString(R.string.error_loading_channels)));

    }

    @Test
    public void saveSettingsAndStartService() throws Exception {
        getfeeds();
        waitForChannelsAndFields();
        //Send Manual
        solo.clickOnView(solo.getView(R.id.btnManualSync));
        solo.waitForActivity(SendDataActivity.class);

        ListView v = (ListView) solo.getView(R.id.listViewSettings);

        if (v.getAdapter().isEmpty()) {
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }
        solo.clickOnView(solo.getView(R.id.btnSend));
        solo.waitForActivity(DashBoardActivity.class);

        TextView textView = (TextView) solo.getView(R.id.txtSyncStatus);
        String s = textView.getText().toString();
        assertEquals("M didn't show", s.charAt(1), 'M');

        //Send Auto
        solo.clickOnView(solo.getView(R.id.actionAutoSend));
        solo.waitForActivity(GranularityActivity.class);
        solo.clickOnText(solo.getString(R.string.minute));
        solo.clickOnView(solo.getView(R.id.btnNextActivity));
        solo.waitForActivity(SyncDataActivity.class);

        ListView lvSettings = (ListView) solo.getView(R.id.listViewSettings);
        if (lvSettings.getCount() == 0) {
            solo.clickOnView(solo.getView(R.id.btnAddSettings));
        }
        solo.clickOnView(solo.getView(R.id.btnSendAuto));

        solo.waitForActivity(DashBoardActivity.class);

        textView = (TextView) solo.getView(R.id.txtSyncStatus);
        int i = 0;
        do {
            i++;
            solo.sleep(5000);
            s = textView.getText().toString();
        }
        while (s.charAt(1) != 'A' || i <= 13);//While 65 seconds (for Testing purposes) have not passed

        assertEquals("A didn't show", s.charAt(1), 'A');
    }


}
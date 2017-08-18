package pt.ipleiria.estg.dei.phonespeak.ListPublicChannelsActivityTests;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pt.ipleiria.estg.dei.phonespeak.AccountActivity;
import pt.ipleiria.estg.dei.phonespeak.DashBoardActivity;
import pt.ipleiria.estg.dei.phonespeak.ListChannelsActivity;
import pt.ipleiria.estg.dei.phonespeak.R;
import pt.ipleiria.estg.dei.phonespeak.model.Channel;

import static org.junit.Assert.*;

/**
 * Created by Joaquim on 09/12/2016.
 */
@RunWith(AndroidJUnit4.class)
public class ListChannelsActivitySearchTests {



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
        public void searchChannelsByID() throws Exception
        {

            solo.clickOnView(solo.getView(R.id.btnListChannels));

            solo.waitForActivity(ListChannelsActivity.class);
            solo.clickOnView(solo.getView(R.id.btnMenuSearch));
            solo.clickOnView(solo.getView(R.id.btnToggleFilters));
            solo.clickOnView(solo.getView(R.id.chkBoxName));
            solo.clickOnView(solo.getView(R.id.chkBoxDescription));
            solo.clickOnView(solo.getView(R.id.chkBoxTags));
            solo.clickOnView(solo.getView(R.id.chkBoxData));
            final SearchView searchView = (SearchView) solo.getView(R.id.searchBar);
            solo.clickOnView(searchView);


            try {
                activityChannelRule.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchView.setQuery("42", true);
                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }


            ListView channelsList = (ListView) solo.getView(R.id.listViewChannels);

            solo.sleep(5000);

            int count = 0;
            int channelCount = 0;
            boolean noChannels = false;


            if(channelsList.getCount() > 0)
            {
                do {
                    Channel c = (Channel) channelsList.getItemAtPosition(count);
                    if(String.valueOf(c.getId()).contains(searchView.getQuery()))
                    {
                        channelCount++;
                        Log.d(DashBoardActivity.DEBUGTAG, "Channel counted (" + channelCount + ")");
                    }


                    count++;
                    Log.d(DashBoardActivity.DEBUGTAG, "Counting (" + count + ")");


                }while(count < channelsList.getCount());


                assertEquals("Channels on the list don't correspond to user search (" + count + ") vs (" + channelCount + ")", count, channelCount);
            } else
            {
                //Caso a lista devolvida esteja vazia
                noChannels = true;
                assertTrue(noChannels);
            }

        }

    @Test
    public void searchChannelsByName() throws Exception
    {

        solo.clickOnView(solo.getView(R.id.btnListChannels));

        solo.waitForActivity(ListChannelsActivity.class);
        solo.clickOnView(solo.getView(R.id.btnMenuSearch));
        solo.clickOnView(solo.getView(R.id.btnToggleFilters));
        solo.clickOnView(solo.getView(R.id.chkBoxId));
       // solo.clickOnView(solo.getView(R.id.chkBoxName));
        solo.clickOnView(solo.getView(R.id.chkBoxDescription));
        solo.clickOnView(solo.getView(R.id.chkBoxTags));
        solo.clickOnView(solo.getView(R.id.chkBoxData));
        final SearchView searchView = (SearchView) solo.getView(R.id.searchBar);
        solo.clickOnView(searchView);


        try {
            activityChannelRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchView.setQuery("l", true);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        ListView channelsList = (ListView) solo.getView(R.id.listViewChannels);

        solo.sleep(5000);

        int count = 0;
        int channelCount = 0;
        boolean noChannels = false;


        if(channelsList.getCount() > 0)
        {
            do {
                Channel c = (Channel) channelsList.getItemAtPosition(count);
                if(String.valueOf(c.getName()).contains(searchView.getQuery()))
                {
                    channelCount++;
                    Log.d(DashBoardActivity.DEBUGTAG, "Channel counted (" + channelCount + ")");
                }


                count++;
                Log.d(DashBoardActivity.DEBUGTAG, "Counting (" + count + ")");


            }while(count < channelsList.getCount());



            assertEquals("Channels on the list don't correspond to user search (" + count + ") vs (" + channelCount + ")", count, channelCount);
        } else
        {
            //Caso a lista devolvida esteja vazia
            noChannels = true;
            assertTrue(noChannels);
        }

    }

    @Test
    public void searchChannelsByDescription() throws Exception
    {

        solo.clickOnView(solo.getView(R.id.btnListChannels));

        solo.waitForActivity(ListChannelsActivity.class);
        solo.clickOnView(solo.getView(R.id.btnMenuSearch));
        solo.clickOnView(solo.getView(R.id.btnToggleFilters));
        solo.clickOnView(solo.getView(R.id.chkBoxId));
        solo.clickOnView(solo.getView(R.id.chkBoxName));
        //solo.clickOnView(solo.getView(R.id.chkBoxDescription));
        solo.clickOnView(solo.getView(R.id.chkBoxTags));
        solo.clickOnView(solo.getView(R.id.chkBoxData));
        final SearchView searchView = (SearchView) solo.getView(R.id.searchBar);
        solo.clickOnView(searchView);


        try {
            activityChannelRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchView.setQuery("ui", true);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        ListView channelsList = (ListView) solo.getView(R.id.listViewChannels);

        solo.sleep(5000);

        int count = 0;
        int channelCount = 0;

        boolean noChannels = false;


        if(channelsList.getCount() > 0)
        {
            do {
                Channel c = (Channel) channelsList.getItemAtPosition(count);
                if(String.valueOf(c.getDescription()).contains(searchView.getQuery()))
                {
                    channelCount++;
                    Log.d(DashBoardActivity.DEBUGTAG, "Channel counted (" + channelCount + ")");
                }


                count++;
                Log.d(DashBoardActivity.DEBUGTAG, "Counting (" + count + ")");


            }while(count < channelsList.getCount());


            assertEquals("Channels on the list don't correspond to user search (" + count + ") vs (" + channelCount + ")", count, channelCount);
        } else
        {
            //Caso a lista devolvida esteja vazia
            noChannels = true;
            assertTrue(noChannels);
        }

    }

    @Test
    public void searchChannelsByTags() throws Exception
    {

        solo.clickOnView(solo.getView(R.id.btnListChannels));

        solo.waitForActivity(ListChannelsActivity.class);
        solo.clickOnView(solo.getView(R.id.btnMenuSearch));
        solo.clickOnView(solo.getView(R.id.btnToggleFilters));
        solo.clickOnView(solo.getView(R.id.chkBoxId));
        solo.clickOnView(solo.getView(R.id.chkBoxName));
        solo.clickOnView(solo.getView(R.id.chkBoxDescription));
        //solo.clickOnView(solo.getView(R.id.chkBoxTags));
        solo.clickOnView(solo.getView(R.id.chkBoxData));
        final SearchView searchView = (SearchView) solo.getView(R.id.searchBar);
        solo.clickOnView(searchView);


        try {
            activityChannelRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchView.setQuery("ui", true);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        ListView channelsList = (ListView) solo.getView(R.id.listViewChannels);

        solo.sleep(5000);

        int count = 0;
        int channelCount = 0;

        boolean noChannels = false;


        if(channelsList.getCount() > 0)
        {
            do {
                Channel c = (Channel) channelsList.getItemAtPosition(count);
                if(String.valueOf(c.getTags()).contains(searchView.getQuery()))
                {
                    channelCount++;
                    Log.d(DashBoardActivity.DEBUGTAG, "Channel counted (" + channelCount + ")");
                }


                count++;
                Log.d(DashBoardActivity.DEBUGTAG, "Counting (" + count + ")");


            }while(count < channelsList.getCount());


            assertEquals("Channels on the list don't correspond to user search (" + count + ") vs (" + channelCount + ")", count, channelCount);
        } else
        {
            //Caso a lista devolvida esteja vazia
            noChannels = true;
            assertTrue(noChannels);
        }

    }

    @Test
    public void searchChannelsByData() throws Exception
    {

        solo.clickOnView(solo.getView(R.id.btnListChannels));

        solo.waitForActivity(ListChannelsActivity.class);
        solo.clickOnView(solo.getView(R.id.btnMenuSearch));
        solo.clickOnView(solo.getView(R.id.btnToggleFilters));
        solo.clickOnView(solo.getView(R.id.chkBoxId));
        solo.clickOnView(solo.getView(R.id.chkBoxName));
        solo.clickOnView(solo.getView(R.id.chkBoxDescription));
        solo.clickOnView(solo.getView(R.id.chkBoxTags));
        //solo.clickOnView(solo.getView(R.id.chkBoxData));
        final SearchView searchView = (SearchView) solo.getView(R.id.searchBar);
        solo.clickOnView(searchView);


        try {
            activityChannelRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchView.setQuery("2016", true);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        ListView channelsList = (ListView) solo.getView(R.id.listViewChannels);

        solo.sleep(5000);

        int count = 0;
        int channelCount = 0;
        boolean equals = false;
        boolean noChannels = false;


        if(channelsList.getCount() > 0)
        {
            do {
                Channel c = (Channel) channelsList.getItemAtPosition(count);
                if(String.valueOf(c.getCreatedAt()).contains(searchView.getQuery()))
                {
                    channelCount++;
                    Log.d(DashBoardActivity.DEBUGTAG, "Channel counted (" + channelCount + ")");
                }


                count++;
                Log.d(DashBoardActivity.DEBUGTAG, "Counting (" + count + ")");


            }while(count < channelsList.getCount());

            if(count == channelCount)
            {
                equals = true;
            }

            assertEquals("Channels on the list don't correspond to user search (" + count + ") vs (" + channelCount + ")", count, channelCount);
        } else
        {
            //Caso a lista devolvida esteja vazia
            noChannels = true;
            assertTrue(noChannels);
        }

    }

    @Test
    public void eraseSearchQuery() throws Exception
    {

        solo.clickOnView(solo.getView(R.id.btnListChannels));

        solo.waitForActivity(ListChannelsActivity.class);
        solo.clickOnView(solo.getView(R.id.btnMenuSearch));


        final SearchView searchView = (SearchView) solo.getView(R.id.searchBar);
        solo.clickOnView(searchView);


        try {
            activityChannelRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchView.setQuery("2016", true);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        ListView channelsList = (ListView) solo.getView(R.id.listViewChannels);

        solo.sleep(5000);

        int totalChannels = 15;
        boolean noChannels = false;



        try {
            activityChannelRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchView.setQuery("", true);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }





        assertEquals("Did not return all original channels (filtered: " + channelsList.getCount() + " )", totalChannels, channelsList.getCount());

        if(channelsList.getCount() < 0)
        {
            //Caso a lista devolvida esteja vazia
            noChannels = true;
            assertTrue(noChannels);
        }

    }

    @Test
    public void deselectAllCheckboxes() throws Exception {

        solo.clickOnView(solo.getView(R.id.btnListChannels));

        solo.waitForActivity(ListChannelsActivity.class);
        solo.clickOnView(solo.getView(R.id.btnMenuSearch));
        solo.clickOnView(solo.getView(R.id.btnToggleFilters));
        solo.clickOnView(solo.getView(R.id.chkBoxId));
        solo.clickOnView(solo.getView(R.id.chkBoxName));
        solo.clickOnView(solo.getView(R.id.chkBoxDescription));
        solo.clickOnView(solo.getView(R.id.chkBoxTags));
        solo.clickOnView(solo.getView(R.id.chkBoxData));

        CheckBox checkBox = (CheckBox) solo.getView(R.id.chkBoxData);
        TextView textView = (TextView) solo.getView(R.id.txtErrorLog);

        assertTrue("Error checking box",checkBox.isChecked());
        assertEquals("Error message not displayed", solo.getString(R.string.at_least_one_filter), textView.getText().toString());

    }


    @Test
    public void showFilters() throws Exception {

        solo.clickOnView(solo.getView(R.id.btnListChannels));

        solo.waitForActivity(ListChannelsActivity.class);
        solo.clickOnView(solo.getView(R.id.btnMenuSearch));
        solo.clickOnView(solo.getView(R.id.btnToggleFilters));

        assertEquals("Filters are not showing", View.VISIBLE, solo.getView(R.id.layoutFilters).getVisibility());
        boolean allChecked = true;

        CheckBox chkBoxId = (CheckBox) solo.getView(R.id.chkBoxId);
        CheckBox chkBoxName = (CheckBox) solo.getView(R.id.chkBoxName);
        CheckBox chkBoxDescription = (CheckBox) solo.getView(R.id.chkBoxDescription);
        CheckBox chkBoxTags = (CheckBox) solo.getView(R.id.chkBoxTags);
        CheckBox chkBoxData = (CheckBox) solo.getView(R.id.chkBoxData);

        CheckBox[] checkBoxes = {chkBoxId, chkBoxName, chkBoxDescription,
                chkBoxTags, chkBoxData};

        for(CheckBox c: checkBoxes)
        {
            if(!c.isChecked())
            {
                allChecked = false;
                break;
            }
        }

        assertTrue("Not all checkboxes are checked", allChecked);

    }

    @Test
    public void hideFilters() throws Exception {

        solo.clickOnView(solo.getView(R.id.btnListChannels));

        solo.waitForActivity(ListChannelsActivity.class);
        solo.clickOnView(solo.getView(R.id.btnMenuSearch));
        solo.clickOnView(solo.getView(R.id.btnToggleFilters));

        solo.clickOnView(solo.getView(R.id.btnToggleFilters));

        assertEquals("Filters not gone", View.GONE, solo.getView(R.id.layoutFilters).getVisibility());

    }

    @Test
    public void openListChannelsActivity() throws Exception {

        solo.clickOnView(solo.getView(R.id.btnListChannels));

        solo.waitForActivity(ListChannelsActivity.class);
        solo.clickOnView(solo.getView(R.id.btnMenuSearch));

        assertEquals("Filters are showing", View.GONE, solo.getView(R.id.layoutFilters).getVisibility());

    }



}

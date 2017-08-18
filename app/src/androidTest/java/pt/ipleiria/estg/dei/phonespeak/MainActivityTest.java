package pt.ipleiria.estg.dei.phonespeak;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.robotium.solo.Solo;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by joao on 22-11-2016.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<DashBoardActivity> activityTestRule =
            new ActivityTestRule<>(DashBoardActivity.class);

    private Solo solo;

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), activityTestRule.getActivity());

    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @Test
    public void testConectivityNotOk() throws Exception {
        solo.unlockScreen();

        solo.setWiFiData(false);
        solo.setMobileData(false);

        solo.sleep(300);

        solo.clickOnView(solo.getView(R.id.btnListChannels));
        solo.waitForActivity(ListChannelsActivity.class);
        Assert.assertTrue("Connected", solo.searchText("No Network! Please try again!"));
    }
}

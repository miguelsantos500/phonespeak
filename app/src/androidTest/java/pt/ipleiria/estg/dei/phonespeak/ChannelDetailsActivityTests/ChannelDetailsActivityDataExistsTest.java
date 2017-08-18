package pt.ipleiria.estg.dei.phonespeak.ChannelDetailsActivityTests;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ipleiria.estg.dei.phonespeak.DashBoardActivity;
import pt.ipleiria.estg.dei.phonespeak.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChannelDetailsActivityDataExistsTest {

    @Rule
    public ActivityTestRule<DashBoardActivity> mActivityTestRule = new ActivityTestRule<>(DashBoardActivity.class);

    @Test
    public void channelDetailsActivityDataExistsTest() {
        ViewInteraction appCompatButton = onView(
                allOf(ViewMatchers.withId(R.id.btnListChannels), withText("List Channels"),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction linearLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.listViewChannels),
                                withParent(withId(R.id.activity_list_public_channels))),
                        3),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.txtChannelId),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_channel_details),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.txtChannelName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_channel_details),
                                        1),
                                1),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.txtChannelDescription),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_channel_details),
                                        1),
                                2),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.txtChannelLatitude),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_channel_details),
                                        1),
                                3),
                        isDisplayed()));
        textView4.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.txtChannelLongitude),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_channel_details),
                                        1),
                                4),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.txtChannelCreatedAt),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_channel_details),
                                        1),
                                5),
                        isDisplayed()));
        textView6.check(matches(isDisplayed()));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.txtChannelElevation),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_channel_details),
                                        1),
                                6),
                        isDisplayed()));
        textView8.check(matches(isDisplayed()));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.txtChannelLastEntryId),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_channel_details),
                                        1),
                                7),
                        isDisplayed()));
        textView9.check(matches(isDisplayed()));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.txtChannelRanking),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_channel_details),
                                        1),
                                8),
                        isDisplayed()));
        textView10.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}

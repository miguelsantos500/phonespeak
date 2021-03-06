package pt.ipleiria.estg.dei.phonespeak.ListPublicChannelsActivityTests;


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
public class ListChannelsActivityFirstPageButtonTest {

    @Rule
    public ActivityTestRule<DashBoardActivity> mActivityTestRule = new ActivityTestRule<>(DashBoardActivity.class);

    @Test
    public void listPublicChannelsActivityFirstPageButtonTest() {
        ViewInteraction appCompatButton = onView(
                allOf(ViewMatchers.withId(R.id.btnListChannels), withText("List Channels"),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btnNextPage), isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.btnNextPage), isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.btnLastPage), isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withId(R.id.btnFirstPage), isDisplayed()));
        appCompatImageButton4.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.pageTextView), withText("1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_list_public_channels),
                                        2),
                                2),
                        isDisplayed()));
        textView.check(matches(withText("1")));

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

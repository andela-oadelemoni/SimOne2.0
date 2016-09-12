package ng.com.tinweb.www.simone20;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by kamiye on 06/09/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityUITest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void resetActivity() {
        mainActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivityTestRule.getActivity().recreate();
            }
        });
    }

    @Test
    public void testBottomNav_visible() {
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNav_navigationWithSwipe() {
        // Today Navigation
        onView(allOf(withId(R.id.bottom_navigation_container), withChild(withText("Today"))))
                .perform(click());

        onView(allOf(withId(R.id.bottom_navigation_item_title), withText("Today")))
                .check(matches(isDisplayed()));

        // Reminders Navigation
        onView(withChild(withId(R.id.container))).perform(swipeLeft());

        onView(allOf(withId(R.id.bottom_navigation_item_title), withText("Reminders")))
                .check(matches(isDisplayed()));

        // Groups Navigation
        onView(withChild(withId(R.id.container))).perform(swipeLeft());

        onView(allOf(withId(R.id.bottom_navigation_item_title), withText("Groups")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNav_navigationWithClick() {
        // Today Navigation
        onView(allOf(withId(R.id.bottom_navigation_container), withChild(withText("Today"))))
                .perform(click());

        onView(allOf(withId(R.id.bottom_navigation_item_title), withText("Today")))
                .check(matches(isDisplayed()));

        // Reminders Navigation
        onView(allOf(withId(R.id.bottom_navigation_container), withChild(withText("Reminders"))))
                .perform(click());

        onView(allOf(withId(R.id.bottom_navigation_item_title), withText("Reminders")))
                .check(matches(isDisplayed()));

        // Groups Navigation
        onView(allOf(withId(R.id.bottom_navigation_container), withChild(withText("Groups"))))
                .perform(click());

        onView(allOf(withId(R.id.bottom_navigation_item_title), withText("Groups")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testActivityTitle() {
        assertEquals("Today's Calls", mainActivityTestRule.getActivity().getTitle());
    }
}
package neptun.jxy1vz.hp_cluedo.ui.activity.login


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import neptun.jxy1vz.hp_cluedo.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun loginActivityTest() {
        val textInputEditText = onView(
            allOf(
                withId(R.id.txtPlayerName),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.loginRefresh),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(click())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.txtPlayerName),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.loginRefresh),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        textInputEditText2.perform(replaceText("Pedro"), closeSoftKeyboard())

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.txtPassword),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.loginRefresh),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        textInputEditText3.perform(click())

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.txtPassword),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.loginRefresh),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        textInputEditText4.perform(replaceText("alkoholxdddd"), closeSoftKeyboard())

        val appCompatButton = onView(
            allOf(
                withId(R.id.btnLogin), withText("Bejelentkezés"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.loginRefresh),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val imageView = onView(
            allOf(
                withId(R.id.ivLogo), withContentDescription("Game logo"),
                withParent(
                    allOf(
                        withId(R.id.menuRoot),
                        withParent(withId(android.R.id.content))
                    )
                ),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}

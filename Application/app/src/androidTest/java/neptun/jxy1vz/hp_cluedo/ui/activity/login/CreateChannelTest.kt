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
class CreateChannelTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun createChannelTest() {
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
        textInputEditText.perform(replaceText("Pedro"), closeSoftKeyboard())

        val textInputEditText2 = onView(
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
        textInputEditText2.perform(replaceText("alkoholxdddd"), closeSoftKeyboard())

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

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.btnStart), withText("Játék indítása"),
                childAtPosition(
                    allOf(
                        withId(R.id.menuRoot),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        val appCompatImageView = onView(
            allOf(
                withId(R.id.ivMultiPlayer),
                childAtPosition(
                    allOf(
                        withId(R.id.gameModeRoot),
                        childAtPosition(
                            withId(R.id.menuFrame),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(click())

        val appCompatImageView2 = onView(
            allOf(
                withId(R.id.ivPlayerCount3),
                childAtPosition(
                    allOf(
                        withId(R.id.gameModeRoot),
                        childAtPosition(
                            withId(R.id.menuFrame),
                            0
                        )
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        appCompatImageView2.perform(click())

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.btnSet), withText("OK"),
                childAtPosition(
                    allOf(
                        withId(R.id.gameModeRoot),
                        childAtPosition(
                            withId(R.id.menuFrame),
                            0
                        )
                    ),
                    9
                ),
                isDisplayed()
            )
        )
        appCompatButton3.perform(click())

        val appCompatImageView3 = onView(
            allOf(
                withId(R.id.ivCreate),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.menuFrame),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageView3.perform(click())

        val appCompatButton4 = onView(
            allOf(
                withId(R.id.btnNext), withText("Tovább"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.menuFrame),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatButton4.perform(click())

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.txtChannelName),
                childAtPosition(
                    allOf(
                        withId(R.id.createChannelRoot),
                        childAtPosition(
                            withId(R.id.menuFrame),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textInputEditText3.perform(replaceText("test"), closeSoftKeyboard())

        val appCompatButton5 = onView(
            allOf(
                withId(R.id.btnCreateChannel), withText("Létrehozás"),
                childAtPosition(
                    allOf(
                        withId(R.id.createChannelRoot),
                        childAtPosition(
                            withId(R.id.menuFrame),
                            1
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatButton5.perform(click())

        val imageView = onView(
            allOf(
                withId(R.id.ivLoading),
                withParent(
                    allOf(
                        withId(R.id.createChannelRoot),
                        withParent(withId(R.id.menuFrame))
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

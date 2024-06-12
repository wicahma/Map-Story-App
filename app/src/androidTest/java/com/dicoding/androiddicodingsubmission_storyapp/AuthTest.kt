package com.dicoding.androiddicodingsubmission_storyapp

import androidx.paging.ExperimentalPagingApi
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.androiddicodingsubmission_storyapp.ui.activity.MainActivity
import com.dicoding.androiddicodingsubmission_storyapp.ui.fragments.LoginFragment
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
@LargeTest
class AuthTest {

    private val mockWebServer = MockWebServer()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.baseURL = "http://127.0.0.1:8080/"
        init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        release()
    }

    @Test
    fun authLogin() {
        Thread.sleep(5000L)

        Espresso.onView(withId(R.id.ed_login_email))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("maddog@barbar.inc"))

        Espresso.onView(withId(R.id.ed_login_password))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.ed_login_password)).perform(ViewActions.typeText("Maddog123."))
        Espresso.onView(withId(R.id.button_login)).perform(ViewActions.click())

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("login_response.json"))
        mockWebServer.enqueue(mockResponse)

        Thread.sleep(6000L)
        Espresso.onView(withId(R.id.action_logout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000L)
        Espresso.onView(withId(R.id.action_logout)).perform(ViewActions.click())

    }
}
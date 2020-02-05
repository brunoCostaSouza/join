package br.com.bruno.join

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import br.com.bruno.join.view.MainActivity
import kotlinx.android.synthetic.main.home.rootFab
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)
    private lateinit var mActivity: MainActivity

    @Before
    fun setUp() {
        mActivity = activityRule.activity
    }

    @Test
    fun useAppContext() {
        mActivity.run {
            assertNotNull(rootFab)
        }
        /*
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("br.com.bruno.join", appContext.packageName)*/
    }

    @After
    fun tearDown() {

    }
}

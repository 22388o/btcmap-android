/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package com.bubelov.coins.ui.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.openDrawer
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.bubelov.coins.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapActivityTest {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MapActivity::class.java)

    @Rule
    @JvmField
    var grantLocationRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Test
    fun testToolbar() {
        onView(withId(R.id.toolbar))
            .check(matches(hasDescendant(withText("Bitcoin map"))))

        onView(withId(R.id.action_add))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.action_search))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }

    @Test
    fun testFab() {
        onView(withId(R.id.fab))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }

    @Test
    fun testNavigationDrawer() {
        onView(withId(R.id.drawer_layout))
            .check(matches(isDisplayed()))
            .check(matches(isClosed()))

        openDrawer(R.id.drawer_layout)

        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen()))
    }
}
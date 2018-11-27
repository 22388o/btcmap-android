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

package com.bubelov.coins.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.core.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_email_auth.*

class EmailAuthFragment : DaggerFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pager.adapter = TabsAdapter(childFragmentManager)
        tab_layout.setupWithViewPager(pager)
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private inner class TabsAdapter internal constructor(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {

        private val pages = listOf<Pair<Fragment, String>>(
            Pair(EmailSignInFragment(), getString(R.string.sign_in)),
            Pair(EmailSignUpFragment(), getString(R.string.sign_up))
        )

        override fun getItem(position: Int): Fragment {
            return pages[position].first!!
        }

        override fun getPageTitle(position: Int): CharSequence {
            return pages[position].second!!
        }

        override fun getCount(): Int {
            return pages.size
        }
    }
}
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
import kotlinx.android.synthetic.main.fragment_email_auth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class EmailAuthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pager.adapter = TabsAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(pager)
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    @ExperimentalCoroutinesApi
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

        override fun getCount() = pages.size
    }
}
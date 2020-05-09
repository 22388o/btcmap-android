package com.bubelov.coins.profile

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.util.CircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment(), Toolbar.OnMenuItemClickListener {

    private val model: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.apply {
            setNavigationOnClickListener { findNavController().popBackStack() }
            inflateMenu(R.menu.profile)
            setOnMenuItemClickListener(this@ProfileFragment)
        }

        lifecycleScope.launchWhenResumed {
            val user = model.getUser()

            if (user == null) {
                findNavController().popBackStack()
                return@launchWhenResumed
            }

            if (!TextUtils.isEmpty(user.avatarUrl)) {
                Picasso.get()
                    .load(user.avatarUrl)
                    .transform(CircleTransformation())
                    .into(avatar)
            } else {
                avatar.setImageResource(R.drawable.ic_no_avatar)
            }

            if (!TextUtils.isEmpty(user.firstName)) {
                userName.text = String.format("%s %s", user.firstName, user.lastName)
            } else {
                userName.text = user.email
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_sign_out) {
            signOut()
            true
        } else {
            false
        }
    }

    private fun signOut() {
        lifecycleScope.launchWhenResumed {
            model.signOut()
            findNavController().popBackStack()
        }
    }
}
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
import com.bubelov.coins.databinding.FragmentProfileBinding
import com.bubelov.coins.util.CircleTransformation
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment(), Toolbar.OnMenuItemClickListener {

    private val model: ProfileViewModel by viewModel()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.apply {
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
                    .into(binding.avatar)
            } else {
                binding.avatar.setImageResource(R.drawable.ic_no_avatar)
            }

            if (!TextUtils.isEmpty(user.firstName)) {
                binding.userName.text = String.format("%s %s", user.firstName, user.lastName)
            } else {
                binding.userName.text = user.email
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
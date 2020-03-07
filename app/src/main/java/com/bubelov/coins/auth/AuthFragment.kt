package com.bubelov.coins.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        signUp.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_emailAuthFragment)
        }

        signIn.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_emailAuthFragment)
        }
    }
}
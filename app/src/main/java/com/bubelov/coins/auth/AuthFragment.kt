package com.bubelov.coins.auth

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import kotlinx.android.synthetic.main.fragment_auth.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AuthFragment : Fragment() {

    private val model: AuthViewModel by viewModel()

    private val resultModel: AuthResultViewModel by sharedViewModel()

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

        model.showProgress.observe(viewLifecycleOwner, Observer { showProgress ->
            progress.isVisible = showProgress
            signInForm.isVisible = !showProgress
        })

        model.authorized.observe(viewLifecycleOwner, Observer { authorized ->
            if (authorized) {
                resultModel.onAuthSuccess()
                findNavController().popBackStack()
            }
        })

        model.errorMessage.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(it)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        })
    }
}
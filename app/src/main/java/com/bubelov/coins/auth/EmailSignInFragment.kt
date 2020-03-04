package com.bubelov.coins.auth

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController

import com.bubelov.coins.R
import com.bubelov.coins.util.activityViewModelProvider
import com.bubelov.coins.util.hideKeyboard
import com.bubelov.coins.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_email_sign_in.*

import javax.inject.Inject

class EmailSignInFragment : DaggerFragment(), TextView.OnEditorActionListener {
    @Inject internal lateinit var modelFactory: ViewModelProvider.Factory

    private val model by lazy {
        viewModelProvider(modelFactory) as AuthViewModel
    }

    private val resultModel by lazy {
        activityViewModelProvider(modelFactory) as AuthResultViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        password.setOnEditorActionListener(this)

        signIn.setOnClickListener {
            requireContext().hideKeyboard(it)
            signIn()
        }

        model.showProgress.observe(viewLifecycleOwner, Observer { showProgress ->
            progress.isVisible = showProgress
            signInForm.isVisible = !showProgress
        })

        model.authorized.observe(viewLifecycleOwner, Observer { authorized ->
            if (authorized == true) {
                resultModel.onAuthSuccess()

                findNavController().apply {
                    popBackStack()
                    popBackStack()
                }
            }
        })

        model.errorMessage.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(it)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        })
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            signIn()
            return true
        }

        return false
    }

    private fun signIn() {
        model.signIn(email.text.toString(), password.text.toString())
    }
}
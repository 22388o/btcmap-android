package com.bubelov.coins.auth

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.util.hideKeyboard
import kotlinx.android.synthetic.main.fragment_email_sign_up.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class EmailSignUpFragment : Fragment(), TextView.OnEditorActionListener {

    private val model: AuthViewModel by viewModel()

    private val resultModel: AuthResultViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lastName.setOnEditorActionListener(this)

        sign_up.setOnClickListener {
            requireContext().hideKeyboard(it)

            signUp(
                email.text.toString(),
                password.text.toString(),
                firstName.text.toString(),
                lastName.text.toString()
            )
        }

        model.showProgress.observe(viewLifecycleOwner, Observer { showProgress ->
            progress.isVisible = showProgress
            signUpForm.isVisible = !showProgress
        })

        model.authorized.observe(viewLifecycleOwner, Observer { authorized ->
            if (authorized) {
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
            signUp(
                email.text.toString(),
                password.text.toString(),
                firstName.text.toString(),
                lastName.text.toString()
            )

            return true
        }

        return false
    }

    private fun signUp(email: String, password: String, firstName: String, lastName: String) {
        model.signUp(email, password, firstName, lastName)
    }
}
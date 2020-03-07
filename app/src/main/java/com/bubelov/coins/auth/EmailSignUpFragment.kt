package com.bubelov.coins.auth

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.util.BasicTaskState
import com.bubelov.coins.util.hideKeyboard
import kotlinx.android.synthetic.main.fragment_email_sign_up.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
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
        lastNameInput.setOnEditorActionListener(this)

        signUpButton.setOnClickListener {
            requireContext().hideKeyboard(it)
            signUp()
        }
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            signUp()
            return true
        }

        return false
    }

    private fun signUp() {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val firstName = firstNameInput.text.toString()
        val lastName = lastNameInput.text.toString()

        model.signUp(email, password, firstName, lastName)
            .onEach { updateUI(it) }
            .launchIn(lifecycleScope)
    }

    private fun updateUI(state: BasicTaskState) {
        when (state) {
            is BasicTaskState.Progress -> {
                progress.isVisible = true
                signUpForm.isVisible = false
            }

            is BasicTaskState.Success -> {
                resultModel.onAuthSuccess()

                findNavController().apply {
                    popBackStack()
                    popBackStack()
                }
            }

            is BasicTaskState.Error -> {
                progress.isVisible = false
                signUpForm.isVisible = true

                AlertDialog.Builder(requireContext())
                    .setMessage(state.message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
    }
}
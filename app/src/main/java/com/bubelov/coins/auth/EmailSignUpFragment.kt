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
import com.bubelov.coins.databinding.FragmentEmailSignUpBinding
import com.bubelov.coins.util.BasicTaskState
import com.bubelov.coins.util.hideKeyboard
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmailSignUpFragment : Fragment(), TextView.OnEditorActionListener {

    private val model: AuthViewModel by viewModel()

    private val resultModel: AuthResultViewModel by sharedViewModel()

    private var _binding: FragmentEmailSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lastNameInput.setOnEditorActionListener(this)

        binding.signUpButton.setOnClickListener {
            requireContext().hideKeyboard(it)
            signUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            signUp()
            return true
        }

        return false
    }

    private fun signUp() {
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val firstName = binding.firstNameInput.text.toString()
        val lastName = binding.lastNameInput.text.toString()

        model.signUp(email, password, firstName, lastName)
            .onEach { updateUI(it) }
            .launchIn(lifecycleScope)
    }

    private fun updateUI(state: BasicTaskState) {
        when (state) {
            is BasicTaskState.Progress -> {
                binding.progress.isVisible = true
                binding.signUpForm.isVisible = false
            }

            is BasicTaskState.Success -> {
                resultModel.onAuthSuccess()

                findNavController().apply {
                    popBackStack()
                    popBackStack()
                }
            }

            is BasicTaskState.Error -> {
                binding.progress.isVisible = false
                binding.signUpForm.isVisible = true

                AlertDialog.Builder(requireContext())
                    .setMessage(state.message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
    }
}
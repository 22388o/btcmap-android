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
import androidx.navigation.fragment.findNavController

import com.bubelov.coins.R
import com.bubelov.coins.util.activityViewModelProvider
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

        sign_in.setOnClickListener {
            signIn()
        }

        model.showProgress.observe(this, Observer { show ->
            sign_in_panel.visibility = when (show) {
                true -> View.GONE
                else -> View.VISIBLE
            }

            progress.visibility = when (show) {
                true -> View.VISIBLE
                else -> View.GONE
            }
        })

        model.authorized.observe(this, Observer { authorized ->
            if (authorized == true) {
                resultModel.onAuthSuccess()

                findNavController().popBackStack(
                    R.id.mapFragment,
                    true
                )
            }
        })

        model.errorMessage.observe(this, Observer {
            it?.let { message ->
                AlertDialog.Builder(requireContext())
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
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
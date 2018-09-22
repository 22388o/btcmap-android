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

package com.bubelov.coins.startup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class CheckPlayServicesFragment : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val googleApiAvailability = GoogleApiAvailability.getInstance()

        val playServicesAvailability =
            googleApiAvailability.isGooglePlayServicesAvailable(requireContext())

        if (playServicesAvailability == ConnectionResult.SUCCESS) {
            onPlayServicesAvailable()
        } else {
            if (googleApiAvailability.isUserResolvableError(playServicesAvailability)) {
                val dialog = googleApiAvailability.getErrorDialog(
                    requireActivity(),
                    playServicesAvailability,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                )

                dialog.setCancelable(false)
                dialog.show()

                dialog.setOnDismissListener {
                    if (playServicesAvailability == ConnectionResult.SERVICE_INVALID) {
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                onPlayServicesAvailable()
            } else {
                requireActivity().finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onPlayServicesAvailable() {
        findNavController().navigate(R.id.action_checkPlayServicesFragment_to_mapFragment)
    }

    companion object {
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 10
    }
}
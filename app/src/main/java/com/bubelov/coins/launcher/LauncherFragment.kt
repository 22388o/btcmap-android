package com.bubelov.coins.launcher

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R

class LauncherFragment : Fragment() {
    override fun onResume() {
        super.onResume()
        Toast.makeText(requireContext(), "Launcher", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_launcherFragment_to_permissionsFragment)
    }

//   override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        val googleApiAvailability = GoogleApiAvailability.getInstance()
//
//        val playServicesAvailability =
//            googleApiAvailability.isGooglePlayServicesAvailable(requireContext())
//
//        if (playServicesAvailability == ConnectionResult.SUCCESS) {
//            onPlayServicesAvailable()
//        } else {
//            if (googleApiAvailability.isUserResolvableError(playServicesAvailability)) {
//                val dialog = googleApiAvailability.getErrorDialog(
//                    requireActivity(),
//                    playServicesAvailability,
//                    PLAY_SERVICES_RESOLUTION_REQUEST
//                )
//
//                dialog.setCancelable(false)
//                dialog.show()
//
//                dialog.setOnDismissListener {
//                    if (playServicesAvailability == ConnectionResult.SERVICE_INVALID) {
//                        requireActivity().finish()
//                    }
//                }
//            }
//        }
//    }

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
        findNavController().navigate(R.id.action_launcherFragment_to_permissionsFragment)
    }

    companion object {
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 10
    }
}
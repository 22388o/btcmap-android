package com.bubelov.coins.launcher

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import org.koin.android.viewmodel.ext.android.viewModel

class LauncherFragment : Fragment() {

    private val model: LauncherViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (model.permissionsExplained) {
            findNavController().navigate(R.id.action_launcherFragment_to_mapFragment)
        } else {
            findNavController().navigate(R.id.action_launcherFragment_to_permissionsFragment)
        }
    }
}
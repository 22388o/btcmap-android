package com.bubelov.coins.launcher

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.koin.android.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class LauncherFragment : Fragment() {

    private val model: LauncherViewModel by viewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            if (model.getPermissionsExplained().first()) {
                findNavController().navigate(R.id.action_launcherFragment_to_mapFragment)
            } else {
                findNavController().navigate(R.id.action_launcherFragment_to_permissionsFragment)
            }
        }
    }
}
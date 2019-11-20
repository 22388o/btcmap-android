package com.bubelov.coins.launcher

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class LauncherFragment : Fragment() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    private val model by lazy {
        ViewModelProvider(this, modelFactory)[LauncherViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)

        if (model.permissionsExplained) {
            findNavController().navigate(R.id.action_launcherFragment_to_mapFragment)
        } else {
            findNavController().navigate(R.id.action_launcherFragment_to_permissionsFragment)
        }
    }
}
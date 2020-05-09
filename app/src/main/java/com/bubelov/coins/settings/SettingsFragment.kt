package com.bubelov.coins.settings

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SettingsFragment : Fragment() {

    private val model: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        distanceUnitsButton.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                val labels = resources.getStringArray(R.array.distance_units)
                val values = resources.getStringArray(R.array.distance_units_values)

                val selectedUnits = model.getDistanceUnits().first()
                val selectedValueIndex = values.indexOf(selectedUnits)

                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.pref_distance_units)
                    .setSingleChoiceItems(labels, selectedValueIndex) { dialog, index ->
                        lifecycleScope.launch {
                            model.setDistanceUnits(values[index])
                            dialog.dismiss()
                        }
                    }
                    .show()
            }
        }

        lifecycleScope.launchWhenResumed {
            model.getDistanceUnits().map {
                if (it.isBlank()) {
                    resources.getString(R.string.pref_distance_units_automatic)
                } else {
                    it
                }
            }.collect {
                val labels = resources.getStringArray(R.array.distance_units)
                val values = resources.getStringArray(R.array.distance_units_values)
                distanceUnits.text = labels[values.indexOf(it)]
            }
        }

        syncDatabase.setOnClickListener {
            lifecycleScope.launch {
                model.syncDatabase()
            }
        }

        showSyncLog.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_logsFragment)
        }

//        model.syncLogs.observe(viewLifecycleOwner, Observer {
//            it?.let { logs ->
//                AlertDialog.Builder(requireContext())
//                    .setItems(logs.toTypedArray(), null)
//                    .show()
//            }
//        })

        testNotification.setOnClickListener {
            lifecycleScope.launch {
                model.testNotification()
            }
        }
    }
}
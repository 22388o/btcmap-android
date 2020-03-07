package com.bubelov.coins.settings

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.viewmodel.ext.android.viewModel

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
            val labels = resources.getStringArray(R.array.distance_units)
            val values = resources.getStringArray(R.array.distance_units_values)

            val selectedUnits = model.distanceUnits.value ?: return@setOnClickListener
            val selectedValueIndex = values.indexOf(selectedUnits)

            AlertDialog.Builder(requireContext())
                .setTitle(R.string.pref_distance_units)
                .setSingleChoiceItems(labels, selectedValueIndex) { dialog, index ->
                    model.distanceUnits.setValue(values[index])
                    dialog.dismiss()
                }
                .show()
        }

        model.distanceUnits.observe(viewLifecycleOwner, Observer {
            val labels = resources.getStringArray(R.array.distance_units)
            val values = resources.getStringArray(R.array.distance_units_values)
            distanceUnits.text = labels[values.indexOf(it)]
        })

        syncDatabase.setOnClickListener { model.syncDatabase() }

        showSyncLog.setOnClickListener {
            model.showSyncLogs()
        }

        model.syncLogs.observe(viewLifecycleOwner, Observer {
            it?.let { logs ->
                AlertDialog.Builder(requireContext())
                    .setItems(logs.toTypedArray(), null)
                    .show()
            }
        })

        testNotification.setOnClickListener { model.testNotification() }
    }
}
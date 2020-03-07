package com.bubelov.coins.permissions

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import kotlinx.android.synthetic.main.fragment_permissions.*
import org.koin.android.viewmodel.ext.android.viewModel

class PermissionsFragment : Fragment() {

    private val model: PermissionsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_permissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        continueToMap.setOnClickListener {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSIONS_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        model.permissionsExplained = true
        findNavController().navigate(R.id.action_permissionsFragment_to_mapFragment)
    }

    companion object {
        private const val PERMISSIONS_REQUEST = 10
    }
}
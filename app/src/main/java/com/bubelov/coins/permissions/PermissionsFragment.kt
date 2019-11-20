package com.bubelov.coins.permissions

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_permissions.*
import javax.inject.Inject

class PermissionsFragment : Fragment() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    private val model by lazy {
        ViewModelProvider(this, modelFactory)[PermissionsViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_permissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)

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
package com.bubelov.coins.support

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.util.openUrl
import kotlinx.android.synthetic.main.fragment_support_project.*

class SupportProjectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_support_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        openGitHub.setOnClickListener { requireContext().openUrl(getString(R.string.repository_url)) }
        address.setOnClickListener { copyDonationAddressToClipboard() }
        copy.setOnClickListener { copyDonationAddressToClipboard() }
    }

    private fun copyDonationAddressToClipboard() {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboard.setPrimaryClip(
            ClipData.newPlainText(
                getString(R.string.bitcoin_map_donation_address),
                getString(R.string.donation_wallet)
            )
        )

        Toast.makeText(
            requireContext(),
            getString(R.string.address_have_been_copied),
            Toast.LENGTH_SHORT
        ).show()
    }
}
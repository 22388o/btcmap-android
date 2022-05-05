package support

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
import com.bubelov.coins.databinding.FragmentSupportProjectBinding
import etc.openUrl

class SupportProjectFragment : Fragment() {

    private var _binding: FragmentSupportProjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSupportProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.openGitHub.setOnClickListener { requireContext().openUrl(getString(R.string.repository_url)) }
        binding.address.setOnClickListener { copyDonationAddressToClipboard() }
        binding.copy.setOnClickListener { copyDonationAddressToClipboard() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
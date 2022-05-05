package search

import androidx.lifecycle.Observer
import android.os.Bundle
import android.text.Editable
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.databinding.FragmentPlacesSearchBinding
import model.Location
import etc.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlacesSearchFragment : Fragment() {

    private val model: PlacesSearchViewModel by viewModel()

    private val resultModel: PlacesSearchResultViewModel by sharedViewModel()

    private var _binding: FragmentPlacesSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlacesSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args = PlacesSearchFragmentArgs.fromBundle(requireArguments())
        model.setUp(Location(args.lat.toDouble(), args.lon.toDouble()))

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.list.layoutManager = LinearLayoutManager(requireContext())

        val adapter = PlacesSearchResultsAdapter {
            resultModel.pickPlace(it.placeId)
            findNavController().popBackStack()
        }

        binding.list.adapter = adapter

        model.rows.observe(viewLifecycleOwner, Observer { rows ->
            if (rows != null) {
                adapter.swapItems(rows)
            }
        })

        binding.query.setOnFocusChangeListener { query, hasFocus ->
            if (hasFocus) {
                requireContext().showKeyboard(query)
            } else {
                requireContext().hideKeyboard(query)
            }
        }

        binding.query.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                model.setQuery(s.toString())
                binding.clear.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
            }
        })

        binding.query.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> requireContext().hideKeyboard(binding.query)
            }

            true
        }

        binding.clear.setOnClickListener { binding.query.setText("") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract class TextWatcherAdapter : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }
}
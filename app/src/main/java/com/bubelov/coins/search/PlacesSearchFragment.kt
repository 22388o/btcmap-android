package com.bubelov.coins.search

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.util.*
import kotlinx.android.synthetic.main.fragment_places_search.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.time.ExperimentalTime

@ExperimentalTime
class PlacesSearchFragment : Fragment() {

    private val model: PlacesSearchViewModel by viewModel()

    private val resultModel: PlacesSearchResultViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_places_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args = PlacesSearchFragmentArgs.fromBundle(arguments!!)
        model.setUp(args.location)

        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        list.layoutManager = LinearLayoutManager(requireContext())

        val adapter = PlacesSearchResultsAdapter {
            resultModel.pickPlace(it.placeId)
            findNavController().popBackStack()
        }

        list.adapter = adapter

        model.rows.observe(viewLifecycleOwner, Observer { rows ->
            if (rows != null) {
                adapter.swapItems(rows)
            }
        })

        query.setOnFocusChangeListener { query, hasFocus ->
            if (hasFocus) {
                requireContext().showKeyboard(query)
            } else {
                requireContext().hideKeyboard(query)
            }
        }

        query.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                model.setQuery(s.toString())
                clear.visibility = if (TextUtils.isEmpty(s)) View.GONE else View.VISIBLE
            }
        })

        query.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> requireContext().hideKeyboard(query)
            }

            true
        }

        clear.setOnClickListener { query.setText("") }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.search_status_bar)
        }
    }

    override fun onPause() {
        requireActivity().window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark)
        }

        super.onPause()
    }
}
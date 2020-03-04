package com.bubelov.coins.search

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.util.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_places_search.*
import javax.inject.Inject

class PlacesSearchFragment : DaggerFragment() {
    @Inject lateinit var modelFactory: ViewModelProvider.Factory

    private val model by lazy {
        (viewModelProvider(modelFactory) as PlacesSearchViewModel).apply {
            val args = PlacesSearchFragmentArgs.fromBundle(arguments!!)
            setUp(args.location)
        }
    }

    private val resultModel by lazy {
        activityViewModelProvider(modelFactory) as PlacesSearchResultViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_places_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.search_status_bar)
    }

    override fun onPause() {
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_dark)

        super.onPause()
    }
}
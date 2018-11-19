/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package com.bubelov.coins.search

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
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
import com.bubelov.coins.util.TextWatcherAdapter
import com.bubelov.coins.util.activityViewModelProvider
import com.bubelov.coins.util.hideKeyboard
import com.bubelov.coins.util.showKeyboard
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_places_search.*
import javax.inject.Inject

class PlacesSearchFragment : DaggerFragment() {
    @Inject lateinit var modelFactory: ViewModelProvider.Factory

    private val model by lazy {
        ViewModelProviders.of(this, modelFactory).get(PlacesSearchViewModel::class.java).apply {
            val args = PlacesSearchFragmentArgs.fromBundle(arguments)
            setUp(args.location)
        }
    }

    private val resultsModel by lazy {
        activityViewModelProvider(modelFactory) as PlacesSearchResultsViewModel
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

        list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        val adapter = PlacesSearchResultsAdapter {
            resultsModel.pickedPlaceId.value = it.placeId
            findNavController().popBackStack()
        }

        list.adapter = adapter

        model.rows.observe(this, Observer { rows ->
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
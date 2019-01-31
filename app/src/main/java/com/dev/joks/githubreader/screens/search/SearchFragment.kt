package com.dev.joks.githubreader.screens.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dev.joks.githubreader.R
import com.dev.joks.githubreader.databinding.FragmentSearchBinding
import com.dev.joks.githubreader.screens.MainActivity
import com.dev.joks.githubreader.screens.base.BaseFragment
import com.dev.joks.githubreader.screens.details.USERNAME
import com.dev.joks.githubreader.screens.search.adapter.UsersAdapter
import com.dev.joks.githubreader.utils.*
import kotlinx.android.synthetic.main.fragment_search.*
import org.jetbrains.anko.support.v4.toast

class SearchFragment : BaseFragment() {

    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var searchViewModel: SearchViewModel
    private var isLoading = false
    private var nextPageUrl: String? = null

    override fun getLayoutRes(): Int = R.layout.fragment_search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        usersAdapter = UsersAdapter(context, mutableListOf())

        searchViewModel.response.observe(this, Observer {
            if (it?.users != null && it.users.isNotEmpty()) {

                usersAdapter.addItems(it.users)
                nextPageUrl = it.nextPageUrl

                hideKeyboard()
//                etSearch.setText("")
            } else {
                toast(R.string.not_found)
            }

            isLoading = false
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        return searchBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(false)
        val linearLayoutManager = LinearLayoutManager(context)
        rvUsers.apply {
            layoutManager = linearLayoutManager
            adapter = usersAdapter
        }

        usersAdapter.onItemClick = { user ->
            val bundle = Bundle()
            bundle.putString(USERNAME, user.login)
            findNavController().navigate(R.id.userDetailsFragment, bundle)
        }

        btnSearch.onClick {
            activity?.doOnline(online = {
                val isSearchTextValid = validate(etSearch, searchError)

                if (isSearchTextValid) {
                    isLoading = true
                    searchViewModel.searchUsers(etSearch.text.toString())
                    usersAdapter.clear()
                }
            })
        }

        rvUsers.itemAnimator = DefaultItemAnimator()
        rvUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()

                if (nextPageUrl != null) {
                    if (visibleItemCount + firstVisibleItem >= totalItemCount - 3 && !isLoading) {
                        activity?.doOnline(online = {
                            isLoading = true
                            searchViewModel.searchUsersPagination(nextPageUrl)
                        })
                    }
                }
            }
        })

        searchViewModel.error.observe(this, Observer {
            toast(it.toString())
            isLoading = false
        })

        etSearch.addTextChangeListener(searchError)
        searchBinding.viewModel = searchViewModel
    }
}

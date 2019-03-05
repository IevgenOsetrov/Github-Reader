package com.dev.joks.githubreader.screens.details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev.joks.githubreader.R
import com.dev.joks.githubreader.databinding.FragmentUserDetailsBinding
import com.dev.joks.githubreader.screens.MainActivity
import com.dev.joks.githubreader.screens.base.BaseFragment
import com.dev.joks.githubreader.screens.details.adapter.RepoAdapter
import com.dev.joks.githubreader.utils.doOnline
import kotlinx.android.synthetic.main.fragment_user_details.*
import org.jetbrains.anko.support.v4.toast


const val USERNAME = "username"

class UserDetailsFragment : BaseFragment() {

    private lateinit var userDetailsBinding: FragmentUserDetailsBinding
    private var reposAdapter: RepoAdapter? = null
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private var isLoading = false
    private var nextPageUrl: String? = null

    override fun getLayoutRes(): Int = R.layout.fragment_user_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
        userDetailsViewModel = ViewModelProviders.of(this).get(UserDetailsViewModel::class.java)
        reposAdapter = RepoAdapter(context, mutableListOf())
        activity?.doOnline(online = {
            isLoading = true
            userDetailsViewModel.getUserData(arguments?.getString(USERNAME))
        })

        userDetailsViewModel.paginationResponse.observe(this, Observer {
            it?.repos?.let { repos -> reposAdapter?.addItems(repos) }
            nextPageUrl = it?.nextPageUrl
            isLoading = false
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userDetailsBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        return userDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
        val itemDecor = DividerItemDecoration(context, VERTICAL)
        val linearLayoutManager = LinearLayoutManager(context)
        rvRepositories.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(itemDecor)
            adapter = reposAdapter
        }

        userDetailsViewModel.response.observe(this, Observer {

            if (it?.first?.name != null && it.first.company != null) {
                tvUserName.text = (it.first.name).plus(", ").plus(it.first.company)
            } else if (it?.first?.name != null) {
                tvUserName.text = it.first.name
            } else {
                tvUserName.text = it?.first?.company
            }

            tvFollowers.text = it?.first?.followers.toString()
            tvFollowing.text = it?.first?.following.toString()

            context?.let { context ->
                Glide.with(context)
                    .load(it?.first?.avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivAvatar)
            }

            tvRepositories.text = context?.getString(R.string.repositories, it?.first?.reposNumber)
            reposAdapter?.clear()
            it?.second?.repos?.let { repos -> reposAdapter?.addItems(repos) }
            nextPageUrl = it?.second?.nextPageUrl
            isLoading = false
        })

        rvRepositories.itemAnimator = DefaultItemAnimator()
        rvRepositories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()

                if (nextPageUrl != null) {
                    if (visibleItemCount + firstVisibleItem >= totalItemCount - 3 && !isLoading) {
                        activity?.doOnline(online = {
                            isLoading = true
                            userDetailsViewModel.getUserReposPagination(nextPageUrl)
                        })
                    }
                }
            }
        })

        userDetailsViewModel.error.observe(this, Observer {
            toast(it.toString())
            isLoading = false
        })

        userDetailsBinding.viewModel = userDetailsViewModel
    }
}

package com.dev.joks.githubreader.screens.search

import android.arch.lifecycle.MutableLiveData
import android.util.Patterns
import android.view.View
import com.dev.joks.githubreader.BuildConfig
import com.dev.joks.githubreader.screens.base.BaseViewModel
import com.dev.joks.githubreader.service.AppApi
import com.dev.joks.githubreader.service.model.UsersResponse
import com.google.gson.Gson
import com.wajahatkarim3.easyvalidation.core.view_ktx.contains
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response


class SearchViewModel : BaseViewModel() {

    val response: MutableLiveData<UsersResponse> = MutableLiveData()

    fun searchUsers(query: String?) {
        getCompositeDisposable().add(AppApi.apiService.searchUsers(
            BuildConfig.BASE_URL.plus("search/users?q=").plus(query).plus("+in:login&type=user")
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onSearchUsersStart() }
            .subscribe(
                { response -> onSearchUsersSuccess(response) },
                { error -> showError(error) }
            ))
    }

    fun searchUsersPagination(url: String?) {
        getCompositeDisposable().add(AppApi.apiService.searchUsers(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onSearchUsersStart() }
            .subscribe(
                { response -> onSearchUsersSuccess(response) },
                { error -> showError(error) }
            ))
    }

    private fun onSearchUsersStart() {
        progressVisibility.value = View.VISIBLE
    }

    private fun onSearchUsersSuccess(body: Response<ResponseBody>) {
        progressVisibility.value = View.GONE
        val usersResponse = Gson().fromJson(body.body()?.string(), UsersResponse::class.java)
        if (usersResponse.users.isNotEmpty()) {
            var linkHeader: String? = null
            if (body.raw().headers().names().contains("Link")) {
                linkHeader = body.raw().headers().values("Link")[0]
            }

            var nextPageUrl: String? = null

            //Get next page url from header info if it is exists
            if (linkHeader?.contains("next") == true) {
                val urls = extractUrls(linkHeader)
                if (urls.size == 2) {
                    nextPageUrl = urls[0]
                } else if (urls.size == 4) {
                    nextPageUrl = urls[1]
                }
            }

            usersResponse.nextPageUrl = nextPageUrl
        }

        response.value = usersResponse
    }
}
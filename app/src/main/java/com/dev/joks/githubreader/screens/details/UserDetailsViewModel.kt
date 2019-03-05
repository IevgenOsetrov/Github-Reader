package com.dev.joks.githubreader.screens.details

import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.dev.joks.githubreader.screens.base.BaseViewModel
import com.dev.joks.githubreader.service.AppApi
import com.dev.joks.githubreader.service.model.RepositoriesResponse
import com.dev.joks.githubreader.service.model.Repository
import com.dev.joks.githubreader.service.model.UserInfo
import com.google.gson.Gson
import com.wajahatkarim3.easyvalidation.core.view_ktx.contains
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class UserDetailsViewModel : BaseViewModel() {

    val response: MutableLiveData<Pair<UserInfo, RepositoriesResponse>> = MutableLiveData()
    val paginationResponse: MutableLiveData<RepositoriesResponse> = MutableLiveData()

    fun getUserData(username: String?) {
        getCompositeDisposable().add(Single.zip(
            AppApi.apiService.getUserInfo(username),
            AppApi.apiService.getUserRepositories(username),
            BiFunction { userInfo: UserInfo, repos: Response<ResponseBody> ->
                return@BiFunction Pair(userInfo, repos)
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onGetUserDataStart() }
            .subscribe(
                { response -> onGetUserDataSuccess(response) },
                { error -> showError(error) }
            ))
    }

    fun getUserReposPagination(url: String?) {
        getCompositeDisposable().add(AppApi.apiService.getUsersRepos(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onGetUserDataStart() }
            .subscribe(
                { response -> onGetUserReposPaginationSuccess(response) },
                { error -> showError(error) }
            ))
    }

    private fun onGetUserDataStart() {
        progressVisibility.value = View.VISIBLE
    }

    private fun onGetUserDataSuccess(data: Pair<UserInfo, Response<ResponseBody>>) {
        progressVisibility.value = View.GONE
        response.value = Pair(data.first, getDataFromBody(data.second))
    }

    private fun onGetUserReposPaginationSuccess(body: Response<ResponseBody>) {
        progressVisibility.value = View.GONE
        paginationResponse.value = getDataFromBody(body)
    }

    private fun getDataFromBody(body: Response<ResponseBody>): RepositoriesResponse {
        val repositoriesResponse = RepositoriesResponse()
        val repos = Gson().fromJson(body.body()?.string(), Array<Repository>::class.java)
        repositoriesResponse.repos = repos.toList()
        if (repos.isNotEmpty()) {
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

            repositoriesResponse.nextPageUrl = nextPageUrl
        }

        return repositoriesResponse
    }
}
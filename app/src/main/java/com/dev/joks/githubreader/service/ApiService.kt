package com.dev.joks.githubreader.service

import com.dev.joks.githubreader.service.model.Repository
import com.dev.joks.githubreader.service.model.UserInfo
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {

    @GET("users/{login}")
    fun getUserInfo(
        @Path("login") login: String?
    ): Observable<UserInfo>

    @GET("users/{login}/repos")
    fun getUserRepositories(
        @Path("login") login: String?
    ): Observable<Response<ResponseBody>>

    @GET
    fun searchUsers(@Url url: String?): Observable<Response<ResponseBody>>

    @GET
    fun getUsersRepos(@Url url: String?): Observable<Response<ResponseBody>>
}
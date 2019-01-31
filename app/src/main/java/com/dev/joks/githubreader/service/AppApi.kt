package com.dev.joks.githubreader.service

import com.dev.joks.githubreader.BuildConfig
import com.dev.joks.githubreader.GithubReaderApp
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

object AppApi {
    private const val CACHE_SIZE = 10 * 1024 * 1024L
    private const val CONNECTION_TIMEOUT = 20L
    private const val READ_TIMEOUT = 60L

    lateinit var apiService: ApiService
    lateinit var retrofit: Retrofit

    fun init() {
        val cache = Cache(File(GithubReaderApp.instance.cacheDir, "http"), CACHE_SIZE)

        retrofit = Retrofit.Builder()
            .client(OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor {
                    it.proceed(
                        it.request().newBuilder()
                            .addHeader("User-Agent", System.getProperty("http.agent"))
                            .addHeader("Accept-Charset", "UTF-8")
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-type", "application/json")
                            .addHeader("Accept-Language", Locale.getDefault().language)
                            .build()
                    )
                }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    if (BuildConfig.DEBUG) {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                })
                .build()
            )
            .baseUrl(BuildConfig.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }
}
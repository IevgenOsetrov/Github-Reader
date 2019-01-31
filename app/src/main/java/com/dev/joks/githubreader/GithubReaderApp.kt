package com.dev.joks.githubreader

import android.app.Application
import com.dev.joks.githubreader.service.AppApi

class GithubReaderApp : Application() {

    companion object {
        lateinit var instance: GithubReaderApp
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        AppApi.init()
    }
}
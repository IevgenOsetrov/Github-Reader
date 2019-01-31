package com.dev.joks.githubreader.screens.base

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Patterns
import android.view.View
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {

    val progressVisibility: MutableLiveData<Int> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun getCompositeDisposable(): CompositeDisposable {
        return compositeDisposable
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun showError(throwable: Throwable) {
        progressVisibility.value = View.GONE
    }

    fun extractUrls(text: String?): MutableList<String> {
        val urls = mutableListOf<String>()
        val m = Patterns.WEB_URL.matcher(text)
        while (m.find()) {
            val url = m.group()
            urls.add(url)
        }

        return urls
    }
}
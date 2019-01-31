package com.dev.joks.githubreader.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.support.v4.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.dev.joks.githubreader.R
import org.jetbrains.anko.toast

const val TAG = "Utils"

inline fun Context.doOnline(
    online: () -> Unit, offline: () -> Unit = {
        toast(getString(R.string.error_check_internet_connection))
    }
) {
    if (isNetworkAvailable(this)) {
        online()
    } else {
        offline()
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val activeNetworkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager).activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

inline fun <T : View> T.onClick(crossinline action: (T) -> Unit) {
    this.setOnClickListener {
        action(this)
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.toggleVisibility() {
    if (isVisible) {
        gone()
    } else {
        visible()
    }
}

val View.isVisible: Boolean
    get() = visibility == View.VISIBLE

fun Fragment.hideKeyboard() {
    this.activity?.currentFocus?.let {
        (this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
            hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}

fun Activity.hideKeyboard() {
    this.currentFocus?.let {
        (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
            hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}

fun Fragment.showKeyboard(view: View) {
    (this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
        showSoftInput(view, 0)
    }
}

fun Activity.showKeyboard() {
    this.currentFocus?.let {
        (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
            showSoftInput(it, 0)
        }
    }
}

fun Activity.clearFocus() {
    val currentFocus = this.currentFocus
    currentFocus?.clearFocus()
}

fun Fragment.clearFocus() {
    val currentFocus = activity?.currentFocus
    currentFocus?.clearFocus()
}

fun Double.formatToPlaces(places: Int): String {
    return String.format("%.${places}f", this)
}
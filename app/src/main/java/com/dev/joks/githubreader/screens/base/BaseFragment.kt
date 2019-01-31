package com.dev.joks.githubreader.screens.base

import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.View

abstract class BaseFragment : Fragment() {

    var root: View? = null
    val TAG = BaseFragment::class.java.simpleName

    @LayoutRes
    abstract fun getLayoutRes(): Int
}
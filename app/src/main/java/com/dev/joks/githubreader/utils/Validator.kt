package com.dev.joks.githubreader.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator

fun validate(et: EditText, tvError: TextView): Boolean {
    return et.validator()
        .nonEmpty()
        .addErrorCallback {
            tvError.visible()
            tvError.text = it
        }
        .addSuccessCallback {
            tvError.gone()
        }.check()
}

fun EditText.addTextChangeListener(tvError: TextView) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            tvError.gone()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}
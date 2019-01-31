package com.dev.joks.githubreader.service.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @SerializedName("login")
    val login: String,
    @SerializedName("avatar_url")
    val avatar: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("score")
    val score: Double
) : Parcelable
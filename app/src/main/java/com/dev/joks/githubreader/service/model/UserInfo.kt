package com.dev.joks.githubreader.service.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
    @SerializedName("name")
    val name: String?,
    @SerializedName("company")
    val company: String?,
    @SerializedName("followers")
    val followers: Int,
    @SerializedName("following")
    val following: Int,
    @SerializedName("public_repos")
    val reposNumber: Int,
    @SerializedName("avatar_url")
    val avatar: String?,
    @SerializedName("location")
    val location: String?
) : Parcelable
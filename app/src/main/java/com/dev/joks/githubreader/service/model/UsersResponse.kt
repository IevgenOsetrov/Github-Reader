package com.dev.joks.githubreader.service.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UsersResponse(
    @SerializedName("total_count")
    val count: Int,
    @SerializedName("items")
    val users: List<User>,
    var nextPageUrl: String?
) : Parcelable
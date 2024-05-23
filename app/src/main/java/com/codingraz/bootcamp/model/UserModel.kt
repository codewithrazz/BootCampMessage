package com.codingraz.bootcamp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    val userId: String,
    val userName: String,
    val userImage: String,
    var lastMessage: String
): Parcelable

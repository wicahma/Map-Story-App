package com.dicoding.androiddicodingsubmission_storyapp.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import java.util.Date
import java.util.Locale
import java.util.TimeZone


fun CharSequence.validateEmail(): Boolean {
    val s = this
    val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
    return !emailRegex.matches(s.toString())
}

fun CharSequence.validatePassword(): Boolean {
    val s = this
    val isError = s.length <= 8
    return isError
}

fun NavController.safeNavigate(direction: NavDirections) {
    Log.d("clickTag", "Click happened")
    currentDestination?.getAction(direction.actionId)?.run {
        Log.d("clickTag", "Click Propagated")
        navigate(direction)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun String.relativeDateFormatter(): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val parsedDate = sdf.parse(this)!!

    val currentDate = Date()

    val durationInMillis = currentDate.time - parsedDate.time
    val durationInSeconds = durationInMillis / 1000

    val relativeTime = when {
        durationInSeconds < 60 -> "$durationInSeconds seconds ago"
        durationInSeconds < 3600 -> "${durationInSeconds / 60} minutes ago"
        durationInSeconds < 86400 -> "${durationInSeconds / 3600} hours ago"
        else -> "${durationInSeconds / 86400} days ago"
    }
    return relativeTime
}
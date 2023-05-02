package ru.sergean.nasaapp.utils

import android.content.Context
import android.os.Build
import ru.sergean.nasaapp.R

fun Context.createReportText(formattedNumber: String, hasVpn: Boolean) = buildString {
    val divider: StringBuilder.() -> StringBuilder = {
        append(':').append(' ')
    }
    val space: StringBuilder.() -> StringBuilder = {
        append('\n').append('\n')
    }

    append(getString(R.string.my_phone_number)).divider().append(formattedNumber).space()
    append(getString(R.string.unable_to_get_verification_code)).space()
    append("Device").divider().append(Build.BRAND).append(' ').append(Build.DEVICE).append('\n')
    append("OS Version").divider().append(Build.VERSION.SDK_INT).append('\n')
    append("Vpn").divider().append(hasVpn)
}


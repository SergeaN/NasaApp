package ru.sergean.nasaapp.utils

import java.text.SimpleDateFormat
import java.util.*

fun String.formatted(): String {
    //2023-03-07T10:10:44Z
    val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val output = SimpleDateFormat("HH:mm dd.MM.yyyy")

    return try {
        val date = input.parse(this) ?: Calendar.getInstance().time
        output.format(date)
    } catch (e: java.lang.Exception) {
        ""
    }
}
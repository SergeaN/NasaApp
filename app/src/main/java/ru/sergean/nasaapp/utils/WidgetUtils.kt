package ru.sergean.nasaapp.utils

import android.widget.EditText
import androidx.viewpager2.widget.ViewPager2
import java.util.regex.Pattern

fun ViewPager2.onPageSelected(block: (Int) -> Unit) {
    registerOnPageChangeCallback(
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                block(position)
                super.onPageSelected(position)
            }
        }
    )
}

fun String.isMatch(regex: String): Boolean {
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

val EditText.value: String
    get() = text.toString()

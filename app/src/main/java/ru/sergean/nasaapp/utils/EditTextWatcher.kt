package ru.sergean.nasaapp.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditTextWatcher(
    private val scope: CoroutineScope,
    private val delayMillis: Long = 1000
) {

    private var currentText = ""

    fun watch(text: CharSequence?, onTextChanged: (String) -> Unit) {
        val string = text.toString()
        if (currentText != string) {
            currentText = string
            scope.launch {
                delay(timeMillis = delayMillis)
                if (currentText != string) {
                    return@launch
                }
                onTextChanged(currentText)
            }
        }
    }
}
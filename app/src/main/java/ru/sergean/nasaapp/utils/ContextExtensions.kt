package ru.sergean.nasaapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty

inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}


inline fun <reified T : Parcelable> parcelableArgs(key: String): ReadOnlyProperty<Fragment, T> {
    return ReadOnlyProperty { thisRef, _ ->
        val args = thisRef.requireArguments()
        require(args.containsKey(key)) { "Arguments don't contain key '$key'" }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireNotNull(args.getParcelable(key, T::class.java))
        } else {
            requireNotNull(args.getParcelable(key) as? T)
        }
    }
}

inline fun <reified T : Serializable> serializableArgs(key: String): ReadOnlyProperty<Fragment, T> {
    return ReadOnlyProperty { thisRef, _ ->
        val args = thisRef.requireArguments()
        require(args.containsKey(key)) { "Arguments don't contain key '$key'" }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireNotNull(args.getSerializable(key, T::class.java))
        } else {
            requireNotNull(args.getSerializable(key) as? T)
        }
    }
}

fun <T : Serializable> serializableArgsNonInline(key: String): ReadOnlyProperty<Fragment, T> {
    return ReadOnlyProperty { thisRef, _ ->
        val args = thisRef.requireArguments()
        require(args.containsKey(key)) { "Arguments don't contain key '$key'" }
        requireNotNull(args.getSerializable(key) as? T)
    }
}

fun stringArgs(key: String): ReadOnlyProperty<Fragment, String> {
    return ReadOnlyProperty { thisRef, _ ->
        val args = thisRef.requireArguments()
        require(args.containsKey(key)) { "Arguments don't contain key '$key'" }
        requireNotNull(args.getString(key))
    }
}


fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(@StringRes stringId: Int) {
    Toast.makeText(requireContext(), stringId, Toast.LENGTH_LONG).show()
}

fun Fragment.showSnackbar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
}

fun Fragment.showSnackbar(@StringRes stringId: Int) {
    Snackbar.make(requireView(), stringId, Snackbar.LENGTH_LONG).show()
}
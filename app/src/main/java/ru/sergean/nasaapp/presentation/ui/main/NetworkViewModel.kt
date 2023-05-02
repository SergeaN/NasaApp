package ru.sergean.nasaapp.presentation.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.sergean.nasaapp.data.network.NetworkConnectionManager
import ru.sergean.nasaapp.data.network.NetworkConnectionManagerImpl

class NetworkViewModel(application: Application) : AndroidViewModel(application) {

    private val connectivityManager: NetworkConnectionManager

    init {
        connectivityManager = NetworkConnectionManagerImpl(viewModelScope, getApplication())
        connectivityManager.startListenNetworkState()
    }

    val connectionState = connectivityManager.networkConnectionState

    val vpnState = connectivityManager.vpnConnectionState

    override fun onCleared() {
        super.onCleared()
        connectivityManager.stopListenNetworkState()
    }
}
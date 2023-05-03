package ru.sergean.nasaapp.data.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkConnectionManager {
    val networkConnectionState: StateFlow<Boolean>
    val vpnConnectionState: StateFlow<Boolean>

    fun startListenNetworkState()
    fun stopListenNetworkState()
}

val NetworkConnectionManager.isNetworkConnected: Boolean
    get() = networkConnectionState.value

val NetworkConnectionManager.hasVpn: Boolean
    get() = networkConnectionState.value

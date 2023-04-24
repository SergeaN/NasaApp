package ru.sergean.nasaapp.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.flow.*
import ru.sergean.nasaapp.di.ApplicationContext
import javax.inject.Inject

interface NetworkConnectionManager {
    val networkConnectionState: StateFlow<Boolean>

    fun startListenNetworkState()

    fun stopListenNetworkState()
}

val NetworkConnectionManager.isNetworkConnected: Boolean
    get() = networkConnectionState.value

class NetworkConnectionManagerImpl @Inject constructor(
    @ApplicationContext context: Context
) : NetworkConnectionManager {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = NetworkCallback()

    @Volatile
    private var networkIsListening = false

    private val _networkConnectionState = MutableStateFlow(value = false)

    override val networkConnectionState: StateFlow<Boolean> =
        _networkConnectionState.asStateFlow()

    override fun startListenNetworkState() {
        if (networkIsListening) return

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        networkIsListening = true
    }

    override fun stopListenNetworkState() {
        if (!networkIsListening) return

        connectivityManager.unregisterNetworkCallback(networkCallback)
        networkIsListening = false
    }

    private inner class NetworkCallback : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _networkConnectionState.update { true }
        }

        override fun onLost(network: Network) {
            _networkConnectionState.update { false }
        }

        override fun onUnavailable() {
            _networkConnectionState.update { false }
        }
    }
}
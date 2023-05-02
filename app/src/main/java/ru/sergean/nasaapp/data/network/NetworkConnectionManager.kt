package ru.sergean.nasaapp.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import ru.sergean.nasaapp.di.app.AppScope
import ru.sergean.nasaapp.di.app.ApplicationContext
import javax.inject.Inject

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

class NetworkConnectionManagerImpl(
    private val coroutineScope: CoroutineScope, context: Context
) : NetworkConnectionManager {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = NetworkCallback()

    private val currentNetwork = MutableStateFlow(CurrentNetwork())

    override val networkConnectionState: StateFlow<Boolean> =
        currentNetwork.map { it.isConnected() }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = currentNetwork.value.isConnected()
            )

    override val vpnConnectionState: StateFlow<Boolean>
        get() = currentNetwork.map { it.hasVpn() }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = currentNetwork.value.hasVpn()
            )

    override fun startListenNetworkState() {
        if (currentNetwork.value.isListening) return

        currentNetwork.update {
            CurrentNetwork(isListening = true)
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun stopListenNetworkState() {
        if (!currentNetwork.value.isListening) return


        currentNetwork.update {
            it.copy(isListening = false)
        }

        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private inner class NetworkCallback : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            currentNetwork.update {
                it.copy(isAvailable = true)
            }
        }

        override fun onLost(network: Network) {
            currentNetwork.update {
                it.copy(isAvailable = false, networkCapabilities = null)
            }
        }

        override fun onUnavailable() {
            currentNetwork.update {
                it.copy(isAvailable = false, networkCapabilities = null)
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            currentNetwork.update {
                it.copy(networkCapabilities = networkCapabilities)
            }
        }

        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            currentNetwork.update {
                it.copy(isBlocked = blocked)
            }
        }
    }


    private data class CurrentNetwork(
        val isListening: Boolean = false,
        val networkCapabilities: NetworkCapabilities? = null,
        val isAvailable: Boolean = false,
        val isBlocked: Boolean = false,
    )

    private fun CurrentNetwork.isConnected(): Boolean {
        return isListening &&
                isAvailable &&
                !isBlocked &&
                networkCapabilities.isNetworkCapabilitiesValid()
    }

    private fun CurrentNetwork?.hasVpn(): Boolean {
        if (this?.networkCapabilities == null) return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }

    private fun NetworkCapabilities?.isNetworkCapabilitiesValid(): Boolean = when {
        this == null -> false
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> true
        else -> false
    }
}
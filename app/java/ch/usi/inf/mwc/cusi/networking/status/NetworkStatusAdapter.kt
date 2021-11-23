package ch.usi.inf.mwc.cusi.networking.status

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.*

class NetworkStatusAdapter(context: Context) : DefaultLifecycleObserver {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val status = MutableLiveData<NetworkStatus>(NetworkStatus.NotConnected)
    private val callback = Callback(status)

    init {
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        connectivityManager.unregisterNetworkCallback(callback)
        super.onDestroy(owner)
    }

    fun getStatus(): LiveData<NetworkStatus> {
        return status
    }

    private class Callback(
        private val statusLiveData: MutableLiveData<NetworkStatus>,
    ) : ConnectivityManager.NetworkCallback() {
        private var isConnected: Boolean = false
        private var isMetered: Boolean = false

        override fun onAvailable(network: Network) {
            isConnected = true
            updateLiveData()
        }

        override fun onLost(network: Network) {
            isConnected = false
            updateLiveData()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            isMetered = !networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            updateLiveData()
        }

        private fun updateLiveData() {
            statusLiveData.value = if (isConnected) {
                NetworkStatus.Connected(isMetered)
            } else {
                NetworkStatus.NotConnected
            }
        }
    }
}
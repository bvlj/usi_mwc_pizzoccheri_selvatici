package ch.usi.inf.mwc.cusi.networking.status

sealed class NetworkStatus {
    data class Connected(val metered: Boolean) : NetworkStatus()
    object NotConnected : NetworkStatus()
}

package com.scodash.android.services.impl

import com.scodash.android.services.ServerWebsocketService
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.jackson.JacksonMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
class ServerWebsocketServiceProvider(internal var client: OkHttpClient) {
    //public static final String WS_URL = "wss://www.scodash.com/ws/";
    private var currentHash: String? = null

    private var currentServerWebsocketService: ServerWebsocketService? = null

    fun getInstance(hash: String): ServerWebsocketService? {
        if (currentHash == null) {
            setNewCurrentConnection(hash)
        } else if (currentHash != hash) {
            closeCurrentConnection()
            setNewCurrentConnection(hash)
        }
        return currentServerWebsocketService
    }

    private fun closeCurrentConnection() {
        // TODO implement
    }

    private fun setNewCurrentConnection(hash: String) {
        currentServerWebsocketService = createServerWebsocketService(hash)
        currentHash = hash
    }

    private fun createServerWebsocketService(hash: String): ServerWebsocketService {
        return Scarlet.Builder()
                .webSocketFactory(client.newWebSocketFactory(WS_URL))
                .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
                .addMessageAdapterFactory(JacksonMessageAdapter.Factory())
                .build().create(ServerWebsocketService::class.java)
    }

    companion object {

        //val WS_URL = "ws://localhost:9000/ws/"
        val WS_URL = "wss://ws-feed.gdax.com"
    }
}

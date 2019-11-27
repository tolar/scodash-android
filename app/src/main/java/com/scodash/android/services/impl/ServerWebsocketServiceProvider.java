package com.scodash.android.services.impl;

import com.scodash.android.services.ServerWebsocketService;
import com.tinder.scarlet.Scarlet;
import com.tinder.scarlet.messageadapter.jackson.JacksonMessageAdapter;
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory;
import com.tinder.scarlet.websocket.okhttp.OkHttpClientUtils;
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket;
import com.tinder.scarlet.websocket.okhttp.request.RequestFactory;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ServerWebsocketServiceProvider {

    private static final String WS_URL = "ws://www.scodash.com/ws/";

    private OkHttpClient client;

    private String currentHash;
    private ServerWebsocketService currentServerWebsocketService;

    public ServerWebsocketServiceProvider(OkHttpClient client) {
        this.client = client;
    }

    ServerWebsocketService getInstance(String hash) {
        if (currentHash == null) {
            setNewCurrentConnection(hash);
        } else if (currentHash != hash) {
            closeCurrentConnection();
            setNewCurrentConnection(hash);
        }
        return currentServerWebsocketService;
    }

    private void closeCurrentConnection() {
        // TODO naimplementovat
    }

    private void setNewCurrentConnection(String hash) {
        currentHash = hash;
        currentServerWebsocketService = createServerWebsocketService(hash);
    }

    private ServerWebsocketService createServerWebsocketService(final String hash) {
        RequestFactory requestFactory = new RequestFactory() {
            @NotNull
            @Override
            public Request createRequest() {

                Request.Builder rb = new Request.Builder();
                rb.url(WS_URL + hash).addHeader("Origin", "www.scodash.com").build();

                return rb.build();
            }
        };
        return new Scarlet.Builder()
                .webSocketFactory(OkHttpClientUtils.newWebSocketFactory(client, requestFactory))
                .addStreamAdapterFactory(new RxJava2StreamAdapterFactory())
                .addMessageAdapterFactory(new JacksonMessageAdapter.Factory())
                .build().create(ServerWebsocketService.class);
    }


}


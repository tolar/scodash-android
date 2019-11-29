package com.scodash.android.services.impl;

import com.scodash.android.services.ServerWebsocketConnectionService;
import com.tinder.scarlet.Scarlet;
import com.tinder.scarlet.messageadapter.jackson.JacksonMessageAdapter;
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory;
import com.tinder.scarlet.websocket.okhttp.OkHttpClientUtils;
import com.tinder.scarlet.websocket.okhttp.request.RequestFactory;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ServerWebsocketServiceProvider {

    // TODO implement SSL
    private static final String WS_URL = "ws://www.scodash.com/ws/";
    private static final String ORIGIN_HEADER = "http://www.scodash.com";

    private OkHttpClient client;

    private Map<String, ServerWebsocketConnectionService> connections = new HashMap<>();

    public ServerWebsocketServiceProvider(OkHttpClient client) {
        this.client = client;
    }

    ServerWebsocketConnectionService getInstance(String hash) {
        if (connections.containsKey(hash)) {
            return connections.get(hash);
        } else {
            ServerWebsocketConnectionService newNerverWebsocketConnectionService = createServerWebsocketService(hash);
            connections.put(hash, newNerverWebsocketConnectionService);
            return newNerverWebsocketConnectionService;
        }
    }

    private ServerWebsocketConnectionService createServerWebsocketService(final String hash) {
        RequestFactory requestFactory = new RequestFactory() {
            @NotNull
            @Override
            public Request createRequest() {
                Request.Builder rb = new Request.Builder();
                rb.url(WS_URL + hash).addHeader("Origin", ORIGIN_HEADER).build();
                return rb.build();
            }
        };
        return new Scarlet.Builder()
                .webSocketFactory(OkHttpClientUtils.newWebSocketFactory(client, requestFactory))
                .addStreamAdapterFactory(new RxJava2StreamAdapterFactory())
                .addMessageAdapterFactory(new JacksonMessageAdapter.Factory())
                .build().create(ServerWebsocketConnectionService.class);
    }


}


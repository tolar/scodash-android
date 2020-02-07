package com.scodash.android.services.impl;

import com.scodash.android.services.ServerWebsocketConnectionService;
import com.tinder.scarlet.Lifecycle;
import com.tinder.scarlet.Scarlet;
import com.tinder.scarlet.ShutdownReason;
import com.tinder.scarlet.lifecycle.DefaultLifecycle;
import com.tinder.scarlet.lifecycle.LifecycleRegistry;
import com.tinder.scarlet.messageadapter.jackson.JacksonMessageAdapter;
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory;
import com.tinder.scarlet.websocket.okhttp.OkHttpClientUtils;
import com.tinder.scarlet.websocket.okhttp.request.RequestFactory;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscriber;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ServerWebsocketServiceProvider {

    // TODO implement SSL
    private static final String WS_URL = "ws://" + ScodashService.HOSTNAME + "/ws/";
    private static final String ORIGIN_HEADER = "http://" + ScodashService.HOSTNAME;

    private OkHttpClient client;

    private Map<String, ServerWebsocketConnectionService> connections = new HashMap<>();
    private Map<String, LifecycleRegistry> lifecycles = new HashMap<>();

    public ServerWebsocketServiceProvider(OkHttpClient client) {
        this.client = client;
    }

    ServerWebsocketConnectionService getWsConnection(String hash) {
        if (connections.containsKey(hash)) {
            return connections.get(hash);
        } else {
            ServerWebsocketConnectionService newNerverWebsocketConnectionService = createServerWebsocketService(hash);
            getLifecycle(hash).onNext(Lifecycle.State.Started.INSTANCE);
            connections.put(hash, newNerverWebsocketConnectionService);
            return newNerverWebsocketConnectionService;
        }
    }

    private LifecycleRegistry getLifecycle(String hash) {
        if (lifecycles.containsKey(hash)) {
            return lifecycles.get(hash);
        } else {
            LifecycleRegistry newLifecycles = new LifecycleRegistry(0);
            lifecycles.put(hash, newLifecycles);
            return newLifecycles;
        }
    }

    void removeWsConnection(String hash) {
        getLifecycle(hash).onNext(new Lifecycle.State.Stopped.WithReason(ShutdownReason.GRACEFUL));
        lifecycles.remove(hash);
        connections.remove(hash);
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
                .lifecycle(getLifecycle(hash))
                .addStreamAdapterFactory(new RxJava2StreamAdapterFactory())
                .addMessageAdapterFactory(new JacksonMessageAdapter.Factory())
                .build().create(ServerWebsocketConnectionService.class);
    }




}


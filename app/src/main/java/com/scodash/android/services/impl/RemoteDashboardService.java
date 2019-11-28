package com.scodash.android.services.impl;

import android.util.Log;

import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.ServerWebsocketConnectionService;
import com.tinder.scarlet.WebSocket;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.functions.Consumer;

@Singleton
public class RemoteDashboardService {


    @Inject
    ServerWebsocketServiceProvider serverWebsocketServiceProvider;

    private ServerWebsocketConnectionService serverWebsocketConnectionService;

    @Inject
    public RemoteDashboardService() {
    }



    public void connectToServer(String hash) {
        Log.d(this.getClass().getSimpleName(), "Connecting to server with hash " + hash);
        serverWebsocketConnectionService = serverWebsocketServiceProvider.getInstance(hash);
        serverWebsocketConnectionService.receiveDashboardUpdate().subscribe(new Consumer<Dashboard>() {
            @Override
            public void accept(Dashboard dashboard) throws Exception {
                Log.d(this.getClass().getSimpleName(), "message received");
            }
        });
        serverWebsocketConnectionService.observeWebsocketEvent().subscribe(new Consumer<WebSocket.Event>() {
            @Override
            public void accept(WebSocket.Event event) throws Exception {
                Log.d(this.getClass().getSimpleName(), "message received");
            }
        });
    }




}

package com.scodash.android.services;

import com.scodash.android.dto.Dashboard;
import com.scodash.android.dto.DashboardUpdateDto;
import com.tinder.scarlet.WebSocket;
import com.tinder.scarlet.ws.Receive;
import com.tinder.scarlet.ws.Send;

import io.reactivex.Flowable;

public interface ServerWebsocketService {

    @Send
    void updateDashboard(DashboardUpdateDto dashboardUpdateDto);

    @Receive
    Flowable<Dashboard> receiveDashboardUpdate();

    @Receive
    Flowable<WebSocket.Event> observeWebsocketEvent();
}

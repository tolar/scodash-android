package com.scodash.android.services.impl;

import android.util.Log;

import com.scodash.android.dto.Dashboard;
import com.scodash.android.dto.DashboardUpdateDto;
import com.scodash.android.dto.Item;
import com.scodash.android.services.ServerWebsocketConnectionService;
import com.tinder.scarlet.WebSocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.functions.Consumer;

@Singleton
public class ScodashService {

    private Dashboard currentDashboard;

    private List<CurrentDashboardChangeListener> currentDashboardChangeListeners = new ArrayList<>();

    private List<Dashboard> localDashboards = new ArrayList<>();

    private Comparator<Item> azComparator;
    private Comparator<Item> scoreComparator;

    private ServerWebsocketConnectionService serverWebsocketConnectionService;

    @Inject
    ServerWebsocketServiceProvider serverWebsocketServiceProvider;

    @Inject
    public ScodashService() {
        initData();
    }

    private void initData() {
        final Dashboard d1 = new Dashboard();
        d1.setName("Text");
        d1.setDescription("popis");
        d1.setHash("oWwvZ2aT");
        d1.setCreated(new Date());
        d1.setUpdated(new Date());
        localDashboards.add(d1);
        final Dashboard d2 = new Dashboard();
        d2.setName("Today Scrabble Game");
        d2.setDescription("afternoon session");
        d2.setHash("RH5lbxGr");
        d2.setCreated(new Date());
        d2.setUpdated(new Date());
        localDashboards.add(d2);
    }

    public void addCurrentDashboardChangeListener(CurrentDashboardChangeListener changeListener) {
        this.currentDashboardChangeListeners.add(changeListener);
    }

    public void createDashboard(Dashboard newDashboard) {
        localDashboards.add(newDashboard);
    }

    public void setCurrentDashboard(Dashboard dashboard) {
        currentDashboard = dashboard;
        notifyListener();
    }

    public void connectToDashboardOnServer(Dashboard dashboard) {
        connectToServer(dashboard.getHash());
    }

    public void sendUpdateDataToServer(DashboardUpdateDto dashboardUpdate) {
        serverWebsocketConnectionService.updateDashboard(dashboardUpdate);
    }

    private void notifyListener() {
        for (int i = 0; i < currentDashboardChangeListeners.size(); i++) {
            CurrentDashboardChangeListener currentDashboardChangeListener = currentDashboardChangeListeners.get(i);
            currentDashboardChangeListener.currentDashboardChanged(currentDashboard);
        }
    }

    public Dashboard getCurrentDashboard() {
        return currentDashboard;
    }

    public Dashboard getDashboardByHash(String hash) {
        for (int i = 0; i < localDashboards.size(); i++) {
            Dashboard dashboard = localDashboards.get(i);
            if (hash.equals(dashboard.getReadonlyHash()) || hash.equals(dashboard.getWriteHash()) || hash.equals(dashboard.getHash()) ) {
                return dashboard;
            }
        }
        return null;
    }

    public List<Dashboard> getLocalDashboards() {
        return localDashboards;
    }

    public Item getCurrentItem(int index, Sorting sorting) {
        List itemsList = new ArrayList<>();
        itemsList.addAll(currentDashboard.getItems());
        if (sorting == Sorting.SCORE) {
            Collections.sort(itemsList, getScoreComparator());
        } else {
            Collections.sort(itemsList, getAzComparator());
        }
        return (Item) itemsList.get(index);
    }

    public Comparator<Item> getAzComparator() {
        if (azComparator == null) {
            azComparator = new AzComparator();
        }
        return azComparator;
    }

    public Comparator<Item> getScoreComparator() {
        if (scoreComparator == null) {
            scoreComparator = new ScoreComparator();
        }
        return scoreComparator;

    }

    public int getCurrentDashboardItemCount() {
        return currentDashboard.getItems().size();
    }


    private void connectToServer(String hash) {
        Log.d(this.getClass().getSimpleName(), "Connecting to server with hash " + hash);
        serverWebsocketConnectionService = serverWebsocketServiceProvider.getInstance(hash);
        serverWebsocketConnectionService.receiveDashboardUpdate().subscribe(new Consumer<Dashboard>() {
            @Override
            public void accept(Dashboard dashboard) {
                // check that message is for current dashboard
                if (currentDashboard == null || isHashForDashboard(currentDashboard.getHash(), dashboard)) {
                    setCurrentDashboard(dashboard);
                }
            }
        });
        serverWebsocketConnectionService.observeWebsocketEvent().subscribe(new Consumer<WebSocket.Event>() {
            @Override
            public void accept(WebSocket.Event event) throws Exception {
                Log.d(this.getClass().getSimpleName(), event.toString());
            }
        });
    }

    private boolean isHashForDashboard(String hash, Dashboard dashboard) {
        if (hash == null || dashboard == null) {
            return false;
        }
        if (hash.equals(dashboard.getHash()) || hash.equals(dashboard.getReadonlyHash()) || hash.equals(dashboard.getWriteHash())) {
            return true;
        }
        return false;
    }


    class AzComparator implements Comparator<Item> {

        @Override
        public int compare(Item item1, Item item2) {
            if (item1 != null && item1.getName() != null && item2 != null && item2.getName() != null) {
                return item1.getName().compareTo(item2.getName());
            }
            return 0;
        }
    }

    class ScoreComparator implements Comparator<Item> {

        @Override
        public int compare(Item item1, Item item2) {
            if (item1 != null && item2 != null) {
                if (item1.getScore() == item2.getScore()) {
                    return 0;
                }
                if (item1.getScore() < item2.getScore()) {
                    return 1;
                }
                if (item1.getScore() > item2.getScore()) {
                    return -1;
                }
            }
            return 0;

        }
    }


}

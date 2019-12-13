package com.scodash.android.services.impl;

import android.content.SharedPreferences;
import android.util.Log;

import com.scodash.android.dto.Dashboard;
import com.scodash.android.dto.DashboardUpdateDto;
import com.scodash.android.dto.Item;
import com.scodash.android.services.ServerRestService;
import com.scodash.android.services.ServerWebsocketConnectionService;
import com.tinder.scarlet.WebSocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.scodash.android.services.ServerRestService.BASE_URL;

@Singleton
public class ScodashService {

    /**
     * Key to shared preferences for list hashes
     */
    private static final String HASHES = "HASHES";
    public static final String HOSTNAME = "www.scodash.com";
    private Dashboard currentDashboard;

    private List<CurrentDashboardChangeListener> currentDashboardChangeListeners = new ArrayList<>();

    private Map<String, Dashboard> remoteDashboards = new HashMap<>();

    private Comparator<Item> azComparator;
    private Comparator<Item> scoreComparator;

    private ServerWebsocketConnectionService serverWebsocketConnectionService;

    private ServerRestService serverRestService;

    @Inject
    ServerWebsocketServiceProvider serverWebsocketServiceProvider;

    @Inject
    public ScodashService() {
        initServerRestService();
    }

    private void initServerRestService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        serverRestService = retrofit.create(ServerRestService.class);
    }

    public void addCurrentDashboardChangeListener(CurrentDashboardChangeListener changeListener) {
        this.currentDashboardChangeListeners.add(changeListener);
    }

    public void removeCurrentDashboardChangeListener(CurrentDashboardChangeListener changeListener) {
        this.currentDashboardChangeListeners.remove(changeListener);
    }

    public void createDashboard(Dashboard newDashboard) {
        // TODO create dashbaord on server
        remoteDashboards.put(newDashboard.getWriteHash(), newDashboard);
    }

    public void setCurrentDashboard(Dashboard dashboard) {
        currentDashboard = dashboard;
        notifyListener();
    }

    public void connectToDashboardOnServer(String hash) {
        connectToServer(hash);
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

    public Call<Dashboard> getRemoteDashboardByHash(String hash) {
        return serverRestService.getRemoteDashboardByHash(hash);
    }

    private List<Dashboard> getRemoteDashboards() {
        return new ArrayList<>(remoteDashboards.values());
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
                    // if so, we update also current dashboard
                    populateAccessHash(dashboard);
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



    public void putHashToLocalStorage(SharedPreferences sharedPreferences, String hash) {
        addHashToLocalStorage(sharedPreferences, hash);
    }

    public void populateAccessHash(Dashboard dashboardByHash) {
        String hash = dashboardByHash.getWriteHash();
        if (hash == null || hash.isEmpty()) {
            hash = dashboardByHash.getReadonlyHash();
        }
        dashboardByHash.setHash(hash);
    }

    private void addHashToLocalStorage(SharedPreferences sharedPreferences, String hash) {
        Set<String> newHashes = new HashSet<>();
        newHashes.add(hash);
        Set<String> hashes = sharedPreferences.getStringSet(HASHES, new HashSet<String>());
        newHashes.addAll(hashes);
        sharedPreferences.edit().putStringSet(HASHES, newHashes).commit();
    }

    public void removeHashFromLocaStorage(SharedPreferences sharedPreferences, String hash) {
        Set<String> newHashes = new HashSet<>();
        Set<String> hashes = sharedPreferences.getStringSet(HASHES, new HashSet<String>());
        newHashes.addAll(hashes);
        newHashes.remove(hash);
        sharedPreferences.edit().putStringSet(HASHES, newHashes).commit();
    }

    public List<String> getHashesFromLocalStorage(SharedPreferences sharedPreferences) {
        List<String> list = new ArrayList<>(sharedPreferences.getStringSet(HASHES, new HashSet<String>()));
        Collections.sort(list);
        return list;
    }

    public boolean hasWriteModeInLocalStorage(SharedPreferences sharedPreferences, String hash) {
        return getHashesFromLocalStorage(sharedPreferences).contains(hash);
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

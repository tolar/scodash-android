package com.scodash.android.services.impl;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.scodash.android.dto.Dashboard;
import com.scodash.android.dto.DashboardUpdateDto;
import com.scodash.android.dto.HashNameTuple;
import com.scodash.android.dto.Item;
import com.scodash.android.services.ServerRestService;
import com.scodash.android.services.ServerWebsocketConnectionService;
import com.tinder.scarlet.WebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.functions.Consumer;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private static final String USERNAME = "scodashUser";
    private static final String PASSWORD = "jacob";

    //private Dashboard currentDashboard;
    private Map<String, Dashboard> loadedDashboards = new HashMap<>();
    private Dashboard newDashboard;

    //private List<DashboardChangeListener> currentDashboardChangeListeners = new ArrayList<>();
    private Map<String, List<DashboardChangeListener>> loadedDashboardChangeListeners = new HashMap<>();

    private Comparator<Item> azComparator;
    private Comparator<Item> scoreComparator;
    private Comparator<HashNameTuple> hashNameTupleComparator;

    private ServerWebsocketConnectionService serverWebsocketConnectionService;

    private ServerRestService serverRestService;

    private Map<String, String> dashboardNamesPerHash = new HashMap<>();

    @Inject
    ServerWebsocketServiceProvider serverWebsocketServiceProvider;

    @Inject
    public ScodashService() {
        initServerRestService();
    }

    private void initServerRestService() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(USERNAME, PASSWORD))
                .addInterceptor(loggingInterceptor)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
                WRITE_DATES_AS_TIMESTAMPS , false);
        JacksonConverterFactory converterFactory = JacksonConverterFactory.create(objectMapper);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(converterFactory)
                .client(httpClient)
                .build();
        serverRestService = retrofit.create(ServerRestService.class);
    }

    public void addDashboardChangeListener(String hash, DashboardChangeListener changeListener) {
        List<DashboardChangeListener> dashboardChangeListeners = this.loadedDashboardChangeListeners.get(hash);
        if (dashboardChangeListeners == null) {
            dashboardChangeListeners = new ArrayList<>();
            this.loadedDashboardChangeListeners.put(hash, dashboardChangeListeners);
        }
        dashboardChangeListeners.add(changeListener);
    }

    public void removeCurrentDashboardChangeListener(String hash, DashboardChangeListener changeListener) {
        List<DashboardChangeListener> dashboardChangeListeners = this.loadedDashboardChangeListeners.get(hash);
        if (dashboardChangeListeners != null) {
            dashboardChangeListeners.remove(changeListener);
        }
    }

    public Call<Dashboard> createDashboard(Dashboard newDashboard) {
        populateItemsIndexes(newDashboard);
        return serverRestService.createDashboard(newDashboard);
    }

    private void populateItemsIndexes(Dashboard newDashboard) {
        Item[] items = newDashboard.getItems().toArray(new Item[newDashboard.getItems().size()]);
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            item.setId(i);
        }
    }

    public void addLoadedDashboard(String hash, Dashboard dashboard) {
        loadedDashboards.put(hash, dashboard);
        dashboardNamesPerHash.put(dashboard.getHash(), dashboard.getName());
    }

    public void connectToDashboardOnServer(String hash) {
        connectToServer(hash);
    }

    public void sendUpdateDataToServer(DashboardUpdateDto dashboardUpdate) {
        serverWebsocketConnectionService.updateDashboard(dashboardUpdate);
    }

    private void notifyListeners(String hash) {
        List<DashboardChangeListener> listeners = loadedDashboardChangeListeners.get(hash);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                DashboardChangeListener dashboardChangeListener = listeners.get(i);
                dashboardChangeListener.dashboardChanged(loadedDashboards.get(hash));
            }
        }
    }

    public Dashboard getLoadedDashboard(String hash) {
        return loadedDashboards.get(hash);
    }

    public String getWriteDashboardUrl(String hash) {
        Dashboard dashboard = loadedDashboards.get(hash);
        if (dashboard != null) {
            return "https://www.scodash.com/dashboard/" + dashboard.getWriteHash();
        } else {
            return null;
        }
    }

    public String getReadonlyDashboardUrl(String hash) {
        Dashboard dashboard = loadedDashboards.get(hash);
        if (dashboard != null) {
            return "https://www.scodash.com/dashboard/" + dashboard.getReadonlyHash();
        } else {
            return null;
        }
    }

    public Call<Dashboard> getRemoteDashboardByHash(String hash) {
        return serverRestService.getRemoteDashboardByHash(hash);
    }

    public Item getItem(String hash, int index, Sorting sorting) {
        List itemsList = new ArrayList<>();
        itemsList.addAll(loadedDashboards.get(hash).getItems());
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

    public Comparator<HashNameTuple> getHashNameTupleComparator() {
        if (hashNameTupleComparator == null) {
            hashNameTupleComparator = new HashNameComparator();
        }
        return hashNameTupleComparator;
    }

    public int getDashboardItemCount(String hash) {
        Dashboard dashboard = loadedDashboards.get(hash);
        if (dashboard == null) {
            return 0;
        }
        return dashboard.getItems().size();
    }


    @SuppressLint("CheckResult")
    private void connectToServer(String hash) {
        serverWebsocketConnectionService = serverWebsocketServiceProvider.getInstance(hash);
        serverWebsocketConnectionService.receiveDashboardUpdate().subscribe(new Consumer<Dashboard>() {
            @Override
            public void accept(Dashboard dashboard) {
                addLoadedDashboard(hash, dashboard);
                notifyListeners(hash);
            }
        });
        serverWebsocketConnectionService.observeWebsocketEvent().subscribe(new Consumer<WebSocket.Event>() {
            @Override
            public void accept(WebSocket.Event event) throws Exception {
            }
        });
    }

    public void putHashToLocalStorage(SharedPreferences sharedPreferences, String hash) {
        addHashToLocalStorage(sharedPreferences, hash);
    }

    public void populateAccessHash(Dashboard dashboardByHash) {
        String hash = dashboardByHash.getWriteHash();
        if (TextUtils.isEmpty(hash)) {
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
        List<String> hashes = new ArrayList<>(sharedPreferences.getStringSet(HASHES, new HashSet<String>()));
        List<HashNameTuple> hashNameTuples = new ArrayList<>(hashes.size());
        List<String> sortedHashes = new ArrayList<>(hashes.size());
        for (int i = 0; i < hashes.size(); i++) {
            String hash =  hashes.get(i);
            hashNameTuples.add(new HashNameTuple(hash, dashboardNamesPerHash.get(hash)));
        }
        Collections.sort(hashNameTuples, getHashNameTupleComparator());
        for (int i = 0; i < hashNameTuples.size(); i++) {
            HashNameTuple hashNameTuple =  hashNameTuples.get(i);
            sortedHashes.add(hashNameTuple.getHash());
        }
        return sortedHashes;
    }

    public boolean hasWriteModeInLocalStorage(SharedPreferences sharedPreferences, String hash) {
        return getHashesFromLocalStorage(sharedPreferences).contains(hash);
    }

    public Dashboard getNewDashboard() {
        if (newDashboard == null) {
            newDashboard = new Dashboard();
        }
        return newDashboard;
    }

    public void resetNewDashbord() {
        newDashboard = new Dashboard();
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

    class HashNameComparator implements Comparator<HashNameTuple> {

        @Override
        public int compare(HashNameTuple item1, HashNameTuple item2) {
            if (item1 != null && item1.getName() != null && item2 != null && item2.getName() != null) {
                return item1.getName().compareTo(item2.getName());
            }
            return 0;
        }
    }


    private class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }
    }
}

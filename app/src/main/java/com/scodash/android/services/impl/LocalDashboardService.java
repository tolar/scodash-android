package com.scodash.android.services.impl;

import com.scodash.android.dto.Dashboard;
import com.scodash.android.dto.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocalDashboardService {

    private Dashboard currentDashboard;

    private List<Dashboard> localDashboards = new ArrayList<>();

    private Comparator<Item> azComparator;
    private Comparator<Item> scoreComparator;

    @Inject
    RemoteDashboardService remoteDashboardService;

    @Inject
    public LocalDashboardService() {
        initData();
    }

    private void initData() {
        final Dashboard d1 = new Dashboard();
        d1.setName("Text");
        d1.setDescription("popis");
        d1.setHash("bhnchWpU");
        d1.setWriteMode(false);
        d1.setCreated(new Date());
        d1.setUpdated(new Date());
        localDashboards.add(d1);
        final Dashboard d2 = new Dashboard();
        d2.setName("dlouhy popis");
        d2.setDescription("");
        d2.setHash("RH5lbxGr");
        d2.setWriteMode(true);
        d2.setCreated(new Date());
        d2.setUpdated(new Date());
        localDashboards.add(d2);
    }

    public void createDashboard(Dashboard newDashboard) {
        localDashboards.add(newDashboard);
        currentDashboard = newDashboard;
    }


    public void setCurrentDashboard(Dashboard currentDashboard) {
        this.currentDashboard = currentDashboard;
        remoteDashboardService.connectToServer(currentDashboard.getHash());
    }

    public Dashboard getCurrentDashboard() {
        return currentDashboard;
    }

    public Dashboard getDashboardByHash(String hash) {
        for (int i = 0; i < localDashboards.size(); i++) {
            Dashboard dashboard = localDashboards.get(i);
            if (hash.equals(dashboard.getReadHash()) || hash.equals(dashboard.getWriteHash()) || hash.equals(dashboard.getHash()) ) {
                currentDashboard = dashboard;
                return dashboard;
            }
        }
        return null;
    }

    public List<Dashboard> getLocalDashboards() {
        return localDashboards;
    }

    public Item getItem(int index, Sorting sorting) {
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

    public int getItemCount() {
        return currentDashboard.getItems().size();
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

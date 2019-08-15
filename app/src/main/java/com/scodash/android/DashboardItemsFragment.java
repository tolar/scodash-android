package com.scodash.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class DashboardItemsFragment extends Fragment {

    private DashboardItemsAdapter itemsAdapter;

    public DashboardItemsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_items, container, false);

        RecyclerView itemsRecyler = rootView.findViewById(R.id.items);
        itemsAdapter = new DashboardItemsAdapter();
        itemsRecyler.setAdapter(itemsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        itemsRecyler.setLayoutManager(layoutManager);

        return rootView;
    }

    public void notifyItemsChanged() {
        itemsAdapter.notifyDataSetChanged();
    }

}

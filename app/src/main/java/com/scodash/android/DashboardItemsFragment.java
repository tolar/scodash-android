package com.scodash.android;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class DashboardItemsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_items, container, false);
//        ListView itemsView = rootView.findViewById(R.id.items);
//        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, NewDashboard.items);
//        itemsView.setAdapter(adapter);

        RecyclerView itemsRecyler = (RecyclerView)rootView.findViewById(R.id.items);
        DashboardItemsAdapter adapter = new DashboardItemsAdapter(NewDashboard.items);
        itemsRecyler.setAdapter(adapter);

        return rootView;
    }

}

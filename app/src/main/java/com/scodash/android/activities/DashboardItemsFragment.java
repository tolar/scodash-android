package com.scodash.android.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scodash.android.R;
import com.scodash.android.services.impl.ScodashService;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class DashboardItemsFragment extends DaggerFragment {

    @Inject
    public ScodashService scodashService;

    private NewDashboardItemsAdapter itemsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_items, container, false);

        final View newItemAddButton = rootView.findViewById(R.id.new_item_add_button);
        newItemAddButton.setVisibility(View.INVISIBLE);

        attachItemsAdapter(rootView);
        attachNewItemTextLister(rootView);

        return rootView;
    }



    private void attachItemsAdapter(View rootView) {
        RecyclerView itemsRecyler = rootView.findViewById(R.id.new_items);
        itemsAdapter = new NewDashboardItemsAdapter(scodashService);
        itemsRecyler.setAdapter(itemsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        itemsRecyler.setLayoutManager(layoutManager);
    }

    private void attachNewItemTextLister(View rootView) {
        final EditText newItemEditText = rootView.findViewById(R.id.new_item_name);
        final View newItemAddButton = rootView.findViewById(R.id.new_item_add_button);
        newItemEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(s.toString())) {
                    newItemAddButton.setEnabled(true);
                    newItemAddButton.setVisibility(View.VISIBLE);
                } else {
                    newItemAddButton.setEnabled(false);
                    newItemAddButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void notifyItemsChanged() {
        itemsAdapter.notifyDataSetChanged();
    }



}

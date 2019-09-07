package com.scodash.android;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class DashboardAuthorFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_author, container, false);
        attachTextChangeListener(view);
        return view;
    }

    private void attachTextChangeListener(View view) {

        EditText editAuthorName = view.findViewById(R.id.author_name);
        final TextInputLayout editAuthorNameTil = view.findViewById(R.id.author_name_input_layout);
        editAuthorName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    editAuthorNameTil.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText editAuthorEmail = view.findViewById(R.id.author_email);
        final TextInputLayout editAuthorEmailTil = view.findViewById(R.id.author_email_input_layout);
        editAuthorEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    editAuthorEmailTil.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}

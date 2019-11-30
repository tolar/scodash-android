package com.scodash.android.activities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.scodash.android.R;

public abstract class ScodashActivity extends AppCompatActivity {
    protected SharedPreferences getScodashSharedPreferences() {
        return getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }
}

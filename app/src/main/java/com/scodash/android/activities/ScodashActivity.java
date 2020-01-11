package com.scodash.android.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.scodash.android.R;

public abstract class ScodashActivity extends AppCompatActivity {

    private Snackbar noInternetSnackbarWithAction;

    protected SharedPreferences getScodashSharedPreferences() {
        return getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }


    private Snackbar getNoInternetConnectionSnackbarWithAction() {
        if (noInternetSnackbarWithAction != null) {
            return noInternetSnackbarWithAction;
        }
        noInternetSnackbarWithAction = Snackbar.make(findViewById(R.id.dashboard), R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
        int snackbarTextId = R.id.snackbar_text;
        View snackbarView = noInternetSnackbarWithAction.getView();
        snackbarView.findViewById(snackbarTextId);
        TextView snackbarTextView = snackbarView.findViewById(snackbarTextId);
        snackbarTextView.setTextColor(getResources().getColor(R.color.colorErrorText));
        noInternetSnackbarWithAction.setActionTextColor(getResources().getColor(R.color.colorPureBlack));
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorErrorBackground));
        noInternetSnackbarWithAction.setAction("TRY AGAIN", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        return noInternetSnackbarWithAction;
    }

    private void dismissNoInternetSnackbarWithRetryAction() {
        getNoInternetConnectionSnackbarWithAction().dismiss();
    }

    protected void showNoInternetSnackbarWithRetryAction() {
        getNoInternetConnectionSnackbarWithAction().show();
    }

    public void recreate(View view) {
        recreate();
    }
}

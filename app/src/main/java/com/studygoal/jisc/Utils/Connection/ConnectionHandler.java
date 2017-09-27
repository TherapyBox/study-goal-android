package com.studygoal.jisc.Utils.Connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.R;

/**
 * Created by Marjana-Tbox on 20/09/17.
 */

public class ConnectionHandler {

    public static boolean isConnected(Context context) {
        boolean result = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            result = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }

        return result;
    }

    public static void showNoInternetConnectionSnackbar(){
        Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.no_internet_connection_hint, Snackbar.LENGTH_LONG).show();
    }
}

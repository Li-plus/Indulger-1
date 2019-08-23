package com.inftyloop.indulger.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class NetworkUtils {
    public static boolean isNetworkAvailable(Context ctx) {
        if(ctx != null) {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Network activeNetwork = cm.getActiveNetwork();
            if(activeNetwork != null) {
                final NetworkCapabilities nc = cm.getNetworkCapabilities(activeNetwork);
                return nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            }
        }
        return false;
    }
}

package com.Alikapp.alikappconductor;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

public class Common {
    public static final String KEY_REQUEST_LOCATION_UPDATES = "LocationUpdatesEnable";

    public static String getLocationText(Location mLocation) {
        return mLocation == null ? "Unknown Location" : new StringBuilder()
                .append(mLocation.getLatitude())
                .append("/")
                .append(mLocation.getLongitude())
                .toString();
    }

    public static CharSequence getLocationTitle(onAppKilled onAppKilled) {
        return String.format("Location Update : %1$s", DateFormat.getDateInstance().format(new Date()));
    }

    public static void setRequestLoctionUpdates(Context context, boolean value) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUEST_LOCATION_UPDATES, value)
                .apply();
    }

    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUEST_LOCATION_UPDATES, false);
    }
}

package com.Alikapp.alikappconductor;

import android.location.Location;

public class SendLocationActivity {
    private Location location;
    public SendLocationActivity(Location mLocation) {
        this.location = mLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

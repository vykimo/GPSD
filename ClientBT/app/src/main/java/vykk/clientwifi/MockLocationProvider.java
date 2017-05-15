package vykk.clientwifi;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.Criteria;
import android.location.LocationProvider;

import java.util.Timer;
import java.util.TimerTask;

public class MockLocationProvider {
    String providerName;
    Context ctx;
    LocationManager lm;
    Timer t;
    Location location= new Location(providerName);

    public MockLocationProvider(String name, Context ctx) throws SecurityException {
        this.providerName = name;
        this.ctx = ctx;
        lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        lm.addTestProvider(providerName, false, false, false, false, true, true, true, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
        lm.setTestProviderStatus(providerName, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
        lm.setTestProviderEnabled(providerName, true);
    }

    public void pushLocation(Position mockLocation) throws SecurityException,IllegalArgumentException {
        location= new Location(providerName);
        location.setLatitude(mockLocation.getLatitude());
        location.setLongitude(mockLocation.getLongitude());
        location.setAltitude(mockLocation.getAltitude());
        location.setBearing(mockLocation.getBearing());
        location.setSpeed(mockLocation.getSpeed());
        location.setTime(mockLocation.getTime());
        location.setElapsedRealtimeNanos(mockLocation.getElapsedRealtimeNanos());
        location.setAccuracy(mockLocation.getAccuracy());
        lm.setTestProviderLocation(providerName, location);
    }

    public void shutdown() {
        lm.removeTestProvider(providerName);
    }
}


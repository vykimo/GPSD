package vykk.clientwifi;

import android.location.Location;

public class Position {
    private double latitude;
    private double longitude;
    private double altitude;
    private float bearing;
    private float speed;
    private long time;
    private long elapsedRealtimeNanos;
    private float accuracy;

    public Position(Location location){
        this.latitude=location.getLatitude();
        this.longitude=location.getLongitude();
        this.altitude=location.getAltitude();
        this.bearing=location.getBearing();
        this.speed=location.getSpeed();
        this.time=location.getTime();
        this.elapsedRealtimeNanos=location.getElapsedRealtimeNanos();
        this.accuracy=location.getAccuracy();
    }

    public double getAltitude() {
        return altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getBearing() {
        return bearing;
    }

    public float getSpeed() {
        return speed;
    }

    public long getElapsedRealtimeNanos() {
        return elapsedRealtimeNanos;
    }

    public long getTime() {
        return time;
    }

}

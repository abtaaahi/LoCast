package com.abtahiapp.locast_aweatherapp;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
public class LocationProvider implements LocationListener {
    private Context context; private LocationManager locationManager; private Location currentLocation;
    public LocationProvider(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    public Location getCurrentLocation() {
        try {
            // Get GPS location
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (currentLocation == null) {
                // If GPS is not available, get network location
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return currentLocation;
    }
    @Override
    public void onLocationChanged(Location location) {
        // Update current location when it changes
        currentLocation = location;
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
}
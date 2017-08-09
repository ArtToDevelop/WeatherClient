package sigalov.arttodevelop.weatherclient.location;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import sigalov.arttodevelop.weatherclient.interfaces.OnChangeLocationListener;

public class LocationModule implements LocationListener {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10;// каждые 10 секунд

    private Context context;

    boolean isGPSEnabled, isNetworkEnabled, isStartLocation;

    private LocationManager locationManager;
    private OnChangeLocationListener listener;

    public LocationModule(Context context, OnChangeLocationListener listener)
    {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        this.context = context;
        this.listener = listener;
    }

    public boolean isSearched()
    {
        return isStartLocation;
    }

    public boolean isEnabled()
    {
        return isGPSEnabled || isNetworkEnabled;
    }

    public void startLocation()
    {
        if(isStartLocation)
            return;

        isStartLocation = true;

        try {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        }
        catch (Exception ex)
        {
            Log.e("LocationModule", "Error creating location service: " + ex.getMessage());
            isStartLocation = false;
        }
    }

    public void endLocation()
    {
        if(!isStartLocation)
            return;

        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(listener != null) {
            listener.OnChangeLocation(location);
        }
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

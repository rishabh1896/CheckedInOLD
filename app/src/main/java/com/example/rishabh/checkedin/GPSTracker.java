package com.example.rishabh.checkedin;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.annotation.Nullable;


public class GPSTracker extends Service implements LocationListener {

    Location location;
    private final Context context;
    boolean isGPSEnabled=false;
    boolean isNetworkEnabled=false;
    boolean canGetLocation=false;
    double lat;
    double lon;
    int a;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE=10L;
    private static final long MIN_TIME_BW_UPDATE=60000L;
    public GPSTracker(Context context)
    {
        this.context=context;
        getLocation();
    }
    protected LocationManager locationManager;
    public Location getLocation()
    {

        try{
            locationManager=(LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!(isGPSEnabled)&&!(isNetworkEnabled))
            {

            }
            else
            {
                this.canGetLocation=true;
                if(isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATE,MIN_DISTANCE_CHANGE_FOR_UPDATE,this);

                    if(locationManager!=null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }

                }
                if(isGPSEnabled)
                {
                    if(location==null)
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATE,MIN_DISTANCE_CHANGE_FOR_UPDATE,this);
                        if(locationManager!=null)
                        {
                            location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                        if(location!=null)
                        {

                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }
            }
        }
        catch(SecurityException e)
        {
            e.printStackTrace();;
        }
        return location;
    }
    public void stopUsingGps()
    {
        try {
            if (locationManager != null)
                locationManager.removeUpdates(GPSTracker.this);
        }catch(SecurityException e)
        {
            e.printStackTrace();
        }
    }
    public double getLongitude()
    {
        if(location!=null)
            lon=location.getLongitude();
        return lon;
    }
    public double getLatitude()
    {
        if(location!=null)
            lat=location.getLatitude();
        return lat;
    }
    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }
    public void showSettingAlert()
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setMessage("GPS is Settings");
        alertDialog.setMessage("GPS is not enabled.Do you want to go to settings");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }

        });
        alertDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

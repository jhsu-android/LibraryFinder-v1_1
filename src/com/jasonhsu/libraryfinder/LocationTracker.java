package com.jasonhsu.libraryfinder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationTracker extends Service implements LocationListener{

	private final Context Context1;
	
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	
	Location Location1 = null;
	double latitude; // latitude
	double longitude; // longitude
	
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	
	// Declaring a Location Manager
	protected LocationManager LocationManager1;
	
	public LocationTracker(Context context) {
		this.Context1 = context;
		getLocation();
	}
	
	@Override
	public void onLocationChanged(Location Location1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Location getLocation() {
		try {
			LocationManager1 = (LocationManager) Context1
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = LocationManager1
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = LocationManager1
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// Neither location service is enabled.
			} 
			else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					LocationManager1.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network Enabled");
					if (LocationManager1 != null) {
						Location1 = LocationManager1
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (Location1 != null) {
							latitude = Location1.getLatitude();
							longitude = Location1.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (Location1 == null) {
						LocationManager1.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS", "GPS Enabled");
						if (LocationManager1 != null) {
							Location1 = LocationManager1
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (Location1 != null) {
								latitude = Location1.getLatitude();
								longitude = Location1.getLongitude();
							}
						}
					}
				}
			}

		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		return Location1;
	}


}
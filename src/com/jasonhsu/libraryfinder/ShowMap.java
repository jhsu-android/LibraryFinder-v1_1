package com.jasonhsu.libraryfinder;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.MapActivity;

public class ShowMap extends MapActivity {
	
	Boolean LocalSelectedLocationCurrent;
	
	// For getting location
	private LocationManager LocationManager1;
	private Location Location1;
	private LocationListener LocationListener1;
	private int LatInt1, LongInt1;

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		// Start map
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        LocalSelectedLocationCurrent = GlobalVariables.SelectedLocationCurrent;

        if (LocalSelectedLocationCurrent == false) {
        	ProcedureAltLocation ();
        }
        else {
        	ProcedureCurrentLocation ();
        }
        
        //ShowMapOfHere ();
        //MarkerHere ();
        //GetPlaces ();
	}
	
	private void ProcedureAltLocation() {
		Toast.makeText(getApplicationContext(), 
                "LOCATION: alternate", Toast.LENGTH_LONG).show();
		GetLocationAlt ();
	}
	
	private void ProcedureCurrentLocation () {
		Toast.makeText(getApplicationContext(), 
                "LOCATION: current", Toast.LENGTH_LONG).show();
		GetLocationCurrent ();
	}
	
	private void GetLocationAlt () {
		
	}
	
	private void GetLocationCurrent () {
		LocationManager1 = (LocationManager)getSystemService(
				Context.LOCATION_SERVICE);
		Location Location1 = LocationManager1.getLastKnownLocation
				(LocationManager1.GPS_PROVIDER);
		LatInt1 = (int)(Location1.getLatitude()*1000000);
		LongInt1 = (int)(Location1.getLongitude()*1000000);		
	}

}

package com.jasonhsu.libraryfinder;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

public class ShowMap extends MapActivity {

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		// Draw map
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        //ShowMapOfHere ();
        //MarkerHere ();
        //GetPlaces ();
	}

}

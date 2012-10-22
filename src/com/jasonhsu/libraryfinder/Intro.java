package com.jasonhsu.libraryfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Intro extends Activity {
	
	// Keep track of whether current or alternate location is selected
	Boolean LocalSelectedLocationCurrent = true;
	
	// Connection detector class
	ConnectionDetector ConnectionDetector1;
	
	Boolean InternetDetected = false;
	
	LocationTracker LocationTracker1;
	
	EditText text_radius;
	double radius_double;
	Intent Intent1;
	Intent Intent2;
	Button ButtonCurrent;
	Button ButtonAlt;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        
        CheckInternet ();
        CheckLocation ();
        
        // BUTTON TO SELECT CURRENT LOCATION
		ButtonCurrent = (Button) findViewById(R.id.button_current);
		ButtonCurrent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LocalSelectedLocationCurrent = true;
				GlobalVariables.SelectedLocationCurrent = LocalSelectedLocationCurrent;
				
				EditText text_radius = (EditText) findViewById (R.id.EnterRadius);
				radius_double = Double.parseDouble(text_radius.getText().toString());
				
			    Intent1 = new Intent(getApplicationContext(), ShowMap.class);
			    Intent1.putExtra("save_radius", Double.toString(radius_double));
			    
                startActivity(Intent1);
			}
		});
        
        // BUTTON TO SELECT ALTERNATE LOCATION
		ButtonAlt = (Button) findViewById(R.id.button_alt);
		ButtonAlt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {   
				LocalSelectedLocationCurrent = false;
				GlobalVariables.SelectedLocationCurrent = LocalSelectedLocationCurrent;
                
                // Get alternate location
                EditText EditText1 = (EditText)findViewById(R.id.editTextLocation);
                String LocationEntered = EditText1.getText().toString();
                GlobalVariables.LocationAlt = LocationEntered;
                
				EditText text_radius = (EditText) findViewById (R.id.EnterRadius);
				radius_double = Double.parseDouble(text_radius.getText().toString());
				
			    Intent2 = new Intent(getApplicationContext(), ShowMap.class);
			    Intent2.putExtra("save_radius", Double.toString(radius_double));
                startActivity(Intent2);
			}
 
		});

        
        
    }

	public void addButtonAlt() { 
		
		 
	}
	
	private void CheckInternet () {
		ConnectionDetector1 = new ConnectionDetector(getApplicationContext());
		
		// Check if Internet present
		InternetDetected = ConnectionDetector1.isConnectingToInternet();
		if (!InternetDetected) {
			// Internet Connection is not present
			Toast.makeText(getApplicationContext(), 
	                "WARNING: You are not connected to the Internet.", Toast.LENGTH_LONG).show();
			return;  // Stop executing code
		}
		else {
			Toast.makeText(getApplicationContext(), 
	                "Internet service: OK", Toast.LENGTH_LONG).show();
		}
	}
	
	private void CheckLocation () {
		LocationTracker1 = new LocationTracker (this); 
		
		if (LocationTracker1.canGetLocation ) {
			Toast.makeText(getApplicationContext(), 
	                "Location service: OK", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(getApplicationContext(), 
	                "WARNING: Location information is not available.\n" +
	                "Please check your GPS and/or network location services.", 
	                Toast.LENGTH_LONG).show();
			return;
		}
	}

}

// Global variables:
// http://codelikes.blogspot.com/2012/05/how-to-use-global-variables-in-android.html?zx=e184afc45965f370

// Example of the use of Google APIs: 
// http://www.androidhive.info/2012/08/android-working-with-google-places-and-maps-tutorial/
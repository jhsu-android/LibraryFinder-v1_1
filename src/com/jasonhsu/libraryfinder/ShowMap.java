package com.jasonhsu.libraryfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class ShowMap extends MapActivity {
	
	Boolean LocalSelectedLocationCurrent;
	
	// For getting location
	private LocationManager LocationManager1;
	private Location Location1;
	private LocationListener LocationListener1;
	private double LatDouble1, LongDouble1;
	private int LatInt1, LongInt1;
	
	// For getting and processing places
	ArrayList<HashMap<String, String>> PlacesListItems = new ArrayList<HashMap<String,String>>();
	public static String KEY_REFERENCE = "reference"; // ID of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_VICINITY = "vicinity"; // Place area name
	
	// For drawing the map
	private MapView MapView1;
	private MapController MapController1;
	private GeoPoint GeoPoint1;

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
		GetPlaces ();
		//ProcessPlaces ();
		DrawMap ();
	}
	
	private void ProcedureCurrentLocation () {
		Toast.makeText(getApplicationContext(), 
                "LOCATION: current", Toast.LENGTH_LONG).show();
		GetLocationCurrent ();
		GetPlaces ();
		//ProcessPlaces ();
		DrawMap ();
	}
	
	private void GetLocationAlt () {
		
	}
	
	private void GetLocationCurrent () {
		LocationManager1 = (LocationManager)getSystemService(
				Context.LOCATION_SERVICE);
		Location Location1 = LocationManager1.getLastKnownLocation
				(LocationManager1.GPS_PROVIDER);
		LatDouble1 = Location1.getLatitude();
		LongDouble1 = Location1.getLongitude();
		LatInt1 = (int)(LatDouble1*1000000);
		LongInt1 = (int)(LongDouble1*1000000);		
		//Toast.makeText(getApplicationContext(), 
                //String.valueOf(LatDouble1) + ',' + String.valueOf(LongDouble1),
                //Toast.LENGTH_LONG).show();
	}

	// Borrowed from 
	// http://p-xr.com/android-tutorial-how-to-parse-read-json-data-into-a-android-listview/
	private void GetPlaces () {
		// URL PARAMETERS
		// API Key below
		String API_KEY = "AIzaSyCRLa4LQZWNQBcjCYcIVYA45i9i8zfClqc";
		String types = "library";
		double radius=2000;
		String URL1 = "https://maps.googleapis.com/maps/api/place/search/json?";
		URL1 = URL1 + "key=" + API_KEY;
		URL1 = URL1 + "&location=" + LatDouble1 + "," + LongDouble1;
		URL1 = URL1 + "&radius=" + radius;
		URL1 = URL1 + "&sensor=false";
		URL1 = URL1 + "&types=" + types;
		
		InputStream InputStream1 = null;
		String result_str = "";
		JSONObject ArrayJSON = null;
		
		// Get information from the URL
		try{
			HttpClient HttpClient1 = new DefaultHttpClient();
			HttpPost HttpPost1 = new HttpPost(URL1);
			HttpResponse Response1 = HttpClient1.execute(HttpPost1);
			HttpEntity Entity1 = Response1.getEntity();
			InputStream1 = Entity1.getContent(); 
		}
		catch(Exception e){
			Log.e("log_tag", "Error in http connection "+e.toString());
		}
		
		// Convert InputStream1 to string
		try{
			BufferedReader Reader1 = new BufferedReader(new InputStreamReader(InputStream1,"iso-8859-1"),8);
			StringBuilder Builder1 = new StringBuilder();
			String line = null;
			while ((line = Reader1.readLine()) != null) {
				Builder1.append(line + "\n");
			}
			InputStream1.close();
			result_str = Builder1.toString();
		}
		
		catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
		}
		
		Log.i ("CHECK", result_str);
		
	    //try parse the string to a JSON object
		try {
			ArrayJSON = new JSONObject(result_str);
		}
		catch (JSONException e) {
			Log.e("log_tag", "Error parsing data "+e.toString());
		}
			 
			    

	}
	
	
	
		
	private void DrawMap () {
		//MapView1 = (MapView)findViewById(R.id.mapview1);
		//MapController1 = MapView1.getController();
		//GeoPoint GeoPoint1 = new GeoPoint (LatInt1, LongInt1);
		//MapController1.setCenter (GeoPoint1);
	}
		
	
}

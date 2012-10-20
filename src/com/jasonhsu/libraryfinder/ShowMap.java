package com.jasonhsu.libraryfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ShowMap extends MapActivity {
	
	Boolean LocalSelectedLocationCurrent;
	
	// For getting location
	private LocationManager LocationManager1;
	private Location Location1;
	private LocationListener LocationListener1;
	private double LatDouble1, LongDouble1;
	private int LatInt1, LongInt1;
	
	// For getting and processing places
	public static String KEY_INDEX = "index"; // ID of the place
	public static String KEY_REFERENCE = "reference"; // ID of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_ADDRESS = "vicinity"; // Place area name
	public static String KEY_LAT = "lat";
	public static String KEY_LNG = "lng";
	ArrayList<HashMap<String, String>> ResultList = new ArrayList<HashMap<String, String>>();
	
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
        
	}
	
	private void ProcedureAltLocation() {
		Toast.makeText(getApplicationContext(), 
                "LOCATION: alternate", Toast.LENGTH_LONG).show();
		GetLocationAlt ();
		GetPlaces ();
		DrawMap ();
		DrawMarkersPlaces ();
	}
	
	private void ProcedureCurrentLocation () {
		Toast.makeText(getApplicationContext(), 
                "LOCATION: current", Toast.LENGTH_LONG).show();
		GetLocationCurrent ();
		GetPlaces ();
		DrawMap ();
		DrawMarkersPlaces ();
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
	}

	// Borrowed from 
	// http://p-xr.com/android-tutorial-how-to-parse-read-json-data-into-a-android-listview/
	// http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
	private void GetPlaces () {
		// URL PARAMETERS
		// API Key below
		String API_KEY = "AIzaSyCRLa4LQZWNQBcjCYcIVYA45i9i8zfClqc";
		String types = "library";
		double radius=32186.8;
		//double radius=1000;
		String URL1 = "https://maps.googleapis.com/maps/api/place/search/json?";
		URL1 = URL1 + "key=" + API_KEY;
		URL1 = URL1 + "&location=" + LatDouble1 + "," + LongDouble1;
		URL1 = URL1 + "&radius=" + radius;
		URL1 = URL1 + "&sensor=false";
		URL1 = URL1 + "&types=" + types;
		
		InputStream InputStream1 = null;
		String json_str = "";
		JSONObject JSONObject1 = null;
		
		// GetPlaces Step 1: Get input data stream from the URL.
		// Input: URL
		// Output: InputStream1
		try{
			HttpClient HttpClient1 = new DefaultHttpClient();
			HttpPost HttpPost1 = new HttpPost(URL1);
			HttpResponse Response1 = HttpClient1.execute(HttpPost1);
			HttpEntity Entity1 = Response1.getEntity();
			InputStream1 = Entity1.getContent(); 
		}
		catch(Exception e){
			Log.e("GetPlaces 1", "Error in http connection "+e.toString());
		}
		
		// GetPlaces Step 2: Convert the downloaded input stream into a string. 
		// Input: InputStream1
		// Output: json_str
		try{
			BufferedReader Reader1 = new BufferedReader(new InputStreamReader(InputStream1,"iso-8859-1"),8);
			StringBuilder Builder1 = new StringBuilder();
			String line = null;
			while ((line = Reader1.readLine()) != null) {
				Builder1.append(line + "\n");
			}
			InputStream1.close();
			json_str = Builder1.toString();
		}
		
		catch(Exception e){
			Log.e("GetPlaces 2", "Error converting result "+e.toString());
		}
		
		Log.i ("CHECK 1", json_str);
		
		// GetPlaces Step 3: Parse the data stream into a JSON object.  
		// Input: json_str
		// Output: JSONObject1
		
		try {
			JSONObject1 = new JSONObject(json_str);
		}
		catch (JSONException e) {
			Log.e("GetPlaces 3", "Error parsing data "+e.toString());
		}
		
		
		// GetPlaces Step 4: Collect the desired data from the JSON object and
		// place the information in an array.
		// Borrowed from
		// http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
		// Input: JSONObject1
		// Output: ResultList
			 
		try {
			JSONArray JSONresults = null;
			JSONresults = JSONObject1.getJSONArray("results"); 
			ResultList.clear();
			
			for (int i = 0; i < JSONresults.length (); i++) {	
				JSONObject r = JSONresults.getJSONObject(i);
				String FieldIndex = String.valueOf(i);
				String FieldName = r.getString(KEY_NAME);
				String FieldAddress = r.getString(KEY_ADDRESS);
				
				JSONObject JSONgeometry = r.getJSONObject("geometry");
				JSONObject JSONlocation = JSONgeometry.getJSONObject("location");
				String FieldLat = JSONlocation.getString(KEY_LAT);
				String FieldLng = JSONlocation.getString(KEY_LNG);
				
				HashMap<String, String> HashMap1 = new HashMap<String, String>();
				HashMap1.put ("index", FieldIndex);
				HashMap1.put (KEY_NAME, FieldName);
				HashMap1.put (KEY_ADDRESS, FieldAddress);
				HashMap1.put (KEY_LAT, FieldLat);
				HashMap1.put (KEY_LNG, FieldLng);
				
				ResultList.add (HashMap1);
			}
			
		}
		catch (Exception e) {
			Log.e ("GetPlaces 4", "Failed to process JSONObject1");
		}
	}
		
	private void DrawMap () {
		MapView1 = (MapView)findViewById(R.id.mapview1);
		MapController1 = MapView1.getController();
		GeoPoint GeoPoint1 = new GeoPoint (LatInt1, LongInt1);
		MapController1.setCenter (GeoPoint1);
		MapView1.setBuiltInZoomControls(true); // Add zoom control
        MapController1.setZoom(15); // Set zoom level
        

		
		//Log.i ("DrawMap", String.valueOf(list_length));

	}
		
	
	
	private void DrawMarkersPlaces () { 
		Drawable MarkerPlaces = getResources().getDrawable(R.drawable.marker);
		MarkerPlaces.setBounds( (int) (-MarkerPlaces.getIntrinsicWidth()/2),
				(int) (-MarkerPlaces.getIntrinsicHeight()/2),
				(int) (MarkerPlaces.getIntrinsicWidth()/2),
				(int) (MarkerPlaces.getIntrinsicHeight()/2)
				);
		PlacesFound PlacesFound1 = new PlacesFound (MarkerPlaces);
		MapView1.getOverlays().add(PlacesFound1);
	}
	
	
	// Based on the example on pages 608-610 in the book _Pro Android 4_
	class PlacesFound extends ItemizedOverlay {
		
		
		private ArrayList<OverlayItem> places =
				new ArrayList<OverlayItem>();

		public PlacesFound (Drawable marker_local) {
			super(marker_local);
			// TODO Auto-generated constructor stub
			
	        try {
	        	HashMap<String, String> HashMap1 = new HashMap<String, String>();
	    		int list_length = ResultList.size();
	    		for (int i = 0; i < list_length; i++) {
	    			HashMap1 = ResultList.get(i);
	    			String Name = HashMap1.get(KEY_NAME);
	    			String Address = HashMap1.get(KEY_ADDRESS);
	    			String LatStr = HashMap1.get(KEY_LAT);
	    			String LngStr = HashMap1.get(KEY_LNG);
	    			double LatDouble = Double.valueOf(LatStr);
	    			double LngDouble = Double.valueOf(LngStr);
	    			int LatInt = (int)(LatDouble*1000000);
	    			int LngInt = (int)(LngDouble*1000000);
	    			GeoPoint GeoPointPlace = new GeoPoint (LatInt, LngInt);
	    			places.add(new OverlayItem (GeoPointPlace, Name, Name));
	    			populate();
	    			//Log.i ("DrawMap", Element1);
	    		}
	        }
	        catch (Exception e) {
	        	Log.e ("MarkersPlaces", "ERROR: Could not extract places data");
	        }
			
		}

		@Override
		protected OverlayItem createItem(int arg0) {
			// TODO Auto-generated method stub
			return places.get(arg0);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return places.size();
		}
		
	}
	
}

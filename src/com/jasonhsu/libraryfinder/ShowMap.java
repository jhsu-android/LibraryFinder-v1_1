package com.jasonhsu.libraryfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class ShowMap extends MapActivity implements LocationListener {
	
	Boolean LocalSelectedLocationCurrent;
	double radius;
	
	// For getting location
	private double LatDouble1, LongDouble1;
	private int LatInt1, LongInt1;
	
	// For getting current location
	private LocationManager LocationManager1;
	private String provider;

	
	// For getting location (alternate only)
	String LocationString;
	
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
	
	// For drawing the icon at the current location
	MyLocationOverlay MyLocationOverlay1 = null;

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
        
        Intent Intent1 = getIntent();
        String radius_str = Intent1.getStringExtra("save_radius");
        Log.i ("ShowMap OnCreate", radius_str);
        double radius_miles = Double.parseDouble(radius_str);
    	double radius_meters = radius_miles * 1609.34; 
    	radius = radius_meters * 1.73; // Fudge factor due to rectangular screen
    	Log.i ("ShowMap OnCreate", String.valueOf(radius));

        
        LocalSelectedLocationCurrent = GlobalVariables.SelectedLocationCurrent;

        if (LocalSelectedLocationCurrent == false) {
        	ProcedureAltLocation ();
        }
        else {
        	ProcedureCurrentLocation ();
        }
        
	}
	
	private void ProcedureAltLocation() {
		GetLocationAlt ();
		GetPlaces ();
		DrawMap ();
		DrawMarkersPlaces ();
	}
	
	private void ProcedureCurrentLocation () {
		GetLocationCurrent ();
		GetPlaces ();
		DrawMap ();
		DrawMarkersPlaces ();
		DrawMarkerHere ();
	}
	
	private void GetLocationAlt () {
		LocationString = GlobalVariables.LocationAlt;
		Geocoder Geocoder1 = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses =
            		Geocoder1.getFromLocationName(LocationString, 5);
            LatDouble1 = addresses.get(0).getLatitude();
            LongDouble1 = addresses.get(0).getLongitude();
        	LatInt1 = (int)(LatDouble1*1000000);
        	LongInt1 = (int)(LongDouble1*1000000); 
        } 
        catch (Exception e) {
        	Toast.makeText(getApplicationContext(), 
        			"The location you entered could not be found.  " +
        			"Your current location is being shown instead."
        			, Toast.LENGTH_LONG).show();
        	// Run the Intro.java script
        	ProcedureCurrentLocation ();
        }
	}
	
	private void GetLocationCurrent () {
		GetLocationGPS ();
	}
	
	// Based on
	// http://www.vogella.com/articles/AndroidLocationAPI/article.html#tutlocationapi
	private void GetLocationGPS () {
		// Get the location manager
	    LocationManager1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    Criteria Criteria1 = new Criteria();
	    provider = LocationManager1.getBestProvider(Criteria1, false);
	    Location Location1 = LocationManager1.getLastKnownLocation(provider);
		LocationManager1 = (LocationManager)getSystemService(
				Context.LOCATION_SERVICE);
		LatDouble1 = Location1.getLatitude();
		LongDouble1 = Location1.getLongitude();
		LatInt1 = (int)(LatDouble1*1000000);
		LongInt1 = (int)(LongDouble1*1000000); 
	}

	// Borrowed from 
	// http://p-xr.com/android-tutorial-how-to-parse-read-json-data-into-a-android-listview/
	// http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
	private void GetPlaces () {
		// Get search radius
        //radius_str = GlobalVariables.radius_entered;
        //Log.i ("GetPlaces", radius_str);

		
		// URL PARAMETERS
		// API Key below
		String API_KEY = "AIzaSyCRLa4LQZWNQBcjCYcIVYA45i9i8zfClqc";
		String types = "library";
		String URL1 = "https://maps.googleapis.com/maps/api/place/search/json?";
		URL1 = URL1 + "key=" + API_KEY;
		URL1 = URL1 + "&location=" + LatDouble1 + "," + LongDouble1;
		URL1 = URL1 + "&radius=" + String.valueOf(radius);
		URL1 = URL1 + "&sensor=false";
		URL1 = URL1 + "&types=" + types;
		
		// URL for location 55402 and radius of 5 miles
		// https://maps.googleapis.com/maps/api/place/search/json?key=AIzaSyCRLa4LQZWNQBcjCYcIVYA45i9i8zfClqc&location=44.975922,-93.272186&radius=12926.5&sensor=false&types=library
		
		Log.i ("GetPlaces - URL", URL1);
		Log.i ("GetPlaces - URL", String.valueOf(radius));
		
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
		
		GeoPoint CenterPoint1 = PlacesFound1.getCenterPoint();
		int LatSpan1 = PlacesFound1.getLatSpanE6();
		int LngSpan1 = PlacesFound1.getLonSpanE6();
		
		MapController1.setCenter (CenterPoint1);
		MapController1.zoomToSpan((int)(LatSpan1 * 1.5), (int) (LngSpan1 * 1.5));
	}
	
	private void DrawMarkerHere () {
		// MapView1.getOverlays().add(MyLocationOverlay1);
	}
	
	// Based on the example on pages 608-610 in the book _Pro Android 4_
	class PlacesFound extends ItemizedOverlay {
		
		private ArrayList<OverlayItem> places =
				new ArrayList<OverlayItem>();
		private GeoPoint center = null;

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
	    			places.add(new OverlayItem (GeoPointPlace, Name, Address));
	    			populate();
	    			//Log.i ("DrawMap", Element1);
	    		}
	        }
	        catch (Exception e) {
	        	Log.e ("MarkersPlaces", "ERROR: Could not extract places data");
	        }
			
		}
		
		public GeoPoint getCenterPoint () {
			if (center == null) {
				int northEdge = -90000000;
				int southEdge = 90000000;
				int eastEdge = -180000000;
				int westEdge = 180000000;
		        try {
		        	HashMap<String, String> HashMap1 = new HashMap<String, String>();
		    		int list_length = ResultList.size();
		    		for (int i = 0; i < list_length; i++) {
		    			HashMap1 = ResultList.get(i);
		    			String LatStr = HashMap1.get(KEY_LAT);
		    			String LngStr = HashMap1.get(KEY_LNG);
		    			double LatDouble = Double.valueOf(LatStr);
		    			double LngDouble = Double.valueOf(LngStr);
		    			int LatInt = (int)(LatDouble*1000000);
		    			int LngInt = (int)(LngDouble*1000000);
		    			if (LatInt > northEdge)
		    				northEdge = LatInt;
		    			if (LatInt < southEdge)
		    				southEdge = LatInt;
		    			if (LngInt > eastEdge)
		    				eastEdge = LngInt;
		    			if (LngInt < westEdge)
		    				westEdge = LngInt;
		    			Log.i ("PlacesFound", LatStr);
		    			Log.i ("PlacesFound", LngStr);
		    		}
		    		int LatCenter = (int)((northEdge + southEdge)/2);
		    		int LngCenter = (int)((westEdge + eastEdge)/2);
		    		center = new GeoPoint (LatCenter, LngCenter);
		    		Log.i ("PlacesFound", String.valueOf(northEdge));
		    		Log.i ("PlacesFound", String.valueOf(southEdge));
		    		Log.i ("PlacesFound", String.valueOf(eastEdge));
		    		Log.i ("PlacesFound", String.valueOf(westEdge));
		        }
		        catch (Exception e) {
		        	Log.e ("MarkersPlaces", "ERROR: Could not extract places data");
		        }
				center = new GeoPoint ((int)((northEdge + southEdge)/2),
						(int)((westEdge + eastEdge)/2));
			}
			return center;
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


	@Override
	public void onLocationChanged(Location location) {
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
	
}

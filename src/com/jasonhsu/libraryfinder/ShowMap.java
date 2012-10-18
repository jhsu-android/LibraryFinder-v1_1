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
	
	// For adding markers for the places found
	PlacesToPlot PlacesToPlot1;

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
		//DrawMarkersPlaces ();
	}
	
	private void ProcedureCurrentLocation () {
		Toast.makeText(getApplicationContext(), 
                "LOCATION: current", Toast.LENGTH_LONG).show();
		GetLocationCurrent ();
		GetPlaces ();
		DrawMap ();
		//DrawMarkersPlaces ();
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
	// and
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
		JSONObject ArrayJSON = null;
		
		
		
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
			Log.e("log_tag", "Error in http connection "+e.toString());
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
			Log.e("log_tag", "Error converting result "+e.toString());
		}
		
		Log.i ("CHECK", json_str);
		
		// GetPlaces Step 3: Parse the data stream into a JSON object.  
		// Input: json_str
		// Output: ArrayJSON
		try {
			ArrayJSON = new JSONObject(json_str);
		}
		catch (JSONException e) {
			Log.e("log_tag", "Error parsing data "+e.toString());
		}
		
		
		// GetPlaces Step 4: Collect the desired data from the JSON object and
		// place the information in arrays.
		// Based on http://www.androidcompetencycenter.com/2009/10/json-parsing-in-android/
		// Input: ArrayJSON
		// Output: 
			 
		try {
			JSONObject ObjectStatus = ArrayJSON.getJSONObject("status");
			String FieldStatus = ObjectStatus.getString("status");
			
			JSONObject ObjectResult = ArrayJSON.getJSONObject("results");
			
			ResultList.clear();
			for (int i = 0; i < ObjectResult.length (); i++) {
				HashMap<String, String> HashMap1 = new HashMap<String, String>();
				
				String FieldName = ObjectResult.getString(KEY_NAME);
				String FieldAddress = ObjectResult.getString(KEY_ADDRESS);
				String FieldReference = ObjectResult.getString(KEY_REFERENCE);
				JSONObject ObjectGeometry = ObjectResult.getJSONObject("geometry");
				JSONObject ObjectLocation = ObjectGeometry.getJSONObject("location");
				JSONObject ObjectLat = ObjectLocation.getJSONObject(KEY_LAT);
				JSONObject ObjectLng = ObjectLocation.getJSONObject(KEY_LNG);
				String FieldLat = ObjectLat.getString(KEY_LAT);
				String FieldLng = ObjectLng.getString(KEY_LNG);
				
				HashMap1.put (KEY_REFERENCE, FieldReference);
				HashMap1.put (KEY_NAME, FieldName);
				HashMap1.put (KEY_ADDRESS, FieldAddress);
				HashMap1.put (KEY_LAT, FieldLat);
				HashMap1.put (KEY_LNG, FieldLng);
				
				ResultList.add (HashMap1);
			}
			
		}
		catch (Exception e) {
			Log.e ("log_tag", "No results found");
		}
	}
	
	
	
		
	private void DrawMap () {
		MapView1 = (MapView)findViewById(R.id.mapview1);
		MapController1 = MapView1.getController();
		GeoPoint GeoPoint1 = new GeoPoint (LatInt1, LongInt1);
		MapController1.setCenter (GeoPoint1);
		MapView1.setBuiltInZoomControls(true); // Add zoom control
        MapController1.setZoom(15); // Set zoom level
	}
		
	class PlacesToPlot extends ItemizedOverlay {
		
		private ArrayList<OverlayItem> PlacesToPlot =
				new ArrayList<OverlayItem> ();
		

		public PlacesToPlot(Drawable marker_local) {
			super(marker_local);
			// TODO Auto-generated constructor stub
			for (int i=1; i < ResultList.size(); i++) {
				HashMap<String, String> HashMap1 = new HashMap<String, String>();
				HashMap1 = ResultList.get(i);
				String PlaceName = HashMap1.get(KEY_NAME);
				String PlaceAddress = HashMap1.get(KEY_ADDRESS);
				
				String PlaceLatStr = HashMap1.get(KEY_LAT);
				String PlaceLongStr = HashMap1.get(KEY_LNG);
				double PlaceLatDouble = Double.valueOf(PlaceLatStr);
				double PlaceLongDouble = Double.valueOf(PlaceLongStr);
				int PlaceLatInt = (int)(PlaceLatDouble*1000000);
				int PlaceLongInt = (int)(PlaceLongDouble*1000000);
				GeoPoint PlaceGeoPoint = new GeoPoint (PlaceLatInt, PlaceLongInt);
				
				PlacesToPlot.add(new OverlayItem (PlaceGeoPoint, PlaceName, PlaceAddress));
				populate ();

			}
			
		}

		@Override
		protected OverlayItem createItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	private void DrawMarkersPlaces () { 
		Drawable marker1 = getResources().getDrawable(R.drawable.marker);
		PlacesToPlot1 = new PlacesToPlot (marker1);
		MapView1.getOverlays().add(PlacesToPlot1);
		//PlacesToPlot.clear();
		//for (int i=1; i < ResultList.size(); i++) {
			//HashMap<String, String> HashMap1 = new HashMap<String, String>();
			//HashMap1 = ResultList.get(i);
			//String PlaceName = HashMap1.get(KEY_NAME);
			//String PlaceAddress = HashMap1.get(KEY_ADDRESS);
			
			//String PlaceLatStr = HashMap1.get(KEY_LAT);
			//String PlaceLongStr = HashMap1.get(KEY_LNG);
			//double PlaceLatDouble = Double.valueOf(PlaceLatStr);
			//double PlaceLongDouble = Double.valueOf(PlaceLongStr);
			//int PlaceLatInt = (int)(PlaceLatDouble*1000000);
			//int PlaceLongInt = (int)(PlaceLongDouble*1000000);
			//GeoPoint PlaceGeoPoint = new GeoPoint (PlaceLatInt, PlaceLongInt);
			
			//PlacesToPlot.add(new OverlayItem (PlaceGeoPoint, PlaceName, PlaceAddress));
		//}
		//MapView1.getOverlays().add(PlacesToPlot);
		
	}
	
}

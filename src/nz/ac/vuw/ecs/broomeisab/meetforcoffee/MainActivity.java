package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private String username;
	private boolean updatesActive;
	private FeedInputStreamLoader inputStreamLoader;
	private XMLPullFeedParser xmlParser;

	private static MyLocationListener locationListener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		locationListener = new MyLocationListener();
		this.inputStreamLoader = new FeedInputStreamLoader();
		xmlParser = new XMLPullFeedParser();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


    /**
     * Called when the user clicks the Send button
     * @param view
     */
    public void signIn(View view){

    	// gets the EditText
    	EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);

    	// gets the message from the EditText
    	this.username = editTextUsername.getText().toString();

    	if(username.equals("")){
    		return;
    	}

    	// change the screen
    	setContentView(R.layout.main_menu);

    	Log.d("", "logged in as " + username);

    	//toggleLocationUpdates();
    	getGPSLocation();
    }


    public void manageFriends(View view){
    	Log.d("", "manage friends started");
    	
    	
    	String xml = "<ns:GetInvitationUpdatesResponse xmlns:ns=\"http://stockquoteservice/xsd\"><ns:return><result><invitationUpdates><friendRequests></friendRequests></result></ns:return></ns:GetInvitationUpdatesResponse>";
    	
    	InputStream is = inputStreamLoader.getFeedInputStream("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetInvitationUpdates?username=bill");
    	Log.d("", "loaded input stream, parsing input");
    	Requests requests = xmlParser.parsePendingRequests(is);
    	Log.d("", "parsed input: " + requests.friends.size());	
    	Log.d("", "friend request: " + requests.friends.get(0));
    	
    	
//    	try {
//			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
//	    	Log.d("", "loaded input stream, parsing input");
//	    	Requests requests = xmlParser.parsePendingRequests(is);
//	    	Log.d("", "parsed input: " + requests.friends.size());					
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	

    	
    }


    public void inviteForCoffee(View view){
    	Log.d("", "invite for coffee started");
    }

    
    public void getRequestUpdates(){
    	
    	
    }
    
    
    
    
    
    //==========================================================================
    // update location stuff
    //==========================================================================
    
    
    
	public void getGPSLocation(){

		// get location
//		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	private class MyLocationListener implements LocationListener {
		  public void onLocationChanged(Location location) {
			  Log.d("", "update received");

			  // call our webservice
			  updateLocation(location);
		  }

		  public void onProviderDisabled(String provider){
			  Log.d("", "provider disabled");
		  }
		  public void onProviderEnabled(String provider) {
			  Log.d("", "provider enabled");
		  }

		  @Override
		  public void onStatusChanged(String provider, int status, Bundle extras) {
			  Log.d("", "provider state changed");
		  }
	};

	protected void updateLocation(Location location){
		Log.d("", "received location update");

		if (location == null)
			return;

		// update our location here
		double lat = location.getLatitude();
		double lon = location.getLongitude();

		Log.d("", "lat: " + lat + ", lon: " + lon);
	}

}

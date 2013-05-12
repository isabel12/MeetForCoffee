package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Cafe;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.User;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity {

	// for main bit
	private double lat = 0;
	private double lon = 0;
	
	// for invites
	List<User> friends;
	List<Cafe> cafes;
	
	private boolean updatesActive;
	private FeedInputStreamLoader inputStreamLoader;
	private XMLPullFeedParser xmlParser;

	private static MyLocationListener locationListener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		locationListener = new MyLocationListener();
		this.inputStreamLoader = new FeedInputStreamLoader();
		xmlParser = new XMLPullFeedParser();
		
		
		if (LoginInfo.username == null){
			setContentView(R.layout.login);
			
		} else {
			setContentView(R.layout.main_menu);
			getGPSLocation();
				
			// disable buttons depending on if in group or not
			if(LoginInfo.groupId != 0){
				Button inviteForCoffeeButton = (Button)findViewById(R.id.invite_for_coffee_button);
				inviteForCoffeeButton.setClickable(false);
				inviteForCoffeeButton.setEnabled(false);
			}
			
			// disable group status button depending on if group or not
			if(LoginInfo.groupId == 0){
				Button inviteForCoffeeButton = (Button)findViewById(R.id.view_group_status_button);
				inviteForCoffeeButton.setClickable(false);
				inviteForCoffeeButton.setEnabled(false);
			}
			
		}
		
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
    	LoginInfo.username = editTextUsername.getText().toString();

    	if(LoginInfo.username.equals("")){
    		LoginInfo.username = null;
    		return;
    	}

    	// change the screen
    	setContentView(R.layout.main_menu);
    	
    	// disable the view status button
		Button inviteForCoffeeButton = (Button)findViewById(R.id.view_group_status_button);
		inviteForCoffeeButton.setClickable(false);
		inviteForCoffeeButton.setEnabled(false);

    	Log.d("", "logged in as " + LoginInfo.username);

    	getGPSLocation();
    }


    
   
    
    
    
    
    
    public void manageFriends(View view){
    	Log.d("", "manage friends started");
//    		
//    	String xml = "<ns:GetInvitationUpdatesResponse xmlns:ns=\"http://stockquoteservice/xsd\"><ns:return><result><invitationUpdates><friendRequests></friendRequests></result></ns:return></ns:GetInvitationUpdatesResponse>";
//    	
//    	InputStream is = inputStreamLoader.getFeedInputStream("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetInvitationUpdates?username=bill");
//    	Log.d("", "loaded input stream, parsing input");
//    	Requests requests = xmlParser.parsePendingRequests(is);
//    	Log.d("", "parsed input: " + requests.friends.size());	
//    	Log.d("", "friend request: " + requests.friends.get(0));
    }
    
    
    
    


    
    
    
    public void inviteForCoffee(View view){
    	Log.d("", "invite for coffee started");
    	
    	loadFriends();
    	loadCafes();
    	
    	setInviteView();
    	
//    	// the intent binds this view to the other view we want to display
//    	Intent intent = new Intent(this, InviteActivity.class);
//    	intent.putExtra(InviteActivity.LAT, lat);
//    	intent.putExtra(InviteActivity.LON, lon);
//
//    	// start the new activity
//    	startActivity(intent);
    }

    
    public void viewGroupStatus(View view){
    	Intent intent = new Intent(this, GroupStatusActivity.class);
    	startActivity(intent);
    }
        
    
    
    //======================================================
    // private methods to help with group_invitation page
    //======================================================
    
	private void loadFriends(){
		
		// get input

		InputStream is = inputStreamLoader.getFeedInputStream("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetAllFriendsLocations?username=" + LoginInfo.username);
		friends = xmlParser.parseFriendLocations(is);

		Log.d("", "loaded friend: " + friends.get(0));
		
		// fake it
		//friends = new ArrayList<User>();
		//friends.add(new User("Jenny", new nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Location(-41.292112, 174.766432)));	
	}
	
	private void loadCafes(){
		
		// get input
		try {
			InputStream is = inputStreamLoader.getFeedInputStream("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetCloseByCafes?username=" + LoginInfo.username);
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// fake it
		cafes = new ArrayList<Cafe>();
		cafes.add(new Cafe("", "Vic Books", new nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Location(-41.288610, 174.768405)));
	}
	
	private void setInviteView(){
		setContentView(R.layout.activity_invite);
		
		// friends
		Spinner friendsSpinner = (Spinner) findViewById(R.id.friends_spinner);
		List<String> list = new ArrayList<String>();
		for(User friend: this.friends){
			list.add(friend.GetUsername());
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		friendsSpinner.setAdapter(dataAdapter);
		
		// cafes
		Spinner cafesSpinner = (Spinner) findViewById(R.id.cafes_spinner);
		List<String> list2 = new ArrayList<String>();
		for(Cafe cafe: this.cafes){
			list2.add(cafe.name);
		}

		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list2);
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cafesSpinner.setAdapter(dataAdapter2);
	}
    
    
	public void backToMainMenu(View view){
		setContentView(R.layout.main_menu);
	}
    
    /**
     * The button that calls this method is only available from activity_invite.
     * @param view
     */
    public void inviteToMeet(View view){
    	Log.d("","invite to meet entered");
    	
    	if(LoginInfo.groupId != 0){
    		Log.d("","groupId is not zero: " + LoginInfo.groupId);
    		return;
    	}
    	
    	
    	// create a group
    	Spinner friendsSpinner = (Spinner) findViewById(R.id.friends_spinner);
    	int friendId = friendsSpinner.getSelectedItemPosition();
    	String friendName = friends.get(friendId).GetUsername();
    	
    	Spinner cafeSpinner = (Spinner) findViewById(R.id.cafes_spinner);
    	int cafeId = cafeSpinner.getSelectedItemPosition();
    	String cafeXML = cafes.get(cafeId).name; // todo change to xml!
    	
    	
    	// send group create request
    	int groupId = 1; // this will be parsed and checked for failure and wotnot
    	
    	
    	// put all info into intent
    	Cafe cafe = cafes.get(cafeId);
    	
    	LoginInfo.groupId = groupId;
    	LoginInfo.cafeName = cafe.name;
    	LoginInfo.cafeLocation = new Location("cafe");
    	LoginInfo.cafeLocation.setLatitude(cafe.location.getLat());
    	LoginInfo.cafeLocation.setLongitude(cafe.location.getLon());
    	
    	Log.d("","set group info");
    	
    	Intent intent = new Intent(this, GroupStatusActivity.class);   	
    	this.startActivity(intent);
    }
    
    
    //==========================================================================
    // update location stuff
    //==========================================================================
    
    
    
	public void getGPSLocation(){

		// get location
//		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
		
		// fake it
		Location location = new Location("gps");
		location.setLatitude(-41.288020); // von zedlitz i think...
		location.setLongitude(174.768008);
		
		updateLocation(location);
	}

	private class MyLocationListener implements LocationListener {
		  public void onLocationChanged(android.location.Location location) {
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
		lat = location.getLatitude();
		lon = location.getLongitude();

		// update location
		inputStreamLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/UpdateLocation?username=%s&lat=%f&lon=%f", LoginInfo.username, lat, lon));
		
		Log.d("", "lat: " + lat + ", lon: " + lon);
	}

}

package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.Cafe;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.User;

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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	// for main bit
	private double lat = 0;
	private double lon = 0;
	
	// for invites
	List<User> friends;
	List<Cafe> cafes;
	
	private boolean updatesActive;
	private InputStreamLoader inputStreamLoader;
	private XMLPullFeedParser xmlParser;

	private static MyLocationListener locationListener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		locationListener = new MyLocationListener();
		this.inputStreamLoader = new InputStreamLoader();
		xmlParser = new XMLPullFeedParser();
			
		if (ApplicationState.username == null){
			setContentView(R.layout.login);
			
		} else {
			signIn();	
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


    /**
     * Called when the user clicks the 'Sign in' button on the login screen.
     * @param view
     */
    public void signIn(View view){

    	// gets the EditText
    	EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);

    	// gets the message from the EditText
    	ApplicationState.username = editTextUsername.getText().toString();

    	if(ApplicationState.username.equals("")){
    		ApplicationState.username = null;
    		return;
    	}
    	
		// register on server just in case
		InputStream is = inputStreamLoader.getFeedInputStream("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/Register?username=" + ApplicationState.username);
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		Log.d("", "logged in as " + ApplicationState.username);
		
    	signIn();
    }
    
    /**
     * This method is called when the username is valid.  It gets the groupID, disables buttons, starts updating GPS location, and displays the Main Menu View.
     */
    public void signIn(){
    	  	
    	// change the screen
    	setContentView(R.layout.main_menu);
    	
    	// get group id from the server
    	InputStream is = inputStreamLoader.getFeedInputStream("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetActiveGroup?username=" + ApplicationState.username);
    	ApplicationState.groupId = xmlParser.parseGroupId(is);
    	
    	if(ApplicationState.groupId == 0){
    	
	    	// disable the view status button
			Button viewGroupStatusButton = (Button)findViewById(R.id.view_group_status_button);
			viewGroupStatusButton.setClickable(false);
			viewGroupStatusButton.setEnabled(false);
    	} else {
	    	// disable the invite button
			Button inviteForCoffeeButton = (Button)findViewById(R.id.invite_for_coffee_button);
			inviteForCoffeeButton.setClickable(false);
			inviteForCoffeeButton.setEnabled(false);	
    	}

    	refreshPage();

    	// start updating location
    	getGPSLocation(ApplicationState.getTimeBetweenLocationUpdates());
    }
    
    
    //======================================================
    // Buttons on the main menu view
    //======================================================
    
    public void inviteForCoffee(View view){
    	Log.d("", "invite for coffee started");
    	
    	loadFriends();
    	loadCafes();
    	
    	setInviteView();
    }

    
    public void viewGroupStatus(View view){
    	Intent intent = new Intent(this, GroupStatusActivity.class);
    	startActivity(intent);
    }
        
    
    public void addFriend(View view){
    	addFriend();	
    }
    
    //======================================================
    // private methods to help with main menu page
    //======================================================
    
    public void addFriend(){
    	EditText editText = (EditText)findViewById(R.id.enter_username);
    	
    	String username = editText.getText().toString();
    	editText.setText("");
    	
    	if(username.equals("") || username.equals(ApplicationState.username)){
    		return;
    	}
    	
    	InputStream is = inputStreamLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/AddFriend?username=%s&toInvite=%s", ApplicationState.username, username));
    	//TODO parse input
    	try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void refreshPage(){	
    	// get friends
    	InputStream is = inputStreamLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetAllFriendsLocations?username=%s", ApplicationState.username));
    	List<User> friends = xmlParser.parseFriendLocations(is);
    	
    	// display friends
		List<String> friendList = new ArrayList<String>();
		for(User friend: friends){
			friendList.add(friend.GetUsername());
		}
	
    	ArrayAdapter<String> friendsAdapter =
        		new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendList);
        	ListView myList = (ListView)findViewById(R.id.friends_list);
            myList.setAdapter(friendsAdapter);
			
    }
    
    
    //======================================================
    // private methods to help with group_invitation page
    //======================================================
    
    /**
     * Loads friends locations.
     */
	private void loadFriends(){
		
		// get input
		InputStream is = inputStreamLoader.getFeedInputStream("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetAllFriendsLocations?username=" + ApplicationState.username);
		friends = xmlParser.parseFriendLocations(is);

		Log.d("", "loaded friend: " + friends.get(0));
	}
	
	/**
	 * Loads nearby cafes.
	 */
	private void loadCafes(){		
		// get input
		InputStream is = inputStreamLoader.getFeedInputStream("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetCloseByCafes?username=" + ApplicationState.username);
		cafes = xmlParser.parseCafes(is);	
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
    	
    	if(ApplicationState.groupId != 0){
    		Log.d("","groupId is not zero: " + ApplicationState.groupId);
    		return;
    	}
    	   	
    	// create a group
    	Spinner friendsSpinner = (Spinner) findViewById(R.id.friends_spinner);
    	int friendId = friendsSpinner.getSelectedItemPosition();
    	String friendName = friends.get(friendId).GetUsername();
    	
    	Spinner cafeSpinner = (Spinner) findViewById(R.id.cafes_spinner);
    	int cafeId = cafeSpinner.getSelectedItemPosition();
    	Cafe cafe = cafes.get(cafeId); // todo change to xml!
    		
    	String cafeName = "";
		try {
			cafeName = URLEncoder.encode(cafe.name, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
    	
    	// send group create request
    	InputStream is = inputStreamLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/InviteFriendToMeet?username=%s&toInvite=%s&cafeID=%s&cafeName=%s&cafeLat=%f&cafeLon=%f", ApplicationState.username, friendName, cafe.id, cafeName, cafe.location.getLat(), cafe.location.getLon()));
    	ApplicationState.groupId = xmlParser.parseGroupId(is);
 	
    	// if it failed, it is because you are already in a group - get the number.
    	if(ApplicationState.groupId == 0){
    		is = inputStreamLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetActiveGroup?username=%s", ApplicationState.username));
    		ApplicationState.groupId = xmlParser.parseGroupId(is);
    	}	
    	
    	// cheat and make them accept straight away
//    	try {
//    		is = inputStreamLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/AcceptGroupInvitation?username=%s&groupID=%d", friendName, groupId));
//			is.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    	 	
    	// put all info into ApplicationState
    	ApplicationState.cafeName = cafe.name;
    	ApplicationState.cafeLocation = new Location("cafe");
    	ApplicationState.cafeLocation.setLatitude(cafe.location.getLat());
    	ApplicationState.cafeLocation.setLongitude(cafe.location.getLon());	
    	Log.d("","set group info");
    	
    	// start more frequent updates
    	getGPSLocation(ApplicationState.getTimeBetweenLocationUpdates());
    	
    	// start GroupStatus Activity
    	Intent intent = new Intent(this, GroupStatusActivity.class);   	
    	this.startActivity(intent);
    }
    
    
    //==========================================================================
    // update location stuff
    //==========================================================================
    
    
    
	public void getGPSLocation(int minMillisBetweenUpdates){

		Log.d("", "Now receiving GPS updates every " + (minMillisBetweenUpdates / 1000) + " seconds.");
		
		// get location
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(locationListener);
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minMillisBetweenUpdates, 0, locationListener);
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minMillisBetweenUpdates, 0, locationListener);
		
		
		
//		// fake it
//		Location location = new Location("gps");
//		location.setLatitude(-41.288020); // von zedlitz i think...
//		location.setLongitude(174.768008);
//		
//		updateLocation(location);
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
		inputStreamLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/UpdateLocation?username=%s&lat=%f&lon=%f", ApplicationState.username, lat, lon));
		
		Log.d("", "lat: " + lat + ", lon: " + lon);
	}

}

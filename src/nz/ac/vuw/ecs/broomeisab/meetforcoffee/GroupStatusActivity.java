package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.Group;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.RequestResult;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.User;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class GroupStatusActivity extends Activity {
	public static String GROUP_ID = "nz.ac.vuw.ecs.broomeisab.meetforcoffee.cafe_id";
	public static String CAFE_NAME = "nz.ac.vuw.ecs.broomeisab.meetforcoffee.cafe_name";
	public static String CAFE_LAT = "nz.ac.vuw.ecs.broomeisab.meetforcoffee.cafe_lat";
	public static String CAFE_LON = "nz.ac.vuw.ecs.broomeisab.meetforcoffee.cafe_lon";
	
	// received from intent
	InputStreamLoader feedLoader;
	XMLPullFeedParser xmlParser;
	
	boolean polling = false;
	private Handler handler;
	private Runnable runnable;
	
	
	// this gets updated lots
	Group group;
	List<User> attending;
	Map<String, Integer> distance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		feedLoader = new InputStreamLoader();
		xmlParser = new XMLPullFeedParser();
		
		setContentView(R.layout.activity_group_status);
		

		
	}

	public void onPause(){
		super.onPause();
		Log.d("", "on pause called");
		
		// set polling as false
		synchronized(handler){
			handler.removeCallbacks(runnable);
			polling = false;
		}
	}
	
	
	public void onResume(){
		super.onResume();
		
		Log.d("", "on resume called");
		
		handler = new Handler();
		
		runnable = new Runnable(){			
				public void run(){
					// get status update
					pollStatus();			
					// reschedule
					synchronized(handler){
						if(polling){
							handler.postDelayed(runnable, 5000);
						}
					}
				}			
			};
					
		polling = true;
		
		// get first status update	
		handler.post(runnable);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_status, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void getFriendLocations(){
		
		// connect to server here
		InputStream is = feedLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetGroupMembersLocations?username=%s&groupID=%d", ApplicationState.username, ApplicationState.groupId));
		attending = xmlParser.parseFriendLocations(is);
	}
	
	private void getGroupStatus(){
		InputStream is = feedLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetGroupStatus?username=%s&groupID=%d", ApplicationState.username, ApplicationState.groupId));
		group = xmlParser.parseGroup(is);	
		
		ApplicationState.cafeLocation = new Location("");
		ApplicationState.cafeLocation.setLatitude(group.cafe.location.getLat());
		ApplicationState.cafeLocation.setLongitude(group.cafe.location.getLon());
		ApplicationState.cafeName = group.cafe.name;
	}
	
	private void checkActiveGroup(){
		InputStream is = feedLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetActiveGroup?username=%s", ApplicationState.username));
		if((ApplicationState.groupId = xmlParser.parseGroupId(is)) == 0){		    	
	    	new AlertDialog.Builder(this).setMessage("Group is no longer active").show(); 	
			leaveGroup();
		}	
	}
	
	
	public void pollStatus(){	
		// goes back to main menu if they aren't in a group
		checkActiveGroup();
		
		// gets group 
		getGroupStatus();
		
		// gets attending people's locations
		getFriendLocations();
				
		// calculate how far away they are
		distance = new HashMap<String, Integer>();
		for(User p: attending){		
			String username = p.GetUsername().equals(ApplicationState.username)? "You" : p.GetUsername();
			distance.put(username, getDistanceInMetres(p.GetLat(), p.GetLon()));
		}		
				
		// update the list
		updateAttendingList();				
	}
	
	
	private void leaveGroup(){

		ApplicationState.cafeLocation = null;
		ApplicationState.cafeName = null;
		ApplicationState.groupId = 0;	
		
		// return to main menu
		Intent intent = new Intent(this, MainActivity.class);	
		startActivity(intent);	
	}
	
	
	public void leaveGroup(View view){
		// ask to leave the group
		InputStream is = feedLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/LeaveGroup?username=%s&groupID=%d", ApplicationState.username, ApplicationState.groupId));	
    	RequestResult result = xmlParser.parseRequestResult(is);  	
    	new AlertDialog.Builder(this).setMessage(result.message).show(); 
		leaveGroup();
	}
	
	
	private void updateAttendingList(){
		// update title
		TextView title = (TextView)findViewById(R.id.group_information_title);
		title.setText(ApplicationState.cafeName);
		
		// pending
		List<String> pendingList = new ArrayList<String>();
		for(String pending: group.pending){
			pendingList.add(pending);
		}

    	ArrayAdapter<String> pendingAdapter =
        		new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pendingList);
        	ListView myList = (ListView)findViewById(R.id.pending_list);
            myList.setAdapter(pendingAdapter);
			
		// attending
		List<String> list = new ArrayList<String>();
		for(String key: distance.keySet()){
			list.add(key + " - " + distance.get(key) + "m away");
		}

    	ArrayAdapter<String> adapter =
        		new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        	myList = (ListView)findViewById(R.id.attending_list);
            myList.setAdapter(adapter);	
	}
	
	
	private int getDistanceInMetres(double lat, double lon){
		
		Location friendLocation = new Location("gps");
		friendLocation.setLatitude(lat);
		friendLocation.setLongitude(lon);
		
		return (int)ApplicationState.cafeLocation.distanceTo(friendLocation);	
	}
	
	
	
	
	
	

	
	
	

}

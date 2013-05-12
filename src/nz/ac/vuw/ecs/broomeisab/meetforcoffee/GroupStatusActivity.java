package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Group;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.User;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
	FeedInputStreamLoader feedLoader;
	XMLPullFeedParser xmlParser;
	
	boolean polling = false;
	Timer timer;
	
	
	// this gets updated lots
	Group group;
	List<User> attending;
	Map<String, Integer> distance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		feedLoader = new FeedInputStreamLoader();
		xmlParser = new XMLPullFeedParser();
		
		setContentView(R.layout.activity_group_status);
		
		pollStatus();
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
		InputStream is = feedLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetGroupMembersLocations?username=%s&groupID=%d", LoginInfo.username, LoginInfo.groupId));
		attending = xmlParser.parseFriendLocations(is);
		
		//attending = new ArrayList<User>();
		//attending.add(new User("Jenny", new nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Location(-41.292112, 174.766432)));
	}
	
	private void getGroupStatus(){
		InputStream is = feedLoader.getFeedInputStream(String.format("http://10.0.2.2:19871/axis2/services/MeetForCoffeeServer/GetGroupStatus?username=%s&groupID=%d", LoginInfo.username, LoginInfo.groupId));
		//group = xmlParser.par
		
		
	}
	
	public void pollStatus(){
		polling = true;
		

				getGroupStatus();
		
				getFriendLocations();
				
				// calculate how far away they are
				distance = new HashMap<String, Integer>();
				for(User p: attending){
					distance.put(p.GetUsername(), getDistanceInMetres(p.GetLat(), p.GetLon()));
				}		
				
				// update the list
				updateAttendingList();	
			
	}
	
	
	public void leaveGroup(View view){
		// ask to leave the group
		
		
		
		// set polling as false
		LoginInfo.cafeLocation = null;
		LoginInfo.cafeName = null;
		LoginInfo.groupId = 0;
		
		
		// return to main menu
		Intent intent = new Intent(this, MainActivity.class);	
		startActivity(intent);	
	}
	
	
	private void updateAttendingList(){
		
		// attending
		List<String> list = new ArrayList<String>();
		for(String key: distance.keySet()){
			list.add(key + " - " + distance.get(key) + "m");
		}

    	ArrayAdapter<String> adapter =
        		new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        	ListView myList = (ListView)findViewById(R.id.attending_list);
            myList.setAdapter(adapter);
		
	}
	
	
	private int getDistanceInMetres(double lat, double lon){
		
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location friendLocation = new Location("gps");
		friendLocation.setLatitude(lat);
		friendLocation.setLongitude(lon);
		
		return (int)LoginInfo.cafeLocation.distanceTo(friendLocation);	
	}
	
	
	
	private class PollTimerTask extends TimerTask{

		@Override
		public void run() {
			// get updated locations
			getFriendLocations();
			
			// calculate how far away they are
			distance = new HashMap<String, Integer>();
			for(User p: attending){
				distance.put(p.GetUsername(), getDistanceInMetres(p.GetLat(), p.GetLon()));
			}		
			
			// update the list
			updateAttendingList();
		}
	}
	
	
	
	

}

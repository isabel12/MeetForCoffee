package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import android.location.Location;

public class ApplicationState {

	public static String username;
	
	// set all info here
	public static int groupId;
	public static String cafeName;
	public static Location cafeLocation = new Location("gps");
	
	public static int getTimeBetweenLocationUpdates(){
		 if (groupId == 0){
			return 300000; // 5 mins
		 } else {
			return 30000; // 30 seconds  
		 }			
	}
	
}

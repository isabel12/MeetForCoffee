package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class UpdateLocationService extends IntentService {

	private String username;
//	private static Timer timer;
	private static boolean running;
	private static LocationManager locationManager;





	public UpdateLocationService() {
		super("UpdateLocationService");
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();

		// check if it was a call to stop
		if(action != null && action.equals("Stop")){
			Log.d("", "stopping UpdateLocationService.");
			running = false;
			stopSelf();
			locationManager.removeUpdates(MainActivity.locationListener);
			return;
		}
		else if (!running){

			// get username
	    	username = intent.getStringExtra("username");
	    	Log.d("", "Received username: " + username);

			// get location
			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.locationListener);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.locationListener);

			running = true;
		}
	}


	protected void updateLocation(Location location){
		Log.d("", "received location update");

		if (location == null)
			return;

		// update our location here
		double lat = location.getLatitude();
		double lon = location.getLongitude();

		Log.d("", "lat: " + lat + ", lon: " + lon);
	}


//	private class GetLocationTimerTask extends TimerTask {
//
//		private LocationManager locationManager;
//		private String providerName;
//
//		public GetLocationTimerTask(LocationManager locationManager, String provider){
//			this.locationManager = locationManager;
//			this.providerName = provider;
//		}
//
//	    @Override
//	    public void run() {
//
//	        Location loc=locationManager.getLastKnownLocation(providerName);
//	        updateLocation(loc);
//	    }
//	}
}



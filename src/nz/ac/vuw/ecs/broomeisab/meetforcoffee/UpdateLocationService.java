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
	private static Timer timer;
	private static boolean running;
	private static LocationManager locationManager;
	
	private final LocationListener locationListener = new LocationListener() {
		  public void onLocationChanged(Location location) {
			  // call our webservice
			  updateLocation(location);	    
		  }

		  public void onProviderDisabled(String provider){}
		  public void onProviderEnabled(String provider) {}
		  
		  @Override
		  public void onStatusChanged(String provider, int status, Bundle extras) {}


		};
	
	
	
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
			timer.cancel();
			running = false;
			stopSelf();
			locationManager.removeUpdates(locationListener);
			return;
		} 
		else if (!running){

			// get username
	    	username = intent.getStringExtra("username");
	    	Log.d("", "Received username: " + username);
				
			// get location
			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			String provider = locationManager.getBestProvider(criteria, true);		
	
			Log.d("", "provider name: " + provider);
			
			
			
			// ask location manager to get updates every half a minute	
			locationManager.requestLocationUpdates(provider,
	                30000, // 1min
	                100,   // 1km
	                locationListener);		
			
			// start timer
		    timer=new Timer();
		    timer.schedule(new GetLocationTimerTask(locationManager),0,10000); // every 30 seconds
		    
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
	
	
	private class GetLocationTimerTask extends TimerTask {

		private LocationManager locationManager;
		
		public GetLocationTimerTask(LocationManager locationManager){
			this.locationManager = locationManager;
		}
		
	    @Override
	    public void run() {
	        Location loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        updateLocation(loc);	        
	    }
	}
}



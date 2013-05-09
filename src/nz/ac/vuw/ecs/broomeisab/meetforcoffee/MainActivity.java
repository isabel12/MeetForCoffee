package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.util.List;

import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.app.Activity;
<<<<<<< HEAD
import android.content.Context;
=======
import android.content.Intent;
import android.util.Log;
>>>>>>> started applicaton
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private String username;
	private boolean updatesActive;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
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
    	
    	toggleLocationUpdates();
    }
    
    
    public void manageFriends(View view){
    	Log.d("", "manage friends started");
    }
    
    
    public void inviteForCoffee(View view){  	
    	Log.d("", "invite for coffee started");	
    }
    
    public void toggleLocationUpdates(View view){
    	Log.d("", "toggle location updates entered");
    	toggleLocationUpdates();
    }
    
    private void toggleLocationUpdates(){
    	
    	Button toggleButton = (Button) findViewById(R.id.toggleLocationUpdatesButton);
    		
    	if(updatesActive){	 		
    		toggleButton.setText("Start Updating Location");		
        	Intent intent = new Intent(this, UpdateLocationService.class);
        	intent.setAction("Stop");
        	startService(intent);	
        	updatesActive = false;
    	} else {
    		toggleButton.setText("Stop Updating Location");	
        	Intent intent = new Intent(this, UpdateLocationService.class);
        	intent.putExtra("username", this.username);
        	startService(intent);
        	updatesActive = true;	
    	}
    }
	
	
	


	public void getGPSLocation(){


		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		List<String> locationProviders = lm.getProviders(true);
		LocationProvider provider = lm.getProvider(locationProviders.get(0));
		lm.getLastKnownLocation(provider.getName());

	}


}

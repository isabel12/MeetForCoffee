package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.util.List;

import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	public void getGPSLocation(){


		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		List<String> locationProviders = lm.getProviders(true);
		LocationProvider provider = lm.getProvider(locationProviders.get(0));
		lm.getLastKnownLocation(provider.getName());

	}


}

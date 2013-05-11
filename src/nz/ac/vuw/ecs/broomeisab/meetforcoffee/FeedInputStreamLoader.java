package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;

/**
 * This class gives you access to the input stream for an online feed.
 * @author Izzi
 *
 */
public class FeedInputStreamLoader {


	/**
	 * Method to get the input stream for the given URL
	 * @param feedUrl
	 * @return
	 */
	public InputStream getFeedInputStream(String feedUrl) {
		URL url = null;

		// parse the url
		try {
			url = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		// get the input stream
		AsyncTask<URL, Void, InputStream> task = new GetInputStream().execute(url);
		try {
			return task.get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private class GetInputStream extends AsyncTask<URL, Void, InputStream> {

		@Override
		protected InputStream doInBackground(URL... feedUrls) {
			try {
				return feedUrls[0].openConnection().getInputStream();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}


}

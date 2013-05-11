package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Group;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;

public class XMLPullFeedParser {

	// names of the XML tags
	public static final String DOCUMENT = "result";
	public static final String FRIEND_REQUESTS = "friendRequests";
	public static final String GROUP_INVITATIONS = "groupInvitations";
	public static final String MEMBER = "member";
	public static final String USERNAME = "username";
	public static final String MESSAGE = "message";
	public static final String SUCCESS = "success";

	public static final String GROUP = "group";
	public static final String ID = "id";
	public static final String CAFE = "cafe";
	public static final String NAME = "name";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String ORGANISER = "organiser";
	public static final String ATTENDING = "attending";
	public static final String PENDING = "pending";
	
	

	public Requests parsePendingRequests(InputStream inputStream){
		try {
			Log.d("", "parsing pending requests");
			AsyncTask<InputStream, Void, Requests> parseTask = new ParsePendingRequestsTask().execute(inputStream);
			return parseTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}


	
	
	
	
	
	/**
	 * Parses route updates.
	 * @author broomeisab
	 *
	 */
	private class ParsePendingRequestsTask extends AsyncTask<InputStream, Void, Requests> {


		private List<String> ParseFriendRequests(XmlPullParser parser) throws XmlPullParserException, IOException{
			
			Log.d("","entered helper method");
			boolean done = false;
			List<String> members = null;
			
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT && !done){
				switch (eventType){
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase(FRIEND_REQUESTS)){
						Log.d("","start of friendRequests tag");
						members = new ArrayList<String>();
					} else {
						if (name.equalsIgnoreCase(USERNAME)){
							Log.d("","start of username tag");
							members.add(parser.nextText());
						} 
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(FRIEND_REQUESTS)){
						Log.d("","end of friendRequests tag");
						done = true;
					}
					break;
				}
				eventType = parser.next();
			}
			
			Log.d("","returning members");
			return members;			
		}
		
		
		
		private List<Group> ParseGroupInvitations(XmlPullParser parser) throws XmlPullParserException, IOException{
			
			
			return null;
			
			
			
			
			
		}
		
		
		
		
		@Override
		protected Requests doInBackground(
				InputStream... params) {


			Requests requests = null;		
			List<String> friends = null;
			List<Group> groups = null;

			XmlPullParserFactory factory;
			XmlPullParser parser = null; 
			try {
				factory = XmlPullParserFactory.newInstance();
				parser = factory.newPullParser();
			} catch (XmlPullParserException e1) {
				e1.printStackTrace();
			}

			try {
				// auto-detect the encoding from the stream
				parser.setInput(params[0], null);
				
				boolean done = false;
				boolean record = false;
				
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT && !done){
					switch (eventType){				
					case XmlPullParser.START_DOCUMENT:
						//Log.d("","start of document");
						friends = new ArrayList<String>();
						groups = new ArrayList<Group>();
						requests = new Requests();
						break;
					case XmlPullParser.START_TAG:
						
						String name = parser.getName();
						//Log.d("", "start tag found: " + name);
						if (name.equalsIgnoreCase(DOCUMENT)){
							Log.d("","start of result");
							record = true;
						} else if (record == true){

							if (name.equalsIgnoreCase(FRIEND_REQUESTS)){
								//Log.d("","start of friendRequests");
								friends = ParseFriendRequests(parser);
							} 
							else if (name.equalsIgnoreCase(GROUP_INVITATIONS)){
								//String type = parser.nextText();
							}
						}
						break;
					case XmlPullParser.END_TAG:
						
						name = parser.getName();
						//Log.d("", "end tag found: " + name);
						if (name.equalsIgnoreCase(DOCUMENT)){
							done = true;
							requests.friends = friends;
							requests.groups = groups;
						}
						break;			
					default:
						Log.d("", "default: ");
						parser.next();	
						break;
					}

					eventType = parser.next();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			// return
			//Log.d("", "returning answer");
			return requests;
		}
	}


	
	public static void main(String[] args){
		String input = "<ns:GetInvitationUpdatesResponse xmlns:ns=\"http://stockquoteservice/xsd\"><ns:return><result> <invitationUpdates> <friendRequests> </friendRequests> </result></ns:return></ns:GetInvitationUpdatesResponse>";
		
		try {
			InputStream is = new ByteArrayInputStream(input.getBytes("UTF-8"));
			
			XMLPullFeedParser parser = new XMLPullFeedParser();
			parser.parsePendingRequests(is);
			
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	



}
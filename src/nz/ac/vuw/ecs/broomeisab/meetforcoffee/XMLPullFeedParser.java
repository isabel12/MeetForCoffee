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

import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Cafe;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Group;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Location;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.User;

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
	public static final String DECLINED = "declined";
	public static final String LOCATION = "location";
		

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


	public List<User> parseFriendLocations(InputStream inputStream){
		
		try {
			Log.d("", "parsing friendLocations");
			AsyncTask<InputStream, Void, List<User>> parseTask = new ParseFriendLocationsTask().execute(inputStream);
			return parseTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		
	}
	
	
	

	
	
	
	private class ParseFriendLocationsTask extends ParsingAsyncTask<InputStream, Void, List<User>>{

		@Override
		protected List<User> doInBackground(InputStream... params) {
			// initialise friends list
			List<User> friends = null;
			
			// get parser
			XmlPullParserFactory factory;
			XmlPullParser parser = null; 
			try {
				factory = XmlPullParserFactory.newInstance();
				parser = factory.newPullParser();
			} catch (XmlPullParserException e1) {
				e1.printStackTrace();
			}
				
			try {
				// set input
				parser.setInput(params[0], null);
				
				boolean done = false;
				boolean record = false;
				
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT && !done){
					String name = null;				
					switch(eventType){
						case XmlPullParser.START_DOCUMENT:
							friends = new ArrayList<User>();
							break;
						case XmlPullParser.START_TAG:
							name = parser.getName();
							Log.d("", "start tag: " + name);
							if(name.equalsIgnoreCase(MEMBER)){
								friends.add(ParseUser(parser));
							}
							break;
						case XmlPullParser.END_TAG:
							name = parser.getName();
							Log.d("", "end tag: " + name);
							if(name.equalsIgnoreCase(DOCUMENT)){
								done = true;					
							}
							break;
					}
					eventType = parser.next();		
				}
						
				Log.d("", "returning friends: " + friends.size());
				return friends;
				
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			

			
			return friends;
			
			
		}	
	}
	
	
	
	
	/**
	 * Parses route updates.
	 * @author broomeisab
	 *
	 */
	private class ParsePendingRequestsTask extends ParsingAsyncTask<InputStream, Void, Requests> {



		
		
		
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
								groups = ParseGroupInvitations(parser);
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


	
	private abstract class ParsingAsyncTask<A,B,ReturnType> extends AsyncTask<A,B,ReturnType>{
		protected List<String> ParseFriendRequests(XmlPullParser parser) throws XmlPullParserException, IOException{
			
			Log.d("","entered ParseMembersUsernames");
			boolean done = false;
			List<String> members = new ArrayList<String>();
			
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT && !done){
				switch (eventType){
				
				case XmlPullParser.START_TAG:		
					String name = parser.getName();
					if (name.equalsIgnoreCase(USERNAME)){
						//Log.d("","start of username tag");
						members.add(parser.nextText().trim());
					} 
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (!name.equalsIgnoreCase(USERNAME) && !name.equalsIgnoreCase(MEMBER) && !name.equalsIgnoreCase(LOCATION)){
						//Log.d("","end of friendRequests tag");
						done = true;
					}
					break;
				}
				eventType = parser.next();
			}
			
			Log.d("","returning usernames: " + members.size());
			return members;			
		}
		
		
		protected User ParseUser(XmlPullParser parser) throws XmlPullParserException, IOException{
			
			Log.d("", "entered ParseUser");
			
			boolean done = false;
			boolean record = false;
			
			User user = null;
			
			String username = null;
			Location location = null;
			
			
			
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT && !done){
				String name = null;
				switch(eventType){
					case(XmlPullParser.START_TAG):
						name = parser.getName();
						if(name.equalsIgnoreCase(MEMBER)){
							record = true;
							location = new Location(0,0);							
						} else if (record && location != null){
							if(name.equalsIgnoreCase(USERNAME)){
								username = parser.nextText().trim();
							} 						
							else if (name.equalsIgnoreCase(LAT)){
								location.setLat(Double.parseDouble(parser.nextText().trim()));
							}					
							else if (name.equalsIgnoreCase(LON)){
								location.setLon(Double.parseDouble(parser.nextText().trim()));
							}
						}
						break;
					case(XmlPullParser.END_TAG):
						name = parser.getName();
						if(name.equalsIgnoreCase(MEMBER)){
							record = false;
							user = new User(username, location);
							done = true;
						}
					break;
				}
				eventType = parser.next();
			}
			
			Log.d("","returning user: " + user);
			return user;	
		}
		
		
		protected Cafe ParseCafe(XmlPullParser parser) throws XmlPullParserException, IOException{
			
			Log.d("","entered ParseCafe");
			boolean done = false;
			boolean record = false;
			Cafe cafe = null;
			
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT && !done){
				switch (eventType){
		
				case XmlPullParser.START_TAG:		
					String name = parser.getName();
					if (name.equalsIgnoreCase(CAFE)){
						cafe = new Cafe();
						record = true;
					} else if (record){
						if (name.equalsIgnoreCase(NAME)){
							cafe.name = parser.nextText();
						}
						else if (name.equalsIgnoreCase(ID)){
							cafe.id = parser.nextText();
						}
						else if (name.equalsIgnoreCase(LOCATION)){
							cafe.location = ParseLocation(parser);
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(CAFE)){
						record = false;
						done = true;
					}
				}
				eventType = parser.next();
			}
			
			Log.d("","returning cafe: " + cafe);
			return cafe;		
		}
		
		
		protected Location ParseLocation(XmlPullParser parser) throws XmlPullParserException, IOException{
			Log.d("","entered ParseLocation");	
			
			boolean done = false;
			boolean record = false;
			Location location = null;
			
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT && !done){
				switch (eventType){
		
				case XmlPullParser.START_TAG:		
					String name = parser.getName();
					if (name.equalsIgnoreCase(LOCATION)){
						location = new Location(0, 0);
						record = true;
					} else if (record){
						if (name.equalsIgnoreCase(LAT)){
							location.setLat(Double.parseDouble(parser.nextText()));
						}
						else if (name.equalsIgnoreCase(LON)){
							location.setLon(Double.parseDouble(parser.nextText()));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(LOCATION)){
						record = false;
						done = true;
					}
				}
				eventType = parser.next();
			}
			
			Log.d("","returning location: " + location);
			return location;	
		}
		
		
		protected String ParseMemberUsername(XmlPullParser parser) throws XmlPullParserException, IOException{
			
			Log.d("","entered ParseMemberUsername");
			boolean done = false;
			String username = null;
			
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT && !done){
				switch (eventType){
		
				case XmlPullParser.START_TAG:		
					String name = parser.getName();
					if (name.equalsIgnoreCase(USERNAME)){
						//Log.d("","start of username tag");
						username = parser.nextText().trim();
						
					} 
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if(name.equalsIgnoreCase(MEMBER)){
						done = true;
					}
				}
				
				eventType = parser.next();
			}
			Log.d("","returning username: " + username);
			return username;	
		}
		
		
		protected List<Group> ParseGroupInvitations(XmlPullParser parser) throws XmlPullParserException, IOException{
			
			
			Log.d("","entered ParseGroupInvitations");
			boolean done = false;
			boolean record = false;
			List<Group> groups = new ArrayList<Group>();
			Group currentGroup = null;
			
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT && !done){
				switch (eventType){
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase(GROUP)){
						Log.d("","start tag for group");
						currentGroup = new Group();
						record = true;
					} else if(record){
						if (name.equalsIgnoreCase(ID)){
							Log.d("", "currentGroup: " + currentGroup);
							String text = parser.getText();
							Log.d("", "groupId: " + text);
							//currentGroup.groupId = Integer.parseInt(text);
						} 
						else if (name.equalsIgnoreCase(CAFE)){
							currentGroup.cafe = ParseCafe(parser);
						}
						else if (name.equalsIgnoreCase(ORGANISER)){
							currentGroup.groupInitiator = ParseMemberUsername(parser);
						}
						else if (name.equalsIgnoreCase(ATTENDING)){
							currentGroup.attending = ParseFriendRequests(parser);
						}
						else if (name.equalsIgnoreCase(PENDING)){
							currentGroup.pending = ParseFriendRequests(parser);
						}
						else if (name.equalsIgnoreCase(DECLINED)){
							currentGroup.pending = ParseFriendRequests(parser);
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(GROUP)){
						//Log.d("","end of friendRequests tag");
						groups.add(currentGroup);
						currentGroup = null;
					} else if (name.equalsIgnoreCase(GROUP_INVITATIONS)){
						done = true;
					}
					break;
				}
				eventType = parser.next();
			}
			
			Log.d("","returning groups");
			return groups;	
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
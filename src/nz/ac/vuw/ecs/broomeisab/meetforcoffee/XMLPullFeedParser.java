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

import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.Cafe;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.Group;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.Invitations;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.Location;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.RequestResult;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects.User;

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
	public static final String REQUEST_RESULT = "requestResult";
	public static final String GROUP_ID = "groupId";
	
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
	public static final String INVITATION_UPDATES = "invitationUpdates";
		

	
	public Invitations parseInvitations(InputStream inputStream){
		try {
			Log.d("", "parsing invitations");
			AsyncTask<InputStream, Void, Invitations> parseTask = new ParseInvitationsTask().execute(inputStream);
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
	
	public Group parseGroup(InputStream inputStream){
		try {
			Log.d("", "parsing friendLocations");
			AsyncTask<InputStream, Void, Group> parseTask = new ParseGroupTask().execute(inputStream);
			return parseTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}	
	}
	
	public List<Cafe> parseCafes(InputStream inputStream){
		
		try {
			Log.d("", "parsing friendLocations");
			AsyncTask<InputStream, Void, List<Cafe>> parseTask = new ParseCafesTask().execute(inputStream);
			return parseTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
			
	}
	

	public int parseGroupId(InputStream inputStream){
		
		try {
			Log.d("", "parsing friendLocations");
			AsyncTask<InputStream, Void, Integer> parseTask = new ParseGroupIdTask().execute(inputStream);
			return parseTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
			
	}
	
	
	public RequestResult parseRequestResult(InputStream inputStream){	
		try {
			Log.d("", "parsing friendLocations");
			AsyncTask<InputStream, Void, RequestResult> parseTask = new ParseRequestResultTask().execute(inputStream);
			return parseTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	
	
	//=================================================================
	// Async tasks
	//=================================================================
	
	
	private class ParseInvitationsTask extends ParsingAsyncTask<InputStream, Void, Invitations>{

		@Override
		protected Invitations doInBackground(InputStream... params) {
			Invitations invitations = null;
			
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
				boolean friendRequests = false;
				boolean groupInvitations = false;
				
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT && !done){
					String name = null;				
					switch(eventType){
						case XmlPullParser.START_TAG:
							name = parser.getName();
							if(name.equalsIgnoreCase(INVITATION_UPDATES)){
								invitations = new Invitations();
							}
							
							else if (name.equalsIgnoreCase(FRIEND_REQUESTS)){
								friendRequests = true;
							}
							
							else if (friendRequests && name.equalsIgnoreCase(MEMBER)){
								invitations.friendInvitations.add(ParseMemberUsername(parser));
							}
							
							else if (name.equalsIgnoreCase(GROUP_INVITATIONS)){
								groupInvitations = true;
							}
							
							else if (groupInvitations && name.equalsIgnoreCase(GROUP)){
								invitations.groupInvitations.add(ParseGroup(parser));
							}
							break;
						case XmlPullParser.END_TAG:
							name = parser.getName();
							if(name.equalsIgnoreCase(DOCUMENT)){
								done = true;					
							} 
							
							else if (name.equalsIgnoreCase(FRIEND_REQUESTS)){
								friendRequests = false;
							}
							
							else if (name.equalsIgnoreCase(GROUP_INVITATIONS)){
								groupInvitations = false;
							}
							break;
					}
					eventType = parser.next();		
				}
							
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			
			return invitations;
			
		}
		
		
	}
	
	private class ParseRequestResultTask extends ParsingAsyncTask<InputStream, Void, RequestResult>{

		@Override
		protected RequestResult doInBackground(InputStream... arg0) {
			RequestResult result = null;
			
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
				parser.setInput(arg0[0], null);
				
				boolean done = false;
				
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT && !done){
					String name = null;				
					switch(eventType){
						case XmlPullParser.START_TAG:
							name = parser.getName();
							if(name.equalsIgnoreCase(REQUEST_RESULT)){
								result = ParseRequestResult(parser);
							}
							break;
						case XmlPullParser.END_TAG:
							name = parser.getName();
							if(name.equalsIgnoreCase(DOCUMENT)){
								done = true;					
							}
							break;
					}
					eventType = parser.next();		
				}
							
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			
			return result;
		}
		
		
		
	}
	
	
	
	private class ParseGroupTask extends ParsingAsyncTask<InputStream, Void, Group>{

		@Override
		protected Group doInBackground(InputStream... params) {
			// initialise
			Group group = null;
			
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
				
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT && !done){
					String name = null;				
					switch(eventType){
						case XmlPullParser.START_TAG:
							name = parser.getName();
							if(name.equalsIgnoreCase(GROUP)){
								group = ParseGroup(parser);
							}
							break;
						case XmlPullParser.END_TAG:
							name = parser.getName();
							if(name.equalsIgnoreCase(DOCUMENT)){
								done = true;					
							}
							break;
					}
					eventType = parser.next();		
				}
							
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			
			Log.d("", "returning group: " + group);
			return group;
		}	
	}
	
	
	
	
	private class ParseGroupIdTask extends ParsingAsyncTask<InputStream, Void, Integer>{

		@Override
		protected Integer doInBackground(InputStream... params) {
			// initialise
			int id = 0;
			
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
				
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT && !done){
					String name = null;				
					switch(eventType){
						case XmlPullParser.START_TAG:
							name = parser.getName();
							if(name.equalsIgnoreCase(GROUP_ID)){
								id = Integer.parseInt(parser.nextText().trim());	
							}
							break;
						case XmlPullParser.END_TAG:
							name = parser.getName();
							if(name.equalsIgnoreCase(DOCUMENT)){
								done = true;					
							}
							break;
					}
					eventType = parser.next();		
				}
							
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			
			Log.d("", "returning groupID: " + id);
			return id;
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
							
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			
			Log.d("", "returning friends: " + friends.size());
			return friends;
		}	
	}
	
	
	
	private class ParseCafesTask extends ParsingAsyncTask<InputStream, Void, List<Cafe>>{

		@Override
		protected List<Cafe> doInBackground(InputStream... params) {
			// initialise friends list
			List<Cafe> cafes = null;
			
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
				
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT && !done){
					String name = null;				
					switch(eventType){
						case XmlPullParser.START_DOCUMENT:
							cafes = new ArrayList<Cafe>();
							break;
						case XmlPullParser.START_TAG:
							name = parser.getName();
							Log.d("", "start tag: " + name);
							if(name.equalsIgnoreCase(CAFE)){
								cafes.add(ParseCafe(parser));
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
							
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			
			Log.d("", "returning cafes: " + cafes.size());
			return cafes;
		}	
	}
	
	
	
	



	
	private abstract class ParsingAsyncTask<A,B,ReturnType> extends AsyncTask<A,B,ReturnType>{
		
		
		protected RequestResult ParseRequestResult(XmlPullParser parser) throws XmlPullParserException, IOException{
			
			Log.d("", "entered ParseRequestResult");
			
			boolean done = false;
			boolean record = false;
			
			RequestResult result = null;
			
				
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT && !done){
				String name = null;
				switch(eventType){
					case(XmlPullParser.START_TAG):
						name = parser.getName();
						if(name.equalsIgnoreCase(REQUEST_RESULT)){
							record = true;
							result = new RequestResult();							
						} else if (record && result != null){
							if(name.equalsIgnoreCase(MESSAGE)){
								result.message = parser.nextText().trim();
							} 						
							else if (name.equalsIgnoreCase(SUCCESS)){
								result.success = Boolean.parseBoolean(parser.nextText().trim());
							}					
						}
						break;
					case(XmlPullParser.END_TAG):
						name = parser.getName();
						if(name.equalsIgnoreCase(REQUEST_RESULT)){
							record = false;
							done = true;
						}
					break;
				}
				eventType = parser.next();
			}
			
			Log.d("","returning requestResult: " + result);
			return result;		
		}
		
		
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
							cafe.name = parser.nextText().trim();
						}
						else if (name.equalsIgnoreCase(ID)){
							cafe.id = parser.nextText().trim();
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
		
		
		protected Group ParseGroup(XmlPullParser parser) throws XmlPullParserException, IOException{
					
			Log.d("","entered ParseGroup");
			boolean done = false;
			boolean record = false;
			Group group = null;
			
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT && !done){
				switch (eventType){
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase(GROUP)){
						group = new Group();
						record = true;
					} else if(record && group != null){
						if (name.equalsIgnoreCase(ID)){
							String text = parser.nextText();
							group.groupId = Integer.parseInt(text.trim());
						} 
						else if (name.equalsIgnoreCase(CAFE)){
							group.cafe = ParseCafe(parser);
						}
						else if (name.equalsIgnoreCase(ORGANISER)){
							group.groupInitiator = ParseMemberUsername(parser);
						}
						else if (name.equalsIgnoreCase(ATTENDING)){
							group.attending = ParseFriendRequests(parser);
						}
						else if (name.equalsIgnoreCase(PENDING)){
							group.pending = ParseFriendRequests(parser);
						}
						else if (name.equalsIgnoreCase(DECLINED)){
							group.declined = ParseFriendRequests(parser);
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(GROUP)){
						done = true;
					} 
					break;
				}
				eventType = parser.next();
			}
			
			Log.d("","returning group: " + group);
			return group;	
		}
	}
	
	
	
	
	public static void main(String[] args){
		String input = "<ns:GetInvitationUpdatesResponse xmlns:ns=\"http://stockquoteservice/xsd\"><ns:return><result> <invitationUpdates> <friendRequests> </friendRequests> </result></ns:return></ns:GetInvitationUpdatesResponse>";
		
		try {
			InputStream is = new ByteArrayInputStream(input.getBytes("UTF-8"));
			
			XMLPullFeedParser parser = new XMLPullFeedParser();

			
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	



}
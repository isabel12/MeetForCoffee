package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Group;

import org.xmlpull.v1.XmlPullParser;

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


	public Requests parsePendingRequests(InputStream inputStream){
		try {
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


		@Override
		protected Requests doInBackground(
				InputStream... params) {

//			// list of updates
//			ArrayList<String> friends = null;
//			ArrayList<Group> groups = null;
//
//			XmlPullParser parser = Xml.newPullParser();
//			try {
//				// auto-detect the encoding from the stream
//				parser.setInput(params[0], null);
//				int eventType = parser.getEventType();
//
//				boolean done = false;
//				boolean record = false;
//
//				// fields to make the Update
//				int parsedVersion = -1; // version currently being parsed
//				RouteUpdate update = null;
//				int id = -1;
//				UpdateType updateType = null;
//				String agency = null;
//				String routeName = null;
//
//
//				while (eventType != XmlPullParser.END_DOCUMENT && !done){
//					switch (eventType){
//					case XmlPullParser.START_DOCUMENT:
//						allUpdates = new ArrayList<List<RouteUpdate>>();
//						break;
//					case XmlPullParser.START_TAG:
//						String name = parser.getName();
//						if (name.equalsIgnoreCase(RECORD)){
//							record = true;
//							update = null;
//							id = -1;
//							updateType = null;
//							agency = null;
//							routeName = null;
//						} else if (record == true){
//
//							if (name.equalsIgnoreCase(VERSION)){
//								// get the version of the record
//								int version = Integer.parseInt(parser.nextText());
//
//								// if its the first record, save version, make a new list
//								if (parsedVersion == -1){
//									parsedVersion = version;
//									versionUpdates = new ArrayList<RouteUpdate>();
//								}
//
//								// if we are looking at a different version now
//								else if (version < parsedVersion){
//									Log.d("", "version=" + version + ", parsedVersion=" + parsedVersion);
//									//put current list into map
//									allUpdates.add(versionUpdates);
//
//									// if that was the last version update we wanted, break
//									if(version == currentSystemVersion){
//										done = true;
//										break;
//									}
//									// otherwise make a new list
//									else {
//										parsedVersion = version;
//										versionUpdates = new ArrayList<RouteUpdate>();
//									}
//								}
//
//
//							} else if (name.equalsIgnoreCase(TYPE)){
//								String type = parser.nextText();
//								if(type.equals(EDIT))
//									updateType = UpdateType.Edit;
//
//								else if (type.equals(DELETE))
//									updateType = UpdateType.Delete;
//
//								else if (type.equals(ADD))
//									updateType = UpdateType.Add;
//							}
//
//							// parsing object specific values
//							else if (name.equalsIgnoreCase(ROUTE_ID)){
//								id = Integer.parseInt(parser.nextText());
//							} else if (name.equalsIgnoreCase(AGENCY_ID)){
//								agency = parser.nextText();
//							} else if (name.equalsIgnoreCase(ROUTE_NAME)){
//								routeName = parser.nextText();
//							}
//
//						}
//						break;
//					case XmlPullParser.END_TAG:
//						name = parser.getName();
//						if (name.equalsIgnoreCase(RECORD) && record){
//							// make update
//							update = new RouteUpdate(id, updateType);
//							update.agency = agency;
//							update.name = routeName;
//
//							// add update to current list of updates
//							versionUpdates.add(update);
//
//							// reset record
//							record = false;
//
//							Log.d("", "loaded update: " + update);
//
//						} else if (name.equalsIgnoreCase(DOCUMENT)){
//							done = true;
//
//							// add the last list if not done already
//							if (!allUpdates.contains(versionUpdates))
//								allUpdates.add(versionUpdates);
//						}
//						break;
//					}
//					eventType = parser.next();
//				}
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//
//			// reverse allUpdates so earliest version is at the start, and latest at the end
//			Collections.reverse(allUpdates);
//
//			// return the version numbers
//			return allUpdates;
			
			return null;
		}
	}





}
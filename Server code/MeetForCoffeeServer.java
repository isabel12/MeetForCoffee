import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;


public class MeetForCoffeeServer {

	int userId;
	int groupId;
	int cafeId;

	// google places API
	int radius = 3000;

	// files
	File usersFile;
	File friendsFile;
	File cafesFile;
	File groupInvitationsFile;
	File friendInvitationsFile;
	File friendRequestsFile;
	File activeGroupsFile;
	File locationsFile;
	File idsFile;

	// filenames
	String USERS_FILENAME = "users.txt";
	String FRIENDS_FILENAME = "friends.txt";
	String CAFES_FILENAME = "cafes.txt";
	String GROUPINVITATIONS_FILENAME = "groupInvitations.txt";
	String FRIENDINVITATIONS_FILENAME = "friendInvitations.txt";
	String FRIENDREQUESTS_FILENAME = "friendRequests.txt";
	String ACTIVEGROUPS_FILENAME = "activeGroups.txt";
	String LOCATIONS_FILENAME = "locations.txt";
	String IDS_FILENAME = "ids.txt";

	// collections
	private Map<String, User> users = new HashMap<String, User>();
	private Map<String, Set<String>> friends = new HashMap<String, Set<String>>();  // from username to Set<username>
	private Map<String, Cafe> cafes = new HashMap<String, Cafe>(); // from cafe name to Cafe
	private Map<String, Set<Group>> groupInvitations = new HashMap<String, Set<Group>>(); //from username to GroupId
	private Map<String, Set<String>> friendInvitations = new HashMap<String, Set<String>>(); // from username to Set<username>
	private Map<String, Set<String>> friendRequests = new HashMap<String, Set<String>>();
	private Map<Integer, Group> activeGroups = new HashMap<Integer, Group>(); // groups
	private Map<String, Location> locations = new HashMap<String, Location>();
	private Map<String, Integer> ids = new HashMap<String, Integer>(); // the next ids of the object types

	public MeetForCoffeeServer(){

		ProxyConnection.ConnectToProxy();

		// check if files exist, if they don't, create
		String currentFileName = "";
		try {
			// users
			currentFileName = USERS_FILENAME;
			usersFile =new File(currentFileName);
			if(!usersFile.exists()){
				usersFile.createNewFile();
				WriteFile(currentFileName, usersFile, users);
			}

			// friends
			currentFileName = FRIENDS_FILENAME;
			friendsFile =new File(currentFileName);
			if(!friendsFile.exists()){
				friendsFile.createNewFile();
				WriteFile(currentFileName, friendsFile, friends);
			}

			// cafes
			currentFileName = CAFES_FILENAME;
			cafesFile =new File(currentFileName);
			if(!cafesFile.exists()){
				cafesFile.createNewFile();
				WriteFile(currentFileName, cafesFile, cafes);
			}

			// group invitations
			currentFileName = GROUPINVITATIONS_FILENAME;
			groupInvitationsFile =new File(currentFileName);
			if(!groupInvitationsFile.exists()){
				groupInvitationsFile.createNewFile();
				WriteFile(currentFileName, groupInvitationsFile, groupInvitations);
			}

			// friend invitations
			currentFileName = FRIENDINVITATIONS_FILENAME;
			friendInvitationsFile =new File(currentFileName);
			if(!friendInvitationsFile.exists()){
				friendInvitationsFile.createNewFile();
				WriteFile(currentFileName, friendInvitationsFile, friendInvitations);
			}

			// friend requests
			currentFileName = FRIENDREQUESTS_FILENAME;
			friendRequestsFile =new File(currentFileName);
			if(!friendRequestsFile.exists()){
				friendRequestsFile.createNewFile();
				WriteFile(currentFileName, friendRequestsFile, friendRequests);
			}

			// active groups
			currentFileName = ACTIVEGROUPS_FILENAME;
			activeGroupsFile =new File(currentFileName);
			if(!activeGroupsFile.exists()){
				activeGroupsFile.createNewFile();
				WriteFile(currentFileName, activeGroupsFile, activeGroups);
			}

			// locations
			currentFileName = LOCATIONS_FILENAME;
			locationsFile =new File(currentFileName);
			if(!locationsFile.exists()){
				locationsFile.createNewFile();
				WriteFile(currentFileName, locationsFile, locations);
			}

			currentFileName = IDS_FILENAME;
			idsFile = new File(currentFileName);
			if(!idsFile.exists()){
				idsFile.createNewFile();
				ids.put("Group", 0);
				WriteFile(currentFileName, idsFile, ids);
			}
		}
		catch (IOException ex) {
			System.out.println("Unable to open the file "+currentFileName);
		}

		// reload the previous state
		this.users = ReadFile(USERS_FILENAME, usersFile);
		System.out.println("Number of users: " + users.size());
		this.friends = ReadFile(FRIENDS_FILENAME, friendsFile);
		this.cafes = ReadFile(CAFES_FILENAME, cafesFile);
		this.groupInvitations = ReadFile(GROUPINVITATIONS_FILENAME, groupInvitationsFile);
		this.friendInvitations = ReadFile(FRIENDINVITATIONS_FILENAME, friendInvitationsFile);
		this.friendRequests = ReadFile(FRIENDREQUESTS_FILENAME, friendRequestsFile);
		this.activeGroups = ReadFile(ACTIVEGROUPS_FILENAME, activeGroupsFile);
		this.locations = ReadFile(LOCATIONS_FILENAME, locationsFile);
		this.ids = ReadFile(IDS_FILENAME, idsFile);
	}

	//======================================================================================
	// Server API methods
	//======================================================================================
	public void DropTables(){

		if(usersFile.delete()){
			System.out.println(USERS_FILENAME + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}

		if(friendsFile.delete()){
			System.out.println(FRIENDS_FILENAME + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}

		if(cafesFile.delete()){
			System.out.println(CAFES_FILENAME + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}

		if(groupInvitationsFile.delete()){
			System.out.println(GROUPINVITATIONS_FILENAME + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}

		if(friendInvitationsFile.delete()){
			System.out.println(FRIENDINVITATIONS_FILENAME + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}

		if(friendRequestsFile.delete()){
			System.out.println(FRIENDREQUESTS_FILENAME + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}

		if(activeGroupsFile.delete()){
			System.out.println(ACTIVEGROUPS_FILENAME + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}

		if(locationsFile.delete()){
			System.out.println(LOCATIONS_FILENAME + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}

	}


	public String Register(String username){
		if(users.containsKey(username)){
			return XMLWriter.RegisterResult(false);
		}

		users.put(username, new User(userId++, username, 0, 0));
		WriteFile(USERS_FILENAME, usersFile, this.users);

		return XMLWriter.RegisterResult(true);
	}


	public String AddFriend(String username, String toInvite){

		// get current friends and initialise if doesn't exist
		Set<String> friends = this.friends.get(username);
		if(friends == null){
			friends = new HashSet<String>();
			this.friends.put(username, friends);
		}

		// check they aren't already a friend
		if (friends.contains(toInvite))
			return XMLWriter.PerformActionResult("Already a friend", false);

		// check not already invited
		Set<String> invitations = friendInvitations.get(username);
		if(invitations == null){
			invitations = new HashSet<String>();
		}
		if(invitations.contains(toInvite)){
			return XMLWriter.PerformActionResult("You have already invited " + toInvite + " as a friend", false);
		}

		// add to invite
		invitations.add(toInvite);
		friendInvitations.put(username, invitations);

		// add to request
		Set<String> requests = friendRequests.get(toInvite);
		if(requests == null){
			requests = new HashSet<String>();
		}
		requests.add(username);
		friendRequests.put(toInvite, requests);

		System.out.println("Friend invitations:");
		for(String key: friendInvitations.keySet()){
			System.out.println(key + " invited " + friendInvitations.get(key));
		}
		System.out.println("Friend requests:");
		for(String key:friendRequests.keySet()){
			System.out.println(friendRequests.get(key) + " really wants to be friends with " + key);
		}

		// write to file
		WriteFile(FRIENDS_FILENAME, friendsFile, this.friends);
		WriteFile(FRIENDREQUESTS_FILENAME, friendRequestsFile, this.friendRequests);
		WriteFile(FRIENDINVITATIONS_FILENAME, friendInvitationsFile, this.friendInvitations);


		return XMLWriter.PerformActionResult("Invited " + toInvite + " to be a friend", true);

	}


	// this method returns the friend invitations, meeting invitations
	public String GetInvitationUpdates(String username){

		Set<String> friendRequests = this.friendRequests.get(username);
		Set<Group> groupRequests = this.groupInvitations.get(username);

		if(friendRequests == null){
			friendRequests = new HashSet<String>();
			this.friendRequests.put(username, friendRequests);
		}

		if(groupRequests == null){
			groupRequests = new HashSet<Group>();
			this.groupInvitations.put(username, groupRequests);
		}

		return XMLWriter.GetInvitationUpdatesResult(friendRequests, groupRequests);
	}


	public void UpdateLocation(String username, double lat, double lon){
		locations.put(username, new Location(lat, lon));
		WriteFile(LOCATIONS_FILENAME, locationsFile, locations);
	}

	public String GetAllFriendsLocations(String username){

		Set<String> friends = this.friends.get(username);

		if(friends == null){
			friends = new HashSet<String>();
			this.friends.put(username, friends);
		}

		Map<String, Location> friendLocations = new HashMap<String, Location>();
		for(String friend: friends){
			Location loc = this.locations.get(friend);
			if(loc == null){
				loc = new Location(0, 0);
				this.locations.put(friend, loc);
			}

			friendLocations.put(friend, loc);
		}

		return XMLWriter.GetFriendsLocationsResult(locations);
	}


	/**
	 * Returns the group id of the newly created group.
	 * @param username
	 * @param toInvite
	 * @param Location
	 * @return
	 */
	public String InviteFriendToMeet(String username, String toInvite, String CafeXML){

		// make sure you don't already own a group
		for(Group g: activeGroups.values()){
			if (g.groupInitiator.equals(username)){
				return XMLWriter.PerformActionResult("You already have an active group: " + g.groupId, false);
			}
		}

		// make sure you are friends
		if(!friends.get(username).contains(toInvite) || !friends.get(toInvite).contains(username)){
			return XMLWriter.PerformActionResult("You are not friends with " + toInvite, false);
		}

		// get the cafe
		Cafe cafe = new Cafe(); //LoadCafe(CafeXML);

		// create an active group
		Group newGroup = new Group(GetId("Group"), cafe, username, new String[]{toInvite});
		activeGroups.put(newGroup.groupId, newGroup);

		// tell the guest they are invited
		Set<Group> invites = groupInvitations.get(toInvite);
		if(invites == null){
			invites = new HashSet<Group>();
			groupInvitations.put(toInvite, invites);
		}
		invites.add(newGroup);

		WriteFile(ACTIVEGROUPS_FILENAME,activeGroupsFile, activeGroups);
		WriteFile(GROUPINVITATIONS_FILENAME, groupInvitationsFile, groupInvitations);

		// return result
		return XMLWriter.CreateGroupResult(newGroup.groupId);
	}


	public String acceptGroupInvitation(String username, int groupID){
		// make sure you have been invited

		Group g = activeGroups.get(groupId);
		if (g == null)
			return XMLWriter.PerformActionResult("No active group with that id: " + groupID, false);


		// if you are the organiser
		if(g.groupInitiator.equals(username))
			return XMLWriter.PerformActionResult("You are the organiser of this group.  You are already attending.", false);


		// if you have already accepted
		if(g.attending.contains(username))
			return XMLWriter.PerformActionResult("You are already attending.", false);

		// if you are in pending
		if (g.pending.contains(username)){
			g.pending.remove(username);
			g.attending.add(username);
			return XMLWriter.PerformActionResult("You are now attending group " + groupID, true);
		}

		// if you are in declined
		if (g.declined.contains(username)){
			g.declined.remove(username);
			g.attending.add(username);
			return XMLWriter.PerformActionResult("You are now attending group " + groupID, true);
		}

		// if none of these, you weren't invited
		return XMLWriter.PerformActionResult("You haven't been invited to join this group: " + groupID, false);

	}

	public String CancelGroup(String username, int groupID){

		Group group = activeGroups.get(groupID);
		if(group == null){
			return XMLWriter.PerformActionResult("Group could not be found", false);
		}

		if(!group.groupInitiator.equals(username)){
			return XMLWriter.PerformActionResult("You are not the group organiser", false);
		}

		activeGroups.remove(groupID);

		return XMLWriter.PerformActionResult("Group cancelled", true);
	}


	public String acceptFriendRequest(String username, String toAccept){

		// get friends lists
		Set<String> myFriends = friends.get(username);
		if(myFriends == null){
			myFriends = new HashSet<String>();
			friends.put(username, myFriends);
		}
		Set<String> theirFriends = friends.get(toAccept);
		if(theirFriends == null){
			theirFriends = new HashSet<String>();
			friends.put(toAccept, theirFriends);
		}
		// check not already friends
		boolean youFriendsWithThem = myFriends.contains(toAccept);
		boolean theyFriendsWithYou = theirFriends.contains(username);
		if(youFriendsWithThem && theyFriendsWithYou){
			return XMLWriter.PerformActionResult(String.format("You are already friends with %s",  toAccept), false);
		} else if (myFriends.contains(toAccept) || theirFriends.contains(username)){
			return XMLWriter.PerformActionResult(String.format("Invalid state.  %s is friends with you: %b, you are friends with %s: %b",toAccept, theyFriendsWithYou, toAccept, youFriendsWithThem),false );
		}

		// get set of people wanting to be your friend
		Set<String> myFriendRequests = friendRequests.get(username);
		if(myFriendRequests == null){
			myFriendRequests = new HashSet<String>();
			friendRequests.put(username, myFriendRequests);
		}
		// check that you have been invited
		if (!myFriendRequests.contains(toAccept)){
			return XMLWriter.PerformActionResult(String.format("There is no invitation from %s to be friends",toAccept), false);
		}

		// add to friends lists
		myFriends.add(toAccept);
		theirFriends.add(username);

		// remove from friendInvitations
		myFriendRequests.remove(toAccept);

		// remove from friendRequests
		Set<String> invitations = friendInvitations.get(toAccept);
		boolean wasInvited = invitations != null && invitations.remove(username);
		if(!wasInvited){
			return XMLWriter.PerformActionResult(String.format("Invalid state. %s's invitation wasn't properly recorded", toAccept),false);
		}

		WriteFile(FRIENDS_FILENAME, friendsFile, friends);
		WriteFile(FRIENDINVITATIONS_FILENAME, friendInvitationsFile, friendInvitations);
		WriteFile(FRIENDREQUESTS_FILENAME, friendRequestsFile, friendRequests);

		return XMLWriter.PerformActionResult(String.format("You are now friends with %s", toAccept ), true);
	}


	public String GetGroupMembersLocations(String username, int groupID){

		// get all members of group
		List<String> members = this.activeGroups.get(groupID).attending;

		// get map of friend to location
		Map<String, Location> friendLocations = new HashMap<String, Location>();
		for(String friend: members){
			Location loc = this.locations.get(friend);
			if(loc == null){
				loc = new Location(0, 0);
				this.locations.put(friend, loc);
			}

			friendLocations.put(friend, loc);
		}

		// return result in xml format
		return XMLWriter.GetFriendsLocationsResult(locations);
	}


	public String GetCloseByCafes(String username){

		//Location location = locations.get(username);

		String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/xml?location=-33.8670522,151.1957362&radius=3000&types=cafe&sensor=false&key=AIzaSyDV4eTh94idJJGG5_mGWa9aB5fP6aphpu0";

		String xmlResult = getXMLResult(url);
		Map<String, Cafe> cafes = LoadCafes(xmlResult);

		System.out.println("Loaded cafes: " + cafes.size());
		return XMLWriter.GetCafesResult(cafes.values());
	}


	/**
	 * Gets and increments the id for the given object type.
	 * @param objectType.  Eg. 'Group'
	 * @return
	 */
	private int GetId(String objectType){
		Integer id = ids.get(objectType);

		if(id == null)
			throw new IllegalArgumentException("No entry for the given object type: " + objectType);

		ids.put(objectType, ++id);

		WriteFile(IDS_FILENAME, idsFile, ids);
		return id;
	}



	//====================================================================
	// Parsing methods
	//====================================================================
	/**
	 * Private method to read XML from a request.
	 * @param urlToRead
	 * @return
	 */
	private static String getXMLResult(String urlToRead) {
		URL url;
		URLConnection conn;
		InputStream is;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(urlToRead);
			conn = (URLConnection) url.openConnection();
			is = conn.getInputStream();
			rd = new BufferedReader(new InputStreamReader(is));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	private static Map<String, Cafe> LoadCafes(String xml){

		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		Map<String, Cafe> cafes = null;

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XMLCafeHandler handler = new XMLCafeHandler();
			saxParser.parse(inputStream, handler);
			cafes = handler.getCafes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return the cafes
		return cafes;
	}

	private static Cafe LoadCafe(String xml){

		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		Cafe cafe = null;

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XMLCafeHandler handler = new XMLCafeHandler();
			saxParser.parse(inputStream, handler);
			cafe = handler.getCafe();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return the cafes
		return cafe;
	}


	//===============================================================================
	// file saving and loading
	//===============================================================================

	private <key, value> void WriteFile (String filename, File f, Map<key, value> collectionToWrite){

		try{
			//use buffering
			OutputStream file = new FileOutputStream(f);
			OutputStream buffer = new BufferedOutputStream( file );
			ObjectOutput output = new ObjectOutputStream( buffer );
			try{
				output.writeObject(collectionToWrite);
				System.out.println("Successfully wrote " + filename + "to file.");
			}
			finally{
				output.close();
			}
		}
		catch(IOException ex){
			System.out.println("Unable to write to " + filename);
			System.out.println(ex);
		}
	}

	private <key, value> Map<key, value> ReadFile(String filename, File f){

		Map<key, value> map = null;
		try{
			//use buffering
			InputStream file = new FileInputStream(f);
			InputStream buffer = new BufferedInputStream( file );
			ObjectInput input = new ObjectInputStream ( buffer );
			Object readObject = null;

			//deserialize the map
			try{
				readObject = input.readObject();
			}
			finally{
				input.close();
			}

			//cast to the right type of object
			map = (HashMap<key, value>)readObject;
			System.out.println("Successfully read " + filename + "from file.");

		}
		catch(ClassNotFoundException ex){
			System.out.println("Cannot perform input. Class not found.");
		}
		catch(IOException ex){
			System.out.println("Cannot perform input.");
		}

		// return the answer
		return map;
	}



	public static void main(String[] args){


		MeetForCoffeeServer server = new MeetForCoffeeServer();

		server.Register("bill");
		server.Register("ben");
		server.AddFriend("bill", "ben");
		server.acceptFriendRequest("ben", "bill");
		server.InviteFriendToMeet("bill", "ben", "some cafe");

		server.GetCloseByCafes("blah");
	}


}

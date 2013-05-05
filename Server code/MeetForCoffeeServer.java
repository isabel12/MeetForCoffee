import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MeetForCoffeeServer {

	int userId;
	int groupId;
	int cafeId;


	//
	private Map<String, User> users;

	private Map<String, Set<String>> friends;  // from username to Set<username>
	private Map<String, Cafe> cafes; // from cafe name to Cafe
	private Map<String, Set<Group>> groupInvitations; //from username to GroupId
	private Map<String, Set<String>> friendInvitations; // from username to Set<username>
	private Map<String, Set<String>> friendRequests;
	private Map<Integer, Group> activeGroups; // groups

	private Map<String, Location> locations;


	public MeetForCoffeeServer(){
		this.users = new HashMap<String, User>();
		this.friends = new HashMap<String, Set<String>>();
		this.cafes = new HashMap<String, Cafe>();
		this.groupInvitations = new HashMap<String, Set<Group>>();
		this.friendInvitations = new HashMap<String, Set<String>>();
		this.friendRequests = new HashMap<String, Set<String>>();
		this.activeGroups = new HashMap<Integer, Group>();
		this.locations = new HashMap<String, Location>();
	}

	public String Register(String username){
		if(users.containsKey(username)){
			return XMLWriter.RegisterResult(false);
		}

		users.put(username, new User(userId++, username, 0, 0));
		return XMLWriter.RegisterResult(true);
	}


	public String AddFriend(String username, String toInvite){

		try{
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

			return XMLWriter.PerformActionResult("Invited " + toInvite + " to be a friend", true);
		} catch(Exception e){
			return XMLWriter.PerformActionResult(e.getMessage(), false);
		}
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
	public int InviteFriendToMeet(String username, String toInvite, String Location){





		return -1;

	}


	public void acceptGroupInvitation(String username, int groupID){

	}

	public String acceptFriendRequest(String username, String toAccept){

		// get set of people wanting to be your friend
		Set<String> myFriendRequests = friendRequests.get(username);
		if(myFriendRequests == null){
			myFriendRequests = new HashSet<String>();
			friendRequests.put(username, myFriendRequests);
		}


		// check that you have been invited
		if (myFriendRequests.contains(toAccept)){
			return XMLWriter.PerformActionResult(String.format("There is no invitation from %s to be friends",toAccept), false);
		}

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

		// add to friends lists
		myFriends.add(toAccept);
		theirFriends.add(username);


		// remove from friendInvitations
		myFriendRequests.remove(toAccept);

		// remove from friendRequests
		boolean wasInvited = friendInvitations.get(toAccept).remove(username);
		if(!wasInvited){
			return XMLWriter.PerformActionResult(String.format("Invalid state. %s's invitation wasn't properly recorded", toAccept),false);
		}

		return XMLWriter.PerformActionResult(String.format("You are now friends with %s", toAccept ), true);
	}


	public String GetGroupMembersLocations(String username, int groupID){

		return "todo";

	}


	public String GetCloseByCafes(){

		return "todo";

	}





}

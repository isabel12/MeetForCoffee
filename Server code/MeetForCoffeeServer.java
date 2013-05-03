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


	public MeetForCoffeeServer(){
		this.users = new HashMap<String, User>();
		this.friends = new HashMap<String, Set<String>>();
		this.cafes = new HashMap<String, Cafe>();
		this.groupInvitations = new HashMap<String, Set<Group>>();
		this.friendInvitations = new HashMap<String, Set<String>>();
		this.friendRequests = new HashMap<String, Set<String>>();
		this.activeGroups = new HashMap<Integer, Group>();
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
				return XMLWriter.AddFriendResult("Already a friend", false);

			// check not already invited
			Set<String> invitations = friendInvitations.get(username);
			if(invitations == null){
				invitations = new HashSet<String>();
			}
			if(invitations.contains(toInvite)){
				return XMLWriter.AddFriendResult("You have already invited " + toInvite + " as a friend", false);
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

			return XMLWriter.AddFriendResult("Invited " + toInvite + " to be a friend", true);
		} catch(Exception e){
			return XMLWriter.AddFriendResult(e.getMessage(), false);
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



	}


	public String GetAllFriendsLocations(String username){

		return "todo";


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
		// check that you have been invited


		// set both to friends

		// remove from friendInvitations


		// remove from friendRequests

		return "todo";


	}


	public String GetGroupMembersLocations(String username, int groupID){

		return "todo";

	}


	public String GetCloseByCafes(){

		return "todo";

	}





}

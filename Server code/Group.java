import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Group {

	private enum Status{
		Accepted, Pending, Declined;
	}

	int groupId;
	String cafeName;  // name of the cafe
	String groupInitiator; // username
	Map<String, Status> members;

	public Group(int groupId, String cafeName, String instigator, String[] pendingMembers){
		this.groupId = groupId;
		this.groupInitiator = instigator;
		this.members = new HashMap<String, Status>();

		// add initiator
		this.members.put(instigator, Status.Accepted);

		// add members
		for(String s: pendingMembers){
			this.members.put(s, Status.Pending);
		}
	}

	/**
	 * Moves the given user to full member status, returning true if successful.
	 * Returns false if the username isn't in the pendingMembers list.
	 * @param username
	 * @return
	 */
	public boolean AcceptInvitation(String username){

		Status memberStatus = members.get(username);

		if(memberStatus != null && memberStatus == Status.Pending){
			members.put(username, Status.Accepted);
			return true;
		}

		return false;
	}

	/**
	 * You can decline an invitation if your status isn't already declined.
	 * @param username
	 * @return
	 */
	public boolean DeclineInvitation(String username){

		Status memberStatus = members.get(username);

		if(memberStatus != null && memberStatus != Status.Declined){
			members.put(username, Status.Declined);
			return true;
		}

		return false;
	}


	/**
	 * You can invite someone if they aren't already in the group.
	 * @param username
	 * @return
	 */
	public boolean InviteNewMember(String username){

		Status memberStatus = members.get(username);

		if(memberStatus == null){
			members.put(username, Status.Pending);
			return true;
		}

		return false;
	}

}

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Group implements Serializable{
	
	int groupId;
	Cafe cafe;  // name of the cafe
	String groupInitiator; // username
	List<String> attending;
	List<String> pending;
	List<String> declined;

	public Group(int groupId, Cafe cafe, String instigator, String[] pendingMembers){
		this.groupId = groupId;
		this.groupInitiator = instigator;
		this.attending = new ArrayList<String>();
		this.pending = new ArrayList<String>();
		this.declined = new ArrayList<String>();

		// add initiator
		this.attending.add(instigator);

		// add members
		for(String s: pendingMembers){
			this.pending.add(s);
		}

		this.cafe = cafe;
	}

	/**
	 * Moves the given user to full member status, returning true if successful.
	 * Returns false if the username isn't in the pendingMembers list.
	 * @param username
	 * @return
	 */
	public boolean AcceptInvitation(String username){


		if(pending.contains(username)){
			attending.add(username);
			pending.remove(username);
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

		if(pending.contains(username)){
			declined.add(username);
			pending.remove(username);
			return true;
		}

		if(attending.contains(username)){
			declined.add(username);
			attending.remove(username);
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

		if(!pending.contains(username) && !attending.contains(username)){

			if(declined.contains(username)){
				declined.remove(username);
			}

			pending.add(username);
			return true;
		}

		return false;
	}





}

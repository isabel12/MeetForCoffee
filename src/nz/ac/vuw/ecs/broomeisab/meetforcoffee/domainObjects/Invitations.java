package nz.ac.vuw.ecs.broomeisab.meetforcoffee.domainObjects;

import java.util.ArrayList;
import java.util.List;

public class Invitations {

	public List<Group> groupInvitations;
	public List<String> friendInvitations;

	
	public Invitations(){
		this.groupInvitations = new ArrayList<Group>();
		this.friendInvitations = new ArrayList<String>();
	}
	
}

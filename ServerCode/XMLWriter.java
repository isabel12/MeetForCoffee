
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.*;

public class XMLWriter {

	private static int tabIndent;

	public synchronized static String PerformActionResult(String message, Boolean success){
		tabIndent = 0;

		StringBuilder sb = new StringBuilder();

		OpenTag(sb, "result");
		OpenTag(sb, "requestResult");
		SimpleTag(sb, "message", message);
		SimpleTag(sb, "success", success.toString());
		CloseTag(sb, "requestResult");
		CloseTag(sb, "result");

		System.out.println(sb.toString());
		return sb.toString();
	}

	public synchronized static String GetInvitationUpdatesResult(Set<String> friendRequests,
			Set<Group> groupRequests) {

		tabIndent = 0;
		StringBuilder sb = new StringBuilder();
		OpenTag(sb, "result");
		OpenTag(sb, "invitationUpdates");
		OpenTag(sb, "friendRequests");
		for(String f: friendRequests){
			Member(sb, f);
		}
		CloseTag(sb, "friendRequests");
		OpenTag(sb, "groupInvitations");
		for(Group g: groupRequests){
			Group(sb, g);
		}
		CloseTag(sb, "groupInvitations");
		CloseTag(sb, "invitationUpdates");
		CloseTag(sb, "result");

		System.out.println(sb.toString());
		return sb.toString();
	}


	public synchronized static String CreateGroupResult(int groupId){
		tabIndent = 0;
		StringBuilder sb = new StringBuilder();
		OpenTag(sb, "result");
		
			SimpleTag(sb, "groupId", groupId + "");
		
		CloseTag(sb, "result");
		System.out.println(sb.toString());
		return sb.toString();
	}

	public synchronized static String GetFriendsLocationsResult(Map<String, Location> locations){
		tabIndent = 0;
		StringBuilder sb = new StringBuilder();

		OpenTag(sb, "result");
		for(String username: locations.keySet()){
			Member(sb, username, locations.get(username));
		}
		CloseTag(sb, "result");

		System.out.println(sb.toString());
		return sb.toString();
	}

	public synchronized static String GetCafesResult(Collection<Cafe> cafes){
		tabIndent = 0;
		StringBuilder sb = new StringBuilder();

		OpenTag(sb, "result");
		for(Cafe c: cafes){
			Cafe(sb, c);
		}
		CloseTag(sb, "result");

		System.out.println(sb.toString());
		return sb.toString();	
	}
	
	//===========================================================================================
	// object encoders
	//===========================================================================================

	private static void Group(StringBuilder sb, Group group){

		OpenTag(sb, "group");

		SimpleTag(sb, "id", group.groupId + "");

		Cafe(sb, group.cafe);

		OpenTag(sb, "organiser");
			Member(sb, group.groupInitiator);
		CloseTag(sb, "organiser");

		OpenTag(sb, "attending");
		for(String member: group.attending){
			Member(sb, member);
		}
		CloseTag(sb, "attending");

		OpenTag(sb, "pending");
		for(String member: group.pending){
			Member(sb, member);
		}
		CloseTag(sb, "pending");

		OpenTag(sb, "declined");
		for(String member: group.declined){
			Member(sb, member);
		}
		CloseTag(sb, "declined");

		CloseTag(sb, "group");
	}
	

	private static void Member(StringBuilder sb, String username){
		OpenTag(sb, "member");
		SimpleTag(sb, "username", username);
		CloseTag(sb, "member");		
	}
	
	private static void Member(StringBuilder sb, String username, Location location){
		OpenTag(sb, "member");
		SimpleTag(sb, "username", username);
		Location(sb, location);
		CloseTag(sb, "member");		
	}

	private static void Location(StringBuilder sb, Location location){
		OpenTag(sb, "location");
		SimpleTag(sb, "lat", location.getLat() + "");
		SimpleTag(sb, "lon", location.getLon() + "");
		CloseTag(sb, "location");
	}

	private static void Cafe(StringBuilder sb, Cafe cafe){
		OpenTag(sb, "cafe");
		if(cafe != null){
			SimpleTag(sb, "name", cafe.name);
			SimpleTag(sb, "id", cafe.id);
			Location(sb, cafe.location);
		}
		CloseTag(sb, "cafe");
	}

	
	//=================================================================================
	// basic building blocks
	//=================================================================================
	
	
	private static void OpenTag(StringBuilder sb, String tagName){
		String tabs = "";
		for(int i = 0; i < tabIndent; i++){
			tabs += "\t";
		}

		sb.append(tabs + "<" + tagName + ">\r\n");
		tabIndent++;
	}

	private static void AddValue(StringBuilder sb, String message){
		String tabs = "";
		for(int i = 0; i < tabIndent; i++){
			tabs += "\t";
		}

		sb.append(tabs + message + "\r\n");
	}

	private static void CloseTag(StringBuilder sb, String tagName){
		tabIndent--;

		String tabs = "";
		for(int i = 0; i < tabIndent; i++){
			tabs += "\t";
		}

		sb.append(tabs + "</" + tagName + ">\r\n");

	}

	private static void SimpleTag(StringBuilder sb, String tagName, String content){
		OpenTag(sb, tagName);

		AddValue(sb, content);

		CloseTag(sb, tagName);
	}







}


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

import javax.xml.*;

public class XMLWriter {

	private static int tabIndent;

	public synchronized static String PerformActionResult(String message, Boolean success){
		tabIndent = 0;

		StringBuilder sb = new StringBuilder();

		OpenTag(sb, "result");
		SimpleTag(sb, "message", message);
		SimpleTag(sb, "success", success.toString());
		CloseTag(sb, "result");

		System.out.println(sb.toString());
		return sb.toString();
	}

	public synchronized static String RegisterResult(Boolean success){
		tabIndent = 0;

		StringBuilder sb = new StringBuilder();

		OpenTag(sb, "result");
		SimpleTag(sb, "success", success.toString());
		CloseTag(sb, "result");

		System.out.println(sb.toString());
		return sb.toString();
	}

	public static String GetInvitationUpdatesResult(Set<String> friendRequests,
			Set<Group> groupRequests) {

		StringBuilder sb = new StringBuilder();
		OpenTag(sb, "result");
		OpenTag(sb, "friendRequests");
		for(String f: friendRequests){
			SimpleTag(sb, "member", f);
		}
		CloseTag(sb, "friendRequests");
		OpenTag(sb, "groupInvitations");
		for(Group g: groupRequests){
			Group(sb, g);
		}
		CloseTag(sb, "groupInvitations");
		CloseTag(sb, "result");

		System.out.println(sb.toString());
		return sb.toString();
	}



	public static String GetFriendsLocationsResult(Map<String, Location> locations){

		StringBuilder sb = new StringBuilder();

		OpenTag(sb, "result");
		for(String username: locations.keySet()){
			OpenTag(sb, "member");
			SimpleTag(sb, "username", username);
			Location(sb, locations.get(username));
			CloseTag(sb, "member");
		}
		CloseTag(sb, "result");

		System.out.println(sb.toString());
		return sb.toString();
	}



	private static void Group(StringBuilder sb, Group group){

		OpenTag(sb, "group");

		SimpleTag(sb, "id", group.groupId + "");

		Cafe(sb, group.cafe);

		OpenTag(sb, "organiser");
		SimpleTag(sb, "member", group.groupInitiator);
		CloseTag(sb, "organiser");

		OpenTag(sb, "attending");
		for(String member: group.attending){
			SimpleTag(sb, "member", member);
		}
		CloseTag(sb, "attending");

		OpenTag(sb, "pending");
		for(String member: group.pending){
			SimpleTag(sb, "member", member);
		}
		CloseTag(sb, "pending");

		OpenTag(sb, "declined");
		for(String member: group.declined){
			SimpleTag(sb, "member", member);
		}
		CloseTag(sb, "declined");

		CloseTag(sb, "group");
	}


	private static void Location(StringBuilder sb, Location location){
		OpenTag(sb, "location");
		SimpleTag(sb, "lat", location.getLat() + "");
		SimpleTag(sb, "lon", location.getLon() + "");
		CloseTag(sb, "location");
	}

	private static void Cafe(StringBuilder sb, Cafe cafe){
		OpenTag(sb, "cafe");
		SimpleTag(sb, "name", cafe.name);
		SimpleTag(sb, "lat", cafe.lat + "");
		SimpleTag(sb, "lon", cafe.lon + "");
		CloseTag(sb, "cafe");
	}

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

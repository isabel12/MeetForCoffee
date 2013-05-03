
import java.io.StringWriter;
import java.util.Set;

import javax.xml.*;

public class XMLWriter {

	private static int tabIndent;

	public synchronized static String AddFriendResult(String message, Boolean success){
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
		// TODO Auto-generated method stub

		return null;
	}



	private static String Group(StringBuilder sb, Group group){

		OpenTag(sb, "group");

		SimpleTag(sb, "id", group.groupId + "");

		OpenTag(sb, "cafe");
		SimpleTag(sb, "name", group.cafe.name);
		SimpleTag(sb, "lat", group.cafe.lat + "");
		SimpleTag(sb, "lon", group.cafe.lon + "");
		CloseTag(sb, "cafe");

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

		return sb.toString();
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


	public static void main(String[] args){

		System.out.println(AddFriendResult("yip, added", true));

	}




}

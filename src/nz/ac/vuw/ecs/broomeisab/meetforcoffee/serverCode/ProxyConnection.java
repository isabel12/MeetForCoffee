package nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode;

import java.net.Authenticator;
import java.net.PasswordAuthentication;


public class ProxyConnection {

	private final static String authUser = "broomeisab";
	private final static String authPassword = "Bottletops12";



	public static void ConnectToProxy(){


		System.setProperty("http.proxyHost", "www-cache.ecs.vuw.ac.nz");
		System.setProperty("http.proxyPort", "8080");
		System.setProperty("http.proxyUser", authUser);
		System.setProperty("http.proxyPassword", authPassword);


		System.setProperty("https.proxyHost", "www-cache.ecs.vuw.ac.nz");
		System.setProperty("https.proxyPort", "8080");
		System.setProperty("https.proxyUser", authUser);
		System.setProperty("https.proxyPassword", authPassword);

		Authenticator.setDefault(
		  new Authenticator() {
		    public PasswordAuthentication getPasswordAuthentication() {
		      return new PasswordAuthentication(authUser, authPassword.toCharArray());
		    }
		  }
		);
	}



}

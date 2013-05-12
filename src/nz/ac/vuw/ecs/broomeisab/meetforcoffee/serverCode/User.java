package nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode;


import java.io.Serializable;


public class User implements Serializable {


	private String username;
	private Location location;


	public User(String username, Location location){
		this.username = username;
		this.location = location;
	}

	public void SetLocation(double lat, double lon){
		this.location.setLat(lat);
		this.location.setLon(lon);
	}

	public double GetLon(){
		return this.location.getLon();
	}

	public double GetLat(){
		return this.location.getLat();
	}

	public String GetUsername(){
		return this.username;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", location=" + location + "]";
	}
	
}

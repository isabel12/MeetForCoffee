import java.awt.Point;


public class User {


	private int id;
	private String username;
	private double lat;
	private double lon;


	public User(int id, String username, double lat, double lon){
		this.id = id;
		this.username = username;
		this.lat = lat;
		this.lon = lon;
	}

	public void SetLocation(double lat, double lon){
		this.lat = lat;
		this.lon = lon;
	}

	public double GetLon(){
		return this.lon;
	}

	public double GetLat(){
		return this.lat;
	}



}

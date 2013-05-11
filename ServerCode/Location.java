import java.io.Serializable;


public class Location implements Serializable{

	private double lat;
	private double lon;

	public Location(double lat, double lon){
		this.lat = lat;
		this.lon = lon;
	}

	public void setLatLon(double lat, double lon){
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

}

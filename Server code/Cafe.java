import java.io.Serializable;


public class Cafe implements Serializable {

	public double lat;
	public double lon;
	public String name;
	public String id;

	public Cafe(String id, String name, double lat, double lon){
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}

	public Cafe() {

	}

}

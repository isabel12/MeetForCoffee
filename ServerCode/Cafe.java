import java.io.Serializable;


public class Cafe implements Serializable {

	public Location location;
	public String name;
	public String id;

	public Cafe(String id, String name, Location location){
		this.id = id;
		this.name = name;
		this.location = location;
	}

	public Cafe() {

	}

}

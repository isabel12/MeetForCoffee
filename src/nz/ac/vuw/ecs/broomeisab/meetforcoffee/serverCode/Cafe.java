package nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode;


import java.io.Serializable;


public class Cafe implements Serializable {

	public Location location;
	public String name;
	public String id;
	private static int tabIndent;

	public Cafe(String id, String name, Location location){
		this.id = id;
		this.name = name;
		this.location = location;
	}

	public Cafe() {

	}

	@Override
	public String toString() {
		return "Cafe [location=" + location + ", name=" + name + ", id=" + id
				+ "]";
	}
	
	public String toXML(){
		return XMLWriter.GetCafe(this);
	}	

}

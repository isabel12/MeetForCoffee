package nz.ac.vuw.ecs.broomeisab.meetforcoffee;

import java.util.HashMap;
import java.util.Map;

import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Cafe;
import nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode.Location;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLLocationHandler extends DefaultHandler {
	
	
	
	private static final String LOCATION = "location";
	private static final String LAT = "lat";
	private static final String LON = "lng";

	private Location location;
	private StringBuilder builder;

	public Location getLocation(){
		return this.location;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);
		if (this.location != null){
			if (name.equalsIgnoreCase(LAT)){
				location.setLat(Double.parseDouble(builder.toString()));
			} else if (name.equalsIgnoreCase(LON)){
				location.setLon(Double.parseDouble(builder.toString()));
			} 
			builder.setLength(0);
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (name.equalsIgnoreCase(LOCATION)){
			this.location = new Location(0,0);
		}
	}
	
	

}

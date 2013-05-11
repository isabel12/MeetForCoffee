package nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class XMLCafeHandler extends DefaultHandler{

	private static final String ITEM = "result";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String LAT = "lat";
	private static final String LON = "lng";

	private Map<String, Cafe> cafes;
	private Cafe currentCafe;
	private StringBuilder builder;

	public Map<String, Cafe> getCafes(){
		return this.cafes;
	}
	
	public Cafe getCafe(){
		return currentCafe;
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
		if (this.currentCafe != null){
			if (name.equalsIgnoreCase(NAME)){
				currentCafe.name = builder.toString();
			} else if (name.equalsIgnoreCase(ID)){
				currentCafe.id = builder.toString();
			} else if (name.equalsIgnoreCase(LAT)){
				currentCafe.lat = Double.parseDouble(builder.toString());
			} else if (name.equalsIgnoreCase(LON)){
				currentCafe.lon = Double.parseDouble(builder.toString());
			} else if (name.equalsIgnoreCase(ITEM)){
				cafes.put(currentCafe.id, currentCafe);
			}
			builder.setLength(0);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		cafes = new HashMap<String, Cafe>();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (name.equalsIgnoreCase(ITEM)){
			this.currentCafe = new Cafe();
		}
	}

}

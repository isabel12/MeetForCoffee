import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;



public class XMLParser {



	public static Map<Integer, Cafe> LoadCafes(String xml){



		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());


		Map<Integer, Cafe> cafes = null;


		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();



		saxParser.parse(inputStream, handler);

		// return the cafes
		return null;

	}




}

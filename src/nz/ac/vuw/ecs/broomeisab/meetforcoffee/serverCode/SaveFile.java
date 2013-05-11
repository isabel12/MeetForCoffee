package nz.ac.vuw.ecs.broomeisab.meetforcoffee.serverCode;

import java.io.File;
import java.util.Map;


public class SaveFile <key, value> {

	private File file;
	private String filename;
	private Map<key, value> collection;

	public SaveFile(String filename, File file, Map<key, value> collection){
		this.file = file;
		this.filename = filename;
		this.collection = collection;
	}

	public File getFile() {
		return file;
	}

	public String getFilename() {
		return filename;
	}

	public Map<key, value> getCollection() {
		return collection;
	}

}

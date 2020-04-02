package de.uniba.pi.dataobjects;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Applications {
	public List<ApplicationRepository> getApplications() {
		return applications;
	}

	public void setApplications(List<ApplicationRepository> applications) {
		this.applications = applications;
	}

	public HashMap<String, ApplicationRepository> getMap() {
		return map;
	}

	private List<ApplicationRepository> applications = new LinkedList<ApplicationRepository>();
	private HashMap<String, ApplicationRepository> map = new HashMap<String, ApplicationRepository>();

	@SuppressWarnings("unchecked")
	public void deserializeApplications(String source) {
		try (FileInputStream fis = new FileInputStream(source); ObjectInputStream ois = new ObjectInputStream(fis);) {
			applications = (List<ApplicationRepository>) ois.readObject();
			map = new HashMap<String, ApplicationRepository>();
			for (ApplicationRepository app : applications) {
				map.put(app.getHtml_url(), app);
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new IllegalArgumentException("Could not deserialize file " + source, e);
		}

	}

	public void serializeApplications(String target) throws IOException {
		FileOutputStream fos = new FileOutputStream(target);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(applications);
		oos.close();
		fos.close();

	}

}

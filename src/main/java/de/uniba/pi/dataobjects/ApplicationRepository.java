package de.uniba.pi.dataobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApplicationRepository implements Serializable {

	private static final long serialVersionUID = 1L;
	private String html_url;
	private List<String> files = new ArrayList<>();
	private final List<String> paths = new ArrayList<>();
	private final List<String> yamlFiles = new ArrayList<>();
	private int stars;

	public String getHtml_url() {
		return html_url;
	}

	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}

	public List<String> getFiles() {
		return files;
	}

	public void addFile(String file) {
		files.add(file);
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public void addPath(String filePath) {
		paths.add(filePath);
	}

	public List<String> getPaths() {
		return paths;
	}

	public List<String> getYamlFiles() {
		return yamlFiles;
	}

	@Override
	public String toString() {
		return html_url +
				System.lineSeparator() +
				String.join("; ", paths) +
				System.lineSeparator() +
				stars;
	}

	@Override
	public int hashCode() {
		return html_url.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ApplicationRepository other = (ApplicationRepository) obj;
		return this.html_url.equals(other.getHtml_url());
	}

	public void addServerlessFile(String contentOfServerlessFile) {
		this.yamlFiles.add(contentOfServerlessFile);
	}

}

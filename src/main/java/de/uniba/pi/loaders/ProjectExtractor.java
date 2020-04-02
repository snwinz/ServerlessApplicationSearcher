package de.uniba.pi.loaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.uniba.pi.applicationsearcher.Main;
import de.uniba.pi.dataobjects.ApplicationRepository;

public class ProjectExtractor {
	JsonParser parser = new JsonParser();
	private List<ApplicationRepository> applications = new LinkedList<ApplicationRepository>();
	private HashMap<String, ApplicationRepository> map = new HashMap<String, ApplicationRepository>();

	public void setApplications(List<ApplicationRepository> applications) {
		this.applications = applications;
		for (ApplicationRepository app : applications) {
			map.put(app.getHtml_url(), app);
		}
	}

	public List<ApplicationRepository> getApplications() {
		return applications;
	}

	public void getInformationOfProjects(String baseOfFilename, int start, int end) throws IOException {
		for (int listNumber = start; listNumber <= end; listNumber++) {
			String dataStorageFile = baseOfFilename + listNumber + ".txt";
			System.out.printf("Get information for %s%n", dataStorageFile);
			String storedData = Files.readString(Path.of(dataStorageFile));
			JsonObject storedDataAsJson = parser.parse(storedData).getAsJsonObject();
			JsonArray items = storedDataAsJson.getAsJsonArray("items");
			for (JsonElement element : items) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String htmlURL = getHtmlURL(element);
				System.out.println("htmlUrl: " + htmlURL);
				String infrastructureFile = element.getAsJsonObject().get("name").toString();
				String urlRef = extractUrlRef(element);
				String contentOfServerlessFile = loadContentOfServerlessFile(urlRef);
				String filePath = element.getAsJsonObject().get("path").toString();

				String stars = getStars(element);
				addProjectToCollection(infrastructureFile, contentOfServerlessFile, filePath, htmlURL, stars);
			}

		}
	}

	private void addProjectToCollection(String infrastructureFile, String contentOfServerlessFile, String filePath,
			String html_url, String stars) {
		if (map.containsKey(html_url)) {
			ApplicationRepository application = map.get(html_url);
			application.addFile(infrastructureFile);
			application.addPath(filePath);
			application.addServerlessFile(contentOfServerlessFile);
		} else {
			ApplicationRepository application = new ApplicationRepository();
			application.addFile(infrastructureFile);
			application.addPath(filePath);
			application.addServerlessFile(contentOfServerlessFile);
			application.setHtml_url(html_url);
			application.setStars(Integer.valueOf(stars));
			applications.add(application);
			map.put(html_url, application);
		}
	}

	private String loadContentOfServerlessFile(String urlRef) {
		String downloadURL = extractDownloadURLofServerlessFile(urlRef);
		String contentOfServerlessFile = Main.gitHubRepositoryImplRaw.getContentOfUrl(downloadURL);
		return contentOfServerlessFile;
	}

	private String extractDownloadURLofServerlessFile(String urlRef) {
		String urlContent = Main.gitHubRepositoryImplRaw.getContentOfUrl(urlRef);
		JsonObject urlContentJson = parser.parse(urlContent).getAsJsonObject();
		String downloadURL = urlContentJson.getAsJsonObject().get("download_url").toString();
		downloadURL = downloadURL.replaceAll("\"", "");
		return downloadURL;
	}

	private String extractUrlRef(JsonElement element) {
		String urlRef = element.getAsJsonObject().get("url").toString();
		urlRef = urlRef.replaceAll("\"", "");
		return urlRef;
	}

	private String getHtmlURL(JsonElement element) {
		JsonObject repositoryObject = element.getAsJsonObject().getAsJsonObject("repository");
		return repositoryObject.get("html_url").toString().replaceAll("\"", "");
	}

	private String getStars(JsonElement element) {
		JsonObject repositoryObject = element.getAsJsonObject().getAsJsonObject("repository");
		String starUrl = repositoryObject.get("url").toString();
		starUrl = starUrl.replaceAll("\"", "");
		String jsonStars = Main.gitHubRepositoryImplRaw.getContentOfUrl(starUrl);
		JsonObject jsonStarObject = parser.parse(jsonStars).getAsJsonObject();
		return jsonStarObject.get("stargazers_count").toString().replaceAll("\"", "");
	}

}

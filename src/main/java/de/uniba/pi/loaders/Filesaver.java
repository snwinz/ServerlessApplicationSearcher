package de.uniba.pi.loaders;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Filesaver {

	private final GitHubRepositoryImplRaw gitHubRepositoryImplRaw;
	private final static JsonParser parser = new JsonParser();

	public Filesaver(GitHubRepositoryImplRaw gitHubRepositoryImplRaw) {
		this.gitHubRepositoryImplRaw = gitHubRepositoryImplRaw;
	}

	public void getFilesForFilename(String[] searchKeysofFile, String filenameBase) throws IOException {
		while (deleteMostUnregularFile(filenameBase)) {
			String searchKey = createSearchKey(searchKeysofFile);
			for (int pageNumber = 1; pageNumber <= 10; pageNumber++) {
				System.out.println(pageNumber);
				String url = String.format("https://api.github.com/search/code?page=%d&per_page=100&q=%s", pageNumber,
						searchKey);
				String fileName = filenameBase + (pageNumber) + ".txt";
				getAndSaveData(url, fileName);
			}
		}

	}

	private boolean deleteMostUnregularFile(String filenameBase) {

		Map<String, List<Path>> occurences = new HashMap<String, List<Path>>();
		countTotalCountOfFiles(filenameBase, occurences);
		// deleteFile
		List<List<Path>> sortedPaths = occurences.values().stream().sorted((a, b) -> a.size() - b.size())
				.collect(Collectors.toList());

		return isFileDeleted(sortedPaths);

	}

	private boolean isFileDeleted(List<List<Path>> sortedPaths) {
		if (sortedPaths.size() == 0) {
			return true;
		}
		if (sortedPaths.size() > 1) {
			for (Path pathToDelete : sortedPaths.get(0)) {
				try {
					System.out.println("Delete: " + pathToDelete.getFileName());
					Files.delete(pathToDelete);
				} catch (IOException e) {
					System.err.print("File could not be deleted: " + pathToDelete.toString());
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private void countTotalCountOfFiles(String filenameBase, Map<String, List<Path>> occurences) {
		for (int i = 0; i < 10; i++) {
			Path path = Path.of(filenameBase + (i + 1) + ".txt");
			try {
				List<String> lines = Files.readAllLines(path);
				if (lines.size() > 1) {

					String totalCountLine = lines.get(1);
					List<Path> pathsForKey = occurences.get(totalCountLine);
					if (pathsForKey == null) {
						pathsForKey = new ArrayList<Path>();
						occurences.put(totalCountLine, pathsForKey);
					}
					pathsForKey.add(path);
				}
			} catch (IOException e) {
				System.err.println("not possible to read file: " + path.getFileName());
			}
		}
	}

	private String createSearchKey(String[] searchKeysofFile) {
		StringBuilder searchKey = new StringBuilder("");
		for (String key : searchKeysofFile) {
			searchKey.append("+");
			searchKey.append(key);
		}
		return searchKey.toString();
	}

	private void getAndSaveData(String url, String fileName) {

		Path path = Path.of(fileName);
		if (Files.notExists((path))) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String githubResult = gitHubRepositoryImplRaw.getContentOfUrl(url);
			githubResult = formatRawString(githubResult);
			saveResult(githubResult, path);
		}
	}

	private static String formatRawString(String result) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement el = parser.parse(result);
		result = gson.toJson(el);
		return result;
	}

	private static void saveResult(String resultOfQuery, Path path) {
		System.out.println("Save: " + path.toString());
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			writer.write(resultOfQuery);
			writer.close();
		} catch (IOException e) {
			System.err.print("Saving was not possible");
		}
	}

}

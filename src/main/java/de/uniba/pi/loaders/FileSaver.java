package de.uniba.pi.loaders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class FileSaver {

    private final GitHubRepositoryImplRaw gitHubRepositoryImplRaw;

    public FileSaver(GitHubRepositoryImplRaw gitHubRepositoryImplRaw) {
        this.gitHubRepositoryImplRaw = gitHubRepositoryImplRaw;
    }

    public void getFilesForFilename(String[] searchKeysOfFile, String filenameBase, int maxNumberOfPages) {
        while (isFileMissing(filenameBase, maxNumberOfPages) || deleteMostIrregularFile(filenameBase)) {
            String searchKey = createSearchKey(searchKeysOfFile);
            for (int pageNumber = 1; pageNumber <= maxNumberOfPages; pageNumber++) {
                System.out.println(pageNumber);
                String url = String.format("https://api.github.com/search/code?page=%d&per_page=100&q=%s", pageNumber,
                        searchKey);
                String fileName = filenameBase + (pageNumber) + ".txt";
                getAndSaveData(url, fileName);
            }
        }

    }

    private boolean isFileMissing(String filenameBase, int maxNumberOfPages) {
        int count = 0;
        for (int i = 0; i < maxNumberOfPages; i++) {
            Path path = Path.of(filenameBase + (i + 1) + ".txt");
            if (Files.exists(path)) {
                count++;
            }
        }
        return count != maxNumberOfPages;
    }

    private boolean deleteMostIrregularFile(String filenameBase) {

        Map<String, List<Path>> occurrences = new HashMap<>();
        countTotalCountOfFiles(filenameBase, occurrences);
        // deleteFile
        List<List<Path>> sortedPaths = occurrences.values().stream().sorted(Comparator.comparingInt(List::size))
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
                    System.err.print("File could not be deleted: " + pathToDelete);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void countTotalCountOfFiles(String filenameBase, Map<String, List<Path>> occurrences) {
        for (int i = 0; i < 10; i++) {
            Path path = Path.of(filenameBase + (i + 1) + ".txt");
            try {
                List<String> lines = Files.readAllLines(path);
                if (lines.size() > 1) {
                    String totalCountLine = lines.get(1);
                    List<Path> pathsForKey = occurrences.computeIfAbsent(totalCountLine, k -> new ArrayList<>());
                    pathsForKey.add(path);
                }
            } catch (IOException e) {
                System.err.println("not possible to read file: " + path.getFileName());
            }
        }
    }

    private String createSearchKey(String[] searchKeysOfFile) {
        StringBuilder searchKey = new StringBuilder();
        for (String key : searchKeysOfFile) {
            searchKey.append("+");
            searchKey.append(key);
        }
        return searchKey.toString();
    }

    private void getAndSaveData(String url, String fileName) {

        Path path = Path.of(fileName);
        if (Files.notExists((path))) {
            String githubResult = null;
            try {
                githubResult = gitHubRepositoryImplRaw.getContentOfUrl(url);
                githubResult = formatRawString(githubResult);
                saveResult(githubResult, path);
                Thread.sleep(100000);
            } catch (IOException e) {
                System.err.println("URL: " + "could not be fetched");
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private static String formatRawString(String result) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement el = JsonParser.parseString(result);
        result = gson.toJson(el);
        return result;
    }

    private static void saveResult(String resultOfQuery, Path path) {
        System.out.println("Save: " + path.toString());
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(resultOfQuery);
        } catch (IOException e) {
            System.err.print("Saving was not possible");
        }
    }

}

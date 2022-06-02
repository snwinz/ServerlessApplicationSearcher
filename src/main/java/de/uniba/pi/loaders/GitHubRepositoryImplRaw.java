package de.uniba.pi.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GitHubRepositoryImplRaw {

    public GitHubRepositoryImplRaw(String token) {
        this.token = token;
    }

    private final String token;

    public String getContentOfUrl(String url) throws IOException {
        HttpURLConnection connection;
        String retVal;
        URL endpoint = new URL(url);
        connection = (HttpURLConnection) endpoint.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "token " + token);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
                result.append(System.lineSeparator());
            }
            retVal = result.toString();
            connection.disconnect();
            reader.close();
            return retVal;
        }

    }
}

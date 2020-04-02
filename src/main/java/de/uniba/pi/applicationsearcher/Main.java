package de.uniba.pi.applicationsearcher;

import java.io.IOException;

import com.google.gson.JsonParser;

import de.uniba.pi.dataobjects.Applications;
import de.uniba.pi.evaluation.FileAnalyzer;
import de.uniba.pi.evaluation.Helper;
import de.uniba.pi.loaders.Filesaver;
import de.uniba.pi.loaders.GitHubRepositoryImplRaw;
import de.uniba.pi.loaders.ProjectExtractor;

public class Main {
	public static GitHubRepositoryImplRaw gitHubRepositoryImplRaw = new GitHubRepositoryImplRaw(
			System.getenv("GITHUB_API_TOKEN"));
	static JsonParser parser = new JsonParser();

	public static void main(String[] args) throws IOException {

		// Search files
		Filesaver filesaver = new Filesaver(gitHubRepositoryImplRaw);
		String filenameBase = "searchResult";
		String[] searchKeys = { "aws", "handler", "filename:serverless.yml" };
		filesaver.getFilesForFilename(searchKeys, filenameBase);

		// Add information to projects
		ProjectExtractor applicationExtractor = new ProjectExtractor();
		applicationExtractor.getInformationOfProjects(filenameBase, 1, 10);

		// Save or Load results of previous runs
		Applications applications = new Applications();
		applications.setApplications(applicationExtractor.getApplications());
		applications.serializeApplications("allProjects");
		applications.deserializeApplications("allProjects");
		// applications.update();

		// Create some stuff to get some backups
		Helper helper = new Helper(applications.getApplications());
		helper.createGitCloneCommands();
		helper.saveServerlessFiles();
		helper.createCSVwithProjectInformation();

		// Analyze results
		FileAnalyzer analyzer = new FileAnalyzer(applications.getApplications());
		analyzer.evaluateRuntimeEnvironments();

	}

}

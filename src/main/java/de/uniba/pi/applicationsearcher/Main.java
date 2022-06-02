package de.uniba.pi.applicationsearcher;

import de.uniba.pi.dataobjects.Applications;
import de.uniba.pi.evaluation.FileAnalyzer;
import de.uniba.pi.evaluation.Helper;
import de.uniba.pi.loaders.FileSaver;
import de.uniba.pi.loaders.GitHubRepositoryImplRaw;
import de.uniba.pi.loaders.ProjectExtractor;

import java.io.IOException;

public class Main {
    public static final GitHubRepositoryImplRaw gitHubRepositoryImplRaw = new GitHubRepositoryImplRaw(
            System.getenv("GITHUB_API_TOKEN"));

    public static void main(String[] args) throws IOException {

        // Search files
        FileSaver filesaver = new FileSaver(gitHubRepositoryImplRaw);
        String filenameBase = "searchResult";
        String[] searchKeys = {"aws", "handler", "filename:serverless.yml"};
        filesaver.getFilesForFilename(searchKeys, filenameBase, 10);

        // Add information to projects
        ProjectExtractor applicationExtractor = new ProjectExtractor();
        applicationExtractor.getInformationOfProjects(filenameBase, 1, 10);

        // Save or Load results of previous runs
        Applications applications = new Applications();
        applications.setApplications(applicationExtractor.getApplications());
        applications.serializeApplications("allProjects");

        applications.deserializeApplications("allProjects");

        // Create some stuff to get some backups
        Helper helper = new Helper(applications.getApplications());
        helper.createGitCloneCommands();
        helper.saveServerlessFiles();
        helper.createCSVWithProjectInformation();

        // Analyze results
        FileAnalyzer analyzer = new FileAnalyzer(applications.getApplications());
        analyzer.evaluateRuntimeEnvironments();

    }

}

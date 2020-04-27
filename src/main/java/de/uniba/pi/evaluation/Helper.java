package de.uniba.pi.evaluation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.uniba.pi.dataobjects.ApplicationRepository;
import de.uniba.pi.yamls.Serverless;

public class Helper {
	private List<ApplicationRepository> applications;

	public Helper(List<ApplicationRepository> applications) {
		this.applications = applications.stream().sorted((a, b) -> b.getStars() - a.getStars())
				.collect(Collectors.toList());
	}

	public String createGitCloneCommands() {

		StringBuilder result = new StringBuilder();
		result.append("#!/bin/sh").append(System.lineSeparator());
		int counter = 0;
		for (ApplicationRepository app : applications) {
			String folder = getFormattedFolder(counter);
			result.append("mkdir ").append(folder).append(System.lineSeparator());
			result.append("cd ").append(folder).append(System.lineSeparator());
			result.append("git clone ").append(app.getHtml_url()).append(System.lineSeparator());
			result.append("cd ..").append(System.lineSeparator());

			counter++;
		}
		Path script = Path.of("loadGitRepos.sh");
		try {
			Files.writeString(script, result.toString(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.err.printf("Not possible to write file: %s%n", script.getFileName());
		}
		return result.toString();
	}

	private String getFormattedFolder(int counter) {
		if (counter < 10) {
			return "00" + counter;
		}
		if (counter < 100) {
			return "0" + counter;
		}
		return String.valueOf(counter);
	}

	public void saveServerlessFiles() {
		String target = "files/serverless%s.yml";
		System.out.printf("Save serverless.yml files in %s%n", target);
		applications = applications.stream().sorted((a, b) -> b.getStars() - a.getStars()).collect(Collectors.toList());
		int counter = 0;
		for (ApplicationRepository app : applications) {
			for (String content : app.getYamlFiles()) {
				counter++;
				Path serverlessFile = Path.of(String.format(target, counter));
				try {
					Files.createDirectories(serverlessFile.getParent());
					Files.writeString(serverlessFile, content, StandardOpenOption.CREATE,
							StandardOpenOption.TRUNCATE_EXISTING);
				} catch (IOException e) {
					System.err.printf("Not possible to write file: %s%n", serverlessFile);
				}
			}
		}
	}

	public String createCSVwithProjectInformation() {
		StringBuilder result = new StringBuilder();
		int counter = 0;
		for (var app : applications) {
			result.append(app.getHtml_url()).append(";").append(app.getStars()).append(System.lineSeparator());
			for (int i = 0; i < app.getPaths().size(); i++) {
				String yamlFile = app.getYamlFiles().get(i);
				String yamlPath = app.getPaths().get(i);
				result.append(";").append(yamlPath).append(System.lineSeparator());
				System.out.println(counter++);
				app.getHtml_url();
				System.out.println(yamlPath);
				System.out.println(app.getHtml_url());
				result.append(getFunctionInformation(yamlFile, app.getHtml_url()));
			}
		}

		Path path = Path.of("functions.csv");
		try {
			Files.writeString(path, result, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.err.printf("could not write result to: %s%n", path.getFileName());
		}
		return result.toString();
	}

	private StringBuilder getFunctionInformation(String yamlFile, String appName) {
		StringBuilder result = new StringBuilder();

		Optional<Serverless> serverlessOpt = YamlRead.readYaml(yamlFile, appName);
		if (serverlessOpt.isPresent()) {
			Serverless serverless = serverlessOpt.get();
			if (isNodeProject(serverless)) {
				if (serverless.getFunctions() != null) {
					for (var entry : serverless.getFunctions().entrySet()) {
						result.append(";;").append(entry.getKey()).append(";").append(entry.getValue().getHandler())
								.append(System.lineSeparator());
					}
				}
			} else {
				String runtime = getRuntime(serverless);
				result.append(";;").append(runtime);
			}
		}
		if (serverlessOpt.isEmpty()) {
			result.append(";;").append("file could not be parsed").append(System.lineSeparator());
		}
		result.append(System.lineSeparator());
		return result;
	}

	private String getRuntime(Serverless serverless) {
		if (serverless.getProvider() == null) {
			return "no provider";
		}
		if( serverless.getProvider().getRuntime() == null) {
			return "no runtime";
		}
		return serverless.getProvider().getRuntime();
	}

	private boolean isNodeProject(Serverless serverless) {
		return getRuntime(serverless).trim().contains("node");
	}

}

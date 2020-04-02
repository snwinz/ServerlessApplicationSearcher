package de.uniba.pi.evaluation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.uniba.pi.dataobjects.ApplicationRepository;
import de.uniba.pi.yamls.Serverless;

public class FileAnalyzer {

	private List<ApplicationRepository> applications;

	public FileAnalyzer(List<ApplicationRepository> applications) {
		this.applications = applications;
	}

	public void evaluateRuntimeEnvironments() {

		int numberOfNodejsFiles = 0;
		int numberOfJavaFiles = 0;
		int numberOfPythonFiles = 0;
		int numberOfTotalFiles = 0;
		int numberOfDotnetFiles = 0;
		int numberOfOtherFiles = 0;
		int numberOfNotFound = 0;

		applications = applications.stream().sorted((a, b) -> b.getStars() - a.getStars()).collect(Collectors.toList());

		for (ApplicationRepository app : applications) {
			List<String> yamlFiles = app.getYamlFiles();
			for (String file : yamlFiles) {
				numberOfTotalFiles++;
				Optional<Serverless> serverless = YamlRead.readYaml(file, app.getHtml_url());

				if (serverless.isPresent()) {
					if (serverless.get().getProvider() != null) {
						String runtime = serverless.get().getProvider().getRuntime();
						if (runtime == null) {
							numberOfNotFound++;
						} else if (runtime.contains("node")) {
							numberOfNodejsFiles++;
						} else if (runtime.contains("java")) {
							numberOfJavaFiles++;
						} else if (runtime.contains("python")) {
							numberOfPythonFiles++;
						} else if (runtime.contains("dotnetcore")) {
							numberOfDotnetFiles++;
						} else {
							System.out.println(runtime);
							numberOfOtherFiles++;
						}
					}
				}

			}
		}
		System.out.printf("Number of applications: %s%n", applications.size());
		System.out.printf("Number of nodejs files: %s%n", numberOfNodejsFiles);
		System.out.printf("Number of java files: %s%n", numberOfJavaFiles);
		System.out.printf("Number of python files: %s%n", numberOfPythonFiles);
		System.out.printf("Number of dotnet files: %s%n", numberOfDotnetFiles);
		System.out.printf("Number of other files: %s%n", numberOfOtherFiles);
		System.out.printf("Number of not found: %s%n", numberOfNotFound);
		System.out.printf("Number of total files: %s%n", numberOfTotalFiles);
	}

}

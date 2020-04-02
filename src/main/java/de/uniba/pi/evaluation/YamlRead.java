package de.uniba.pi.evaluation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.scanner.ScannerException;

import de.uniba.pi.yamls.Serverless;

public class YamlRead {

	public static Optional<Serverless> readYaml(String content, String appName) {
		content = content.replaceAll("!", "_");
		Serverless result = null;
		try (InputStream inputstream = new ByteArrayInputStream(content.getBytes());) {
			Representer representer = new Representer();
			representer.getPropertyUtils().setSkipMissingProperties(true);
			Yaml yaml = new Yaml(representer);
			result = yaml.loadAs(inputstream, Serverless.class);
		} catch (IOException e) {
			System.err.printf("Could not create stream for app %s%n", appName);
		} catch (ParserException e) {
			System.err.printf("Could not parse for app %s%n", appName);
		} catch (ScannerException e) {
			System.err.printf("Could not scan for app %s%n", appName);
		}
		return Optional.ofNullable(result);
	}

}

package de.uniba.pi.yamls;

import java.util.Map;

public class Serverless {
	private String service;
	private Provider provider;
	private Map<String, Function> functions;

	public Map<String, Function> getFunctions() {
		return functions;
	}

	public void setFunctions(Map<String, Function> functions) {
		this.functions = functions;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("service: ").append(this.getService()).append(System.lineSeparator());
		builder.append("\truntime: ").append(this.provider.getRuntime()).append(System.lineSeparator());
		if (this.getFunctions() != null) {
			builder.append("\tfunctions: ").append(System.lineSeparator());
			var functions = this.getFunctions();
			for (var entry : functions.entrySet()) {
				builder.append("\t\tname: ").append(entry.getKey()).append(System.lineSeparator());
				builder.append("\t\thandler: ").append(entry.getValue().getHandler()).append(System.lineSeparator());
			}
		}
		builder.append(System.lineSeparator());
		return builder.toString();
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

}

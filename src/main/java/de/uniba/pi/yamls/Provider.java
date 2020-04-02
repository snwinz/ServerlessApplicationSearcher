package de.uniba.pi.yamls;

public class Provider {
	private String runtime;
	private String name;
	private String region;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRuntime() {
		return runtime != null ? runtime.toLowerCase() : null;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
}

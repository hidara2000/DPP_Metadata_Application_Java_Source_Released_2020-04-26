package model;

public class Config {

	private final String hdShimName;

	private final String sdShimName;

	private final String businessLogicVersion;

	private final String shimVersion;

	public Config(String hdShimName, String sdShimName, String businessLogicVersion, String shimVersion) {
		super();
		this.hdShimName = hdShimName;
		this.sdShimName = sdShimName;
		this.businessLogicVersion = businessLogicVersion;
		this.shimVersion = shimVersion;
	}

	public String getHdShimName() {
		return hdShimName;
	}

	public String getBusinessLogicVersion() {
		return businessLogicVersion;
	}

	public String getSdShimName() {
		return sdShimName;
	}

	public String getShimVersion() {
		return shimVersion;
	}
}

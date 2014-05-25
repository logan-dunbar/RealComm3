package com.openbox.realcomm3.utilities.enums;

public enum CompanyCategory
{
	MAIN_CATEGORY("Main Categories", "mainCategoriesFragment"),
	TARGET_MARKET("Target Markets", "targetMarketsFragment"),
	SUB_CATEGORY("Sub Categories", "subCategoriesFragment"),
	CLIENT_SAMPLING("Client Sampling", "clientSamplingFragment"),
	GEOGRAPHIC_MARKET("Geographic Markets", "geographicMarketsFragment");

	private String header;
	private String fragmentTag;

	private CompanyCategory(String header, String fragmentTag)
	{
		this.header = header;
		this.fragmentTag = fragmentTag;
	}

	public String getHeader()
	{
		return header;
	}

	public String getFragmentTag()
	{
		return fragmentTag;
	}
}

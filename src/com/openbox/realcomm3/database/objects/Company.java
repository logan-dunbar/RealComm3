package com.openbox.realcomm3.database.objects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Company")
public class Company implements Serializable
{
	// Generated Serializable UID
	private static final long serialVersionUID = 1566568241666455317L;

	public static final Class<?> ID_CLASS = Integer.class;

	/**********************************************************************************************
	 * Column Names
	 **********************************************************************************************/
	public static final String COMPANY_ID_COLUMN_NAME = "CompanyId";
	public static final String NAME_COLUMN_NAME = "Name";
	public static final String DESCRIPTION_COLUMN_NAME = "Description";
	public static final String SHORT_DESCRIPTION_COLUMN_NAME = "ShortDescription";
	public static final String WEBSITE_COLUMN_NAME = "Website";
	public static final String RELATED_LINKS_COLUMN_NAME = "RelatedLinks";
	public static final String CONFERENCE_NAME_COLUMN_NAME = "ConferenceName";
	public static final String COUNTRY_COLUMN_NAME = "Country";
	public static final String CITY_COLUMN_NAME = "City";
	public static final String STATE_COLUMN_NAME = "State";
	public static final String ADDRESS_1_COLUMN_NAME = "Address1";
	public static final String ADDRESS_2_COLUMN_NAME = "Address2";
	public static final String ADDRESS_3_COLUMN_NAME = "Address3";
	public static final String POSTAL_CODE_COLUMN_NAME = "PostalCode";
	public static final String FACEBOOK_COLUMN_NAME = "Facebook";
	public static final String FACEBOOK_PROFILE_ID_COLUMN_NAME = "FacebookProfileId";
	public static final String TWITTER_COLUMN_NAME = "Twitter";
	public static final String LINKEDIN_COLUMN_NAME = "LinkedIn";
	public static final String MAIN_CATEGORIES_COLUMN_NAME = "MainCategories";
	public static final String SUB_CATEGORIES_COLUMN_NAME = "SubCategories";
	public static final String TARGET_MARKETS_COLUMN_NAME = "TargetMarkets";
	public static final String CLIENT_SAMPLING_COLUMN_NAME = "ClientSampling";
	public static final String GEOGRAPHIC_MARKETS_COLUMN_NAME = "GeographicMarkets";

	/**********************************************************************************************
	 * Database Fields
	 **********************************************************************************************/
	@DatabaseField(id = true, columnName = COMPANY_ID_COLUMN_NAME)
	@SerializedName(COMPANY_ID_COLUMN_NAME)
	private int companyId;

	@DatabaseField(columnName = NAME_COLUMN_NAME)
	@SerializedName(NAME_COLUMN_NAME)
	private String name;

	@DatabaseField(columnName = DESCRIPTION_COLUMN_NAME)
	@SerializedName(DESCRIPTION_COLUMN_NAME)
	private String description;

	@DatabaseField(columnName = SHORT_DESCRIPTION_COLUMN_NAME)
	@SerializedName(SHORT_DESCRIPTION_COLUMN_NAME)
	private String shortDescription;

	@DatabaseField(columnName = WEBSITE_COLUMN_NAME)
	@SerializedName(WEBSITE_COLUMN_NAME)
	private String website;

	@DatabaseField(columnName = RELATED_LINKS_COLUMN_NAME)
	@SerializedName(RELATED_LINKS_COLUMN_NAME)
	private String relatedLinks;

	@DatabaseField(columnName = CONFERENCE_NAME_COLUMN_NAME)
	@SerializedName(CONFERENCE_NAME_COLUMN_NAME)
	private String conferenceName;

	@DatabaseField(columnName = COUNTRY_COLUMN_NAME)
	@SerializedName(COUNTRY_COLUMN_NAME)
	private String country;

	@DatabaseField(columnName = CITY_COLUMN_NAME)
	@SerializedName(CITY_COLUMN_NAME)
	private String city;

	@DatabaseField(columnName = STATE_COLUMN_NAME)
	@SerializedName(STATE_COLUMN_NAME)
	private String state;

	@DatabaseField(columnName = ADDRESS_1_COLUMN_NAME)
	@SerializedName(ADDRESS_1_COLUMN_NAME)
	private String address1;

	@DatabaseField(columnName = ADDRESS_2_COLUMN_NAME)
	@SerializedName(ADDRESS_2_COLUMN_NAME)
	private String address2;

	@DatabaseField(columnName = ADDRESS_3_COLUMN_NAME)
	@SerializedName(ADDRESS_3_COLUMN_NAME)
	private String address3;

	@DatabaseField(columnName = POSTAL_CODE_COLUMN_NAME)
	@SerializedName(POSTAL_CODE_COLUMN_NAME)
	private String postalCode;

	@DatabaseField(columnName = FACEBOOK_COLUMN_NAME)
	@SerializedName(FACEBOOK_COLUMN_NAME)
	private String facebook;

	@DatabaseField(columnName = FACEBOOK_PROFILE_ID_COLUMN_NAME)
	@SerializedName(FACEBOOK_PROFILE_ID_COLUMN_NAME)
	private String facebookProfileId;

	@DatabaseField(columnName = TWITTER_COLUMN_NAME)
	@SerializedName(TWITTER_COLUMN_NAME)
	private String twitter;

	@DatabaseField(columnName = LINKEDIN_COLUMN_NAME)
	@SerializedName(LINKEDIN_COLUMN_NAME)
	private String linkedIn;

	@DatabaseField(columnName = MAIN_CATEGORIES_COLUMN_NAME)
	@SerializedName(MAIN_CATEGORIES_COLUMN_NAME)
	private String mainCategories;

	@DatabaseField(columnName = SUB_CATEGORIES_COLUMN_NAME)
	@SerializedName(SUB_CATEGORIES_COLUMN_NAME)
	private String subCategories;

	@DatabaseField(columnName = TARGET_MARKETS_COLUMN_NAME)
	@SerializedName(TARGET_MARKETS_COLUMN_NAME)
	private String targetMarkets;

	@DatabaseField(columnName = CLIENT_SAMPLING_COLUMN_NAME)
	@SerializedName(CLIENT_SAMPLING_COLUMN_NAME)
	private String clientSampling;

	@DatabaseField(columnName = GEOGRAPHIC_MARKETS_COLUMN_NAME)
	@SerializedName(GEOGRAPHIC_MARKETS_COLUMN_NAME)
	private String geographicMarkets;

	public Company()
	{
		// all persisted classes must define a no-arg constructor with at least package visibility
	}

	/**********************************************************************************************
	 * Property Get and Setters
	 **********************************************************************************************/
	public int getCompanyId()
	{
		return companyId;
	}

	public void setCompanyId(int companyId)
	{
		this.companyId = companyId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getShortDescription()
	{
		return shortDescription;
	}

	public void setShortDescription(String shortDescription)
	{
		this.shortDescription = shortDescription;
	}

	public String getWebsite()
	{
		return website;
	}

	public void setWebsite(String website)
	{
		this.website = website;
	}

	public String getRelatedLinks()
	{
		return relatedLinks;
	}

	public void setRelatedLinks(String relatedLinks)
	{
		this.relatedLinks = relatedLinks;
	}

	public String getConferenceName()
	{
		return conferenceName;
	}

	public void setConferenceName(String conferenceName)
	{
		this.conferenceName = conferenceName;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getAddress1()
	{
		return address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	public String getAddress2()
	{
		return address2;
	}

	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	public String getAddress3()
	{
		return address3;
	}

	public void setAddress3(String address3)
	{
		this.address3 = address3;
	}

	public String getPostalCode()
	{
		return postalCode;
	}

	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}

	public String getFacebook()
	{
		return facebook;
	}

	public void setFacebook(String facebook)
	{
		this.facebook = facebook;
	}

	public String getFacebookProfileId()
	{
		return facebookProfileId;
	}

	public void setFacebookProfileId(String facebookProfileId)
	{
		this.facebookProfileId = facebookProfileId;
	}

	public String getTwitter()
	{
		return twitter;
	}

	public void setTwitter(String twitter)
	{
		this.twitter = twitter;
	}

	public String getLinkedIn()
	{
		return linkedIn;
	}

	public void setLinkedIn(String linkedIn)
	{
		this.linkedIn = linkedIn;
	}

	public String getMainCategories()
	{
		return mainCategories;
	}

	public void setMainCategories(String mainCategories)
	{
		this.mainCategories = mainCategories;
	}

	public String getSubCategories()
	{
		return subCategories;
	}

	public void setSubCategories(String subCategories)
	{
		this.subCategories = subCategories;
	}

	public String getTargetMarkets()
	{
		return targetMarkets;
	}

	public void setTargetMarkets(String targetMarkets)
	{
		this.targetMarkets = targetMarkets;
	}

	public String getClientSampling()
	{
		return clientSampling;
	}

	public void setClientSampling(String clientSampling)
	{
		this.clientSampling = clientSampling;
	}

	public String getGeographicMarkets()
	{
		return geographicMarkets;
	}

	public void setGeographicMarkets(String geographicMarkets)
	{
		this.geographicMarkets = geographicMarkets;
	}
}

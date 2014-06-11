package com.openbox.realcomm.database.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.openbox.realcomm.database.objects.Booth;
import com.openbox.realcomm.database.objects.Company;
import com.openbox.realcomm.database.objects.CompanyLogo;
import com.openbox.realcomm.utilities.enums.CompanyCategory;
import com.openbox.realcomm.utilities.helpers.StringHelper;

public class CompanyModel
{
	private int boothId;
	private int boothNumber;

	private int companyId;
	private String name;
	private String description;
	private String shortDescription;
	private String website;
	private String relatedLinks;
	private String conferenceName;
	private String country;
	private String city;
	private String state;
	private String address1;
	private String address2;
	private String address3;
	private String postalCode;
	private String facebook;
	private String facebookProfileId;
	private String twitter;
	private String linkedIn;
	private String mainCatergories;
	private String subCategories;
	private String targetMarkets;
	private String clientSampling;
	private String geographicMarkets;

	private List<ContactModel> contactList = new ArrayList<>();

	private Bitmap companyLogo;

	public CompanyModel(Booth booth, Company company, CompanyLogo companyLogo, List<ContactModel> contactList)
	{
		if (booth != null)
		{
			this.boothId = booth.getBoothId();
			this.boothNumber = booth.getBoothNumber();
		}

		if (company != null)
		{
			this.companyId = company.getCompanyId();
			this.name = company.getName();
			this.description = company.getDescription();
			this.shortDescription = company.getShortDescription();
			this.website = company.getWebsite();
			this.relatedLinks = company.getRelatedLinks();
			this.conferenceName = company.getConferenceName();
			this.country = company.getCountry();
			this.city = company.getCity();
			this.state = company.getState();
			this.address1 = company.getAddress1();
			this.address2 = company.getAddress2();
			this.address3 = company.getAddress3();
			this.postalCode = company.getPostalCode();
			this.facebook = company.getFacebook();
			this.facebookProfileId = company.getFacebookProfileId();
			this.twitter = company.getTwitter();
			this.linkedIn = company.getLinkedIn();
			this.mainCatergories = company.getMainCategories();
			this.subCategories = company.getSubCategories();
			this.targetMarkets = company.getTargetMarkets();
			this.clientSampling = company.getClientSampling();
			this.geographicMarkets = company.getGeographicMarkets();
		}

		if (companyLogo != null)
		{
			// TODO do sample size decoding
			byte[] logoBytes = companyLogo.getLogoBytes();
			this.companyLogo = BitmapFactory.decodeByteArray(logoBytes, 0, logoBytes.length);
		}

		if (contactList != null)
		{
			this.contactList = contactList;
		}
	}

	public boolean getHasCategories()
	{
		return getHasMainCategories() || getHasSubCategories() || getHasTargetMarkets() || getHasClientSampling() || getHasGeographicMarkets();
	}

	public boolean getHasCategories(CompanyCategory category)
	{
		switch (category)
		{
			case MAIN_CATEGORY:
				return getHasMainCategories();
			case SUB_CATEGORY:
				return getHasSubCategories();
			case TARGET_MARKET:
				return getHasTargetMarkets();
			case CLIENT_SAMPLING:
				return getHasClientSampling();
			case GEOGRAPHIC_MARKET:
				return getHasGeographicMarkets();
			default:
				return false;
		}
	}

	public boolean getHasMainCategories()
	{
		return !StringHelper.isNullOrEmpty(this.mainCatergories);
	}

	public boolean getHasSubCategories()
	{
		return !StringHelper.isNullOrEmpty(this.subCategories);
	}

	public boolean getHasTargetMarkets()
	{
		return !StringHelper.isNullOrEmpty(this.targetMarkets);
	}

	public boolean getHasClientSampling()
	{
		return !StringHelper.isNullOrEmpty(this.clientSampling);
	}

	public boolean getHasGeographicMarkets()
	{
		return !StringHelper.isNullOrEmpty(this.geographicMarkets);
	}

	public boolean getHasConferenceName()
	{
		return !StringHelper.isNullOrEmpty(this.conferenceName);
	}

	public boolean getHasAddress()
	{
		return getHasCountry() || getHasCity() || getHasState() || getHasAddress1() || getHasAddress2() || getHasAddress3() || getHasPostalCode();
	}

	public boolean getHasCountry()
	{
		return !StringHelper.isNullOrEmpty(this.country);
	}

	public boolean getHasCity()
	{
		return !StringHelper.isNullOrEmpty(this.city);
	}

	public boolean getHasState()
	{
		return !StringHelper.isNullOrEmpty(this.state);
	}

	public boolean getHasAddress1()
	{
		return !StringHelper.isNullOrEmpty(this.address1);
	}

	public boolean getHasAddress2()
	{
		return !StringHelper.isNullOrEmpty(this.address2);
	}

	public boolean getHasAddress3()
	{
		return !StringHelper.isNullOrEmpty(this.address3);
	}

	public boolean getHasPostalCode()
	{
		return !StringHelper.isNullOrEmpty(this.postalCode);
	}

	public boolean getHasContacts()
	{
		return this.contactList.size() > 0;
	}

	public boolean getHasLinks()
	{
		return getHasWebsite() || getHasRelatedLinks();
	}

	public boolean getHasWebsite()
	{
		return !StringHelper.isNullOrEmpty(this.website);
	}

	public boolean getHasRelatedLinks()
	{
		return !StringHelper.isNullOrEmpty(this.relatedLinks);
	}

	public boolean getHasSocialNetworks()
	{
		return getHasFacebook() || getHasTwitter() || getHasLinkedIn();
	}

	public boolean getHasFacebook()
	{
		return !StringHelper.isNullOrEmpty(this.facebook);
	}

	public boolean getHasFacebookProfileId()
	{
		return !StringHelper.isNullOrEmpty(this.facebookProfileId);
	}

	public boolean getHasTwitter()
	{
		return !StringHelper.isNullOrEmpty(this.twitter);
	}

	public boolean getHasLinkedIn()
	{
		return !StringHelper.isNullOrEmpty(this.linkedIn);
	}

	public List<Integer> getContactIds()
	{
		List<Integer> contactIds = new ArrayList<>();
		for (ContactModel contact : this.contactList)
		{
			contactIds.add(contact.getContactId());
		}

		return contactIds;
	}

	public ContactModel getContact(int contactId)
	{
		for (ContactModel contact : this.contactList)
		{
			if (contact.getContactId() == contactId)
			{
				return contact;
			}
		}

		return null;
	}

	public String getFormattedAddress()
	{
		StringBuilder sb = new StringBuilder();
		boolean addNewLine = false;

		if (getHasAddress1() || getHasAddress2() || getHasAddress3())
		{
			List<String> addressLinesList = new ArrayList<>();
			addressLinesList.add(getAddress1());
			addressLinesList.add(getAddress2());
			addressLinesList.add(getAddress3());

			String addressLinesString = StringHelper.join(addressLinesList, StringHelper.NEW_LINE);
			sb.append(addressLinesString);
			addNewLine = true;
		}

		if (getHasCity() || getHasState() || getHasPostalCode())
		{
			if (addNewLine)
			{
				sb.append(StringHelper.NEW_LINE);
			}

			List<String> cityStateCodeList = new ArrayList<>();
			cityStateCodeList.add(getCity());
			cityStateCodeList.add(getState());
			cityStateCodeList.add(getPostalCode());

			String cityStateCodeString = StringHelper.join(cityStateCodeList, ", ");
			sb.append(cityStateCodeString);
			addNewLine = true;
		}

		if (getHasCountry())
		{
			if (addNewLine)
			{
				sb.append(StringHelper.NEW_LINE);
			}

			sb.append(getCountry());
		}

		return sb.toString();
	}

	public String getFormattedLinks()
	{
		StringBuilder sb = new StringBuilder();

		if (getHasWebsite())
		{
			sb.append(this.website);
		}

		if (getHasWebsite() && getHasRelatedLinks())
		{
			sb.append(StringHelper.NEW_LINE);
			sb.append(StringHelper.NEW_LINE);
		}

		if (getHasRelatedLinks())
		{
			String[] relatedLinksArray = this.relatedLinks.split("[\\s]*,[\\s]*");
			sb.append(StringHelper.join(Arrays.asList(relatedLinksArray), StringHelper.NEW_LINE));
		}

		return sb.toString();
	}

	public String getCompanyCategoryDetails(CompanyCategory companyCategory)
	{
		switch (companyCategory)
		{
			case MAIN_CATEGORY:
				return getMainCatergories();
			case SUB_CATEGORY:
				return getSubCategories();
			case TARGET_MARKET:
				return getTargetMarkets();
			case CLIENT_SAMPLING:
				return getClientSampling();
			case GEOGRAPHIC_MARKET:
				return getGeographicMarkets();
			default:
				return null;
		}
	}

	public int getBoothId()
	{
		return boothId;
	}

	public void setBoothId(int boothId)
	{
		this.boothId = boothId;
	}

	public int getBoothNumber()
	{
		return boothNumber;
	}

	public void setBoothNumber(int boothNumber)
	{
		this.boothNumber = boothNumber;
	}

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

	public String getMainCatergories()
	{
		return mainCatergories;
	}

	public void setMainCatergories(String mainCatergories)
	{
		this.mainCatergories = mainCatergories;
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

	public List<ContactModel> getContactList()
	{
		return contactList;
	}

	public void setContactList(List<ContactModel> contactList)
	{
		this.contactList = contactList;
	}

	public Bitmap getCompanyLogo()
	{
		return companyLogo;
	}

	public void setCompanyLogo(Bitmap companyLogo)
	{
		this.companyLogo = companyLogo;
	}
}

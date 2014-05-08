package com.openbox.realcomm3.database.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.openbox.realcomm3.database.objects.Booth;
import com.openbox.realcomm3.database.objects.Company;
import com.openbox.realcomm3.database.objects.CompanyLogo;

public class CompanyModel
{

	private int boothId;
	private int boothNumber;

	private int companyId;
	private String name;
	private String description;
	private String website;
	private String city;
	private String state;
	private String address1;
	private String address2;
	private String address3;
	private String postalCode;
	private String facebook;
	private String twitter;
	private String linkedIn;
	private String mainCatergories;
	private String subCategories;
	private String targetMarkets;
	private String geographicMarkets;

	private Bitmap companyLogo;

	public CompanyModel(Booth booth, Company company, CompanyLogo companyLogo)
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
			this.website = company.getWebsite();
			this.city = company.getCity();
			this.state = company.getState();
			this.address1 = company.getAddress1();
			this.address2 = company.getAddress2();
			this.address3 = company.getAddress3();
			this.postalCode = company.getPostalCode();
			this.facebook = company.getFacebook();
			this.twitter = company.getTwitter();
			this.linkedIn = company.getLinkedIn();
			this.mainCatergories = company.getMainCategories();
			this.subCategories = company.getSubCategories();
			this.targetMarkets = company.getTargetMarkets();
			this.geographicMarkets = company.getGeographicMarkets();
		}

		if (companyLogo != null)
		{
			this.companyLogo = BitmapFactory.decodeByteArray(companyLogo.getLogoBytes(), 0, companyLogo.getLogoBytes().length);
		}
	}

	public boolean getHasCategories()
	{
		return (this.mainCatergories != null && this.mainCatergories != "") ||
			(this.subCategories != null && this.subCategories != "") ||
			(this.targetMarkets != null && this.targetMarkets != "") ||
			(this.geographicMarkets != null && this.geographicMarkets != "");
	}

	public boolean getHasSocialNetworks()
	{
		return getHasFacebook() || getHasTwitter() || getHasLinkedIn();
	}

	public boolean getHasFacebook()
	{
		return this.facebook != null && this.facebook != "";
	}

	public boolean getHasTwitter()
	{
		return this.twitter != null && this.twitter != "";
	}

	public boolean getHasLinkedIn()
	{
		return this.linkedIn != null && this.linkedIn != "";
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

	public String getWebsite()
	{
		return website;
	}

	public void setWebsite(String website)
	{
		this.website = website;
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

	public String getGeographicMarkets()
	{
		return geographicMarkets;
	}

	public void setGeographicMarkets(String geographicMarkets)
	{
		this.geographicMarkets = geographicMarkets;
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

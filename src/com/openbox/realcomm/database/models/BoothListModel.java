package com.openbox.realcomm.database.models;

import com.openbox.realcomm.database.objects.Booth;
import com.openbox.realcomm.database.objects.Company;

public class BoothListModel
{
	private int boothId;
	private double accuracy = BoothModel.DEFAULT_ACCURACY; // Set to default until ranging has begun
	private int boothNumber;

	private int companyId;
	private String companyName;

	public BoothListModel(Booth booth, Company company)
	{
		if (booth != null)
		{
			this.boothId = booth.getBoothId();
			this.boothNumber = booth.getBoothNumber();
		}

		if (company != null)
		{
			this.companyId = company.getCompanyId();
			this.companyName = company.getName();
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

	public double getAccuracy()
	{
		return accuracy;
	}

	public void setAccuracy(double accuracy)
	{
		this.accuracy = accuracy;
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

	public String getCompanyName()
	{
		return companyName;
	}

	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}
}

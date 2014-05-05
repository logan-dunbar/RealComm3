package com.openbox.realcomm3.database.models;

import com.openbox.realcomm3.database.objects.Booth;
import com.openbox.realcomm3.database.objects.Company;

public class BoothListModel
{
	private int boothId;
	private float accuracy = BoothDistanceModel.DEFAULT_ACCURACY; // Set to default until ranging has begun
	private int boothNumber;

	private int companyId;
	private String companyName;

	public BoothListModel(Booth booth, Company company)
	{
		this.boothId = booth.getBoothId();
		this.boothNumber = booth.getBoothNumber();

		this.companyId = company.getCompanyId();
		this.companyName = company.getName();
	}

	public int getBoothId()
	{
		return boothId;
	}

	public void setBoothId(int boothId)
	{
		this.boothId = boothId;
	}

	public float getAccuracy()
	{
		return accuracy;
	}

	public void setAccuracy(float accuracy)
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

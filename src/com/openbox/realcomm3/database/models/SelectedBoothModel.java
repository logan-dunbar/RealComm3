package com.openbox.realcomm3.database.models;

public class SelectedBoothModel
{
	private int boothId;
	private int companyId;

	public SelectedBoothModel(int boothId, int companyId)
	{
		this.boothId = boothId;
		this.companyId = companyId;
	}

	public int getBoothId()
	{
		return boothId;
	}

	public void setBoothId(int boothId)
	{
		this.boothId = boothId;
	}

	public int getCompanyId()
	{
		return companyId;
	}

	public void setCompanyId(int companyId)
	{
		this.companyId = companyId;
	}
}

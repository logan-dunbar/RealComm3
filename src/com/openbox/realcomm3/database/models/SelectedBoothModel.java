package com.openbox.realcomm3.database.models;

import java.io.Serializable;

public class SelectedBoothModel implements Serializable
{
	// Generated serialVersionUID
	private static final long serialVersionUID = -4372057858623803115L;

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

package com.openbox.realcomm.database.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.openbox.realcomm.database.objects.CompanyLogo;

public class CompanyLogoModel
{
	private Bitmap companyLogo;

	public CompanyLogoModel(CompanyLogo logo)
	{
		if (logo != null)
		{
			// TODO do sample size decoding
			byte[] logoBytes = logo.getLogoBytes();
			this.companyLogo = BitmapFactory.decodeByteArray(logoBytes, 0, logoBytes.length);
		}
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

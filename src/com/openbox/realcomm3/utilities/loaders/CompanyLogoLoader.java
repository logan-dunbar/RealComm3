package com.openbox.realcomm3.utilities.loaders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.openbox.realcomm3.database.DatabaseManager;
import com.openbox.realcomm3.database.models.BoothModel;
import com.openbox.realcomm3.database.models.CompanyLogoModel;
import com.openbox.realcomm3.database.objects.Company;
import com.openbox.realcomm3.database.objects.CompanyLogo;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class CompanyLogoLoader extends AsyncTaskLoader<CompanyLogoModel>
{
	private CompanyLogoModel companyLogoModel;

	private BoothModel boothModel;

	public CompanyLogoLoader(Context context, BoothModel boothModel)
	{
		super(context);
		this.boothModel = boothModel;
	}

	@Override
	protected void onStartLoading()
	{
		if (this.companyLogoModel == null)
		{
			forceLoad();
		}
		else
		{
			deliverResult(this.companyLogoModel);
		}
	}

	@Override
	protected void onStopLoading()
	{
		cancelLoad();
	}

	@Override
	public void onContentChanged()
	{
		forceLoad();
	}

	@Override
	public void reset()
	{
		super.reset();

		onStopLoading();
		this.companyLogoModel = null;
	}

	@Override
	public CompanyLogoModel loadInBackground()
	{
		CompanyLogo model = null;

		try
		{
			List<CompanyLogo> logos = DatabaseManager.getInstance().getForWhereEquals(CompanyLogo.class, Company.COMPANY_ID_COLUMN_NAME,
				this.boothModel.getCompanyId());
			if (logos.size() > 0)
			{
				// Should only be one
				model = logos.get(0);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return new CompanyLogoModel(model);
	}

	@Override
	public void deliverResult(CompanyLogoModel data)
	{
		this.companyLogoModel = data;
		if (isStarted())
		{
			super.deliverResult(data);
		}
	}
}

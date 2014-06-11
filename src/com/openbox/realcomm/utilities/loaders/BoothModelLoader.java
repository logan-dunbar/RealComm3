package com.openbox.realcomm.utilities.loaders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.openbox.realcomm.database.DatabaseManager;
import com.openbox.realcomm.database.models.BoothModel;
import com.openbox.realcomm.database.objects.Booth;
import com.openbox.realcomm.database.objects.Company;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class BoothModelLoader extends AsyncTaskLoader<List<BoothModel>>
{

	private List<BoothModel> boothDistanceList;

	public BoothModelLoader(Context context)
	{
		super(context);
	}

	@Override
	protected void onStartLoading()
	{
		if (this.boothDistanceList == null)
		{
			forceLoad();
		}
		else
		{
			deliverResult(this.boothDistanceList);
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

		this.boothDistanceList = null;
	}

	@Override
	public List<BoothModel> loadInBackground()
	{
		List<BoothModel> list = new ArrayList<>();

		try
		{
			List<Booth> booths = DatabaseManager.getInstance().getAll(Booth.class);
			List<Company> companies = DatabaseManager.getInstance().getAll(Company.class);

			for (Booth booth : booths)
			{
				for (Company company : companies)
				{
					if (booth.getCompanyId() == company.getCompanyId())
					{
						list.add(new BoothModel(booth, company));
						break;
					}
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return list;
	}

	@Override
	public void deliverResult(List<BoothModel> data)
	{
		this.boothDistanceList = data;

		if (isStarted())
		{
			super.deliverResult(this.boothDistanceList);
		}
	}
}

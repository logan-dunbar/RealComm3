package com.openbox.realcomm3.utilities.loaders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.openbox.realcomm3.database.DatabaseManager;
import com.openbox.realcomm3.database.models.BoothDistanceModel;
import com.openbox.realcomm3.database.objects.Booth;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class BoothDistanceLoader extends AsyncTaskLoader<List<BoothDistanceModel>>
{

	private List<BoothDistanceModel> boothDistanceList;
	
	public BoothDistanceLoader(Context context)
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
	public List<BoothDistanceModel> loadInBackground()
	{
		List<BoothDistanceModel> list = new ArrayList<>();
		
		try
		{
			List<Booth> boothList = DatabaseManager.getInstance().getAll(Booth.class);
			
			for (Booth booth : boothList)
			{
				list.add(new BoothDistanceModel(booth));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public void deliverResult(List<BoothDistanceModel> data)
	{
		this.boothDistanceList = data;
		
		if (isStarted())
		{
			super.deliverResult(this.boothDistanceList);
		}
	}
}

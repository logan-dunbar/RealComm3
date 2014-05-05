package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.List;

import com.openbox.realcomm3.database.models.BoothDistanceModel;
import com.openbox.realcomm3.utilities.interfaces.DataChangedInteface;
import com.openbox.realcomm3.utilities.loaders.BoothDistanceLoader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

public class DataFragment extends Fragment
{
	public static final String TAG = "dataFragment";
	private static final int BOOTH_DISTANCE_LOADER_ID = 1;
	
	private List<BoothDistanceModel> boothDistanceList = new ArrayList<>();
	
	private DataChangedInteface dataChangedListener;
	
	public static DataFragment newInstance()
	{
		DataFragment fragment = new DataFragment();
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		
		initBoothDistanceLoader();
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		if (activity instanceof DataChangedInteface)
		{
			this.dataChangedListener = (DataChangedInteface) activity;
		}
	}
	
	// All the get/set/update methods go here

	private void initBoothDistanceLoader()
	{
		getLoaderManager().initLoader(BOOTH_DISTANCE_LOADER_ID, null, this.boothDistanceLoaderCallbacks);
	}

	private LoaderCallbacks<List<BoothDistanceModel>> boothDistanceLoaderCallbacks = new LoaderCallbacks<List<BoothDistanceModel>>()
	{
		@Override
		public Loader<List<BoothDistanceModel>> onCreateLoader(int loaderId, Bundle bundle)
		{
			return new BoothDistanceLoader(getActivity());
		}
		
		@Override
		public void onLoadFinished(Loader<List<BoothDistanceModel>> loader, List<BoothDistanceModel> list)
		{
			DataFragment.this.boothDistanceList = list;
			if (DataFragment.this.dataChangedListener != null)
			{
				DataFragment.this.dataChangedListener.onDataChanged();
			}
		}
		
		@Override
		public void onLoaderReset(Loader<List<BoothDistanceModel>> loader)
		{
		}
	};
}

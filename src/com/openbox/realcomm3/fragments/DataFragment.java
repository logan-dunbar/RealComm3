package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.openbox.realcomm3.database.models.BoothModel;
import com.openbox.realcomm3.utilities.enums.BoothSortMode;
import com.openbox.realcomm3.utilities.enums.ProximityRegion;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;
import com.openbox.realcomm3.utilities.loaders.BoothModelLoader;
import com.radiusnetworks.ibeacon.IBeacon;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

public class DataFragment extends Fragment implements DataInterface, DataChangedCallbacks
{
	public static final String TAG = "dataFragment";
	private static final int BOOTH_MODEL_LOADER_ID = 1;

	private List<BoothModel> boothModelList = new ArrayList<>();

	private DataChangedCallbacks dataChangedListener;

	public static DataFragment newInstance()
	{
		DataFragment fragment = new DataFragment();
		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		initBoothModelLoader();
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		if (activity instanceof DataChangedCallbacks)
		{
			this.dataChangedListener = (DataChangedCallbacks) activity;
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.dataChangedListener = null;
	}

	/**********************************************************************************************
	 * Data Interface Implements
	 **********************************************************************************************/
	@Override
	public BoothModel getBoothModelForBoothId(int boothId)
	{
		for (BoothModel model : this.boothModelList)
		{
			if (model.getBoothId() == boothId)
			{
				return model;
			}
		}

		return null;
	}
	
	public BoothModel getBoothModelForCompanyName(String companyName)
	{
		for (BoothModel model : this.boothModelList)
		{
			if (model.getCompanyName().equalsIgnoreCase(companyName))
			{
				return model;
			}
		}
		
		return null;
	}

	@Override
	public List<Integer> getClosestBoothIds(int numberOfDisplayBooths)
	{
		List<Integer> boothIds = new ArrayList<Integer>();
		List<BoothModel> sortedList = getListSortedByAccuracy();
		for (int i = 0; i < getNumberOfBooths(numberOfDisplayBooths); i++)
		{
			BoothModel booth = sortedList.get(i);
			if (BoothModel.getProximityRegion(booth.getAccuracy()) == ProximityRegion.OUTOFRANGE)
			{
				// Stop adding if out of range
				break;
			}

			boothIds.add(booth.getBoothId());
		}

		return boothIds;
	}

	@Override
	public List<Integer> getRandomBoothIds(int numberOfDisplayBooths)
	{
		Random random = new Random();
		List<Integer> randomList = new ArrayList<Integer>();
		List<Integer> boothIds = new ArrayList<Integer>();
		for (int i = 0; i < getNumberOfBooths(numberOfDisplayBooths); i++)
		{
			int position;
			do
			{
				// Make sure to random on full list
				position = random.nextInt(this.boothModelList.size());
			}
			while (randomList.contains(position));

			randomList.add(position);
			boothIds.add(this.boothModelList.get(position).getBoothId());
		}

		return boothIds;
	}

	@Override
	public List<BoothModel> getBoothModelList(BoothSortMode sortMode)
	{
		switch (sortMode)
		{
			case NEAR_ME:
				return getListSortedByAccuracy();
			case A_TO_Z:
				return getListSortedByName();
			default:
				return new ArrayList<>(this.boothModelList);
		}
	}

	@Override
	public Map<ProximityRegion, Integer> getBoothProximityCounts()
	{
		// Get all the proximity regions
		Map<ProximityRegion, Integer> proximityCounts = new HashMap<ProximityRegion, Integer>();
		for (ProximityRegion proximityRegion : ProximityRegion.values())
		{
			proximityCounts.put(proximityRegion, 0);
		}

		// Update the counts for the proximity regions
		for (BoothModel booth : this.boothModelList)
		{
			ProximityRegion boothProximityRegion = BoothModel.getProximityRegion(booth.getAccuracy());
			proximityCounts.put(boothProximityRegion, proximityCounts.get(boothProximityRegion) + 1);
		}

		// Enum.values() returns ordered list, so make sure enum is ordered correctly
		return proximityCounts;
	}

	@Override
	public void updateAccuracy(Collection<IBeacon> beaconList)
	{
		if (this.boothModelList.size() == 0)
		{
			// Do nothing if list empty/not initialized
			return;
		}

		for (BoothModel model : this.boothModelList)
		{
			Boolean outOfRange = true;
			for (IBeacon beacon : beaconList)
			{
				if (model.getUUID().equalsIgnoreCase(beacon.getProximityUuid()) &&
					model.getMajor() == beacon.getMajor() &&
					model.getMinor() == beacon.getMinor())
				{
					model.updateAccuracyWithBeacon(beacon);
					outOfRange = false;
					break;
				}
			}

			if (outOfRange)
			{
				model.updateAccuracyWithDefault();
			}
		}
		
		if (this.dataChangedListener != null)
		{
			this.dataChangedListener.onBeaconsUpdated();
		}
	}

	@Override
	public void resetAccuracy()
	{
		for (BoothModel boothModel : this.boothModelList)
		{
			boothModel.resetAccuracy();
		}

		if (this.dataChangedListener != null)
		{
			this.dataChangedListener.onBeaconsUpdated();
		}
	}

	/**********************************************************************************************
	 * Data Changed Callbacks
	 **********************************************************************************************/
	@Override
	public void onDataLoaded()
	{
		// Stub
	}

	@Override
	public void onDataChanged()
	{
		Loader<List<BoothModel>> loader = getLoaderManager().getLoader(BOOTH_MODEL_LOADER_ID);
		if (loader == null)
		{
			initBoothModelLoader();
		}
		else
		{
			loader.onContentChanged();
		}
	}

	@Override
	public void onBeaconsUpdated()
	{
		// Stub
	}

	/**********************************************************************************************
	 * Private helper methods
	 **********************************************************************************************/
	private void initBoothModelLoader()
	{
		getLoaderManager().initLoader(BOOTH_MODEL_LOADER_ID, null, this.boothModelLoaderCallbacks);
	}

	private int getNumberOfBooths(int numberOfDisplayBooths)
	{
		return this.boothModelList.size() < numberOfDisplayBooths ? this.boothModelList.size() : numberOfDisplayBooths;
	}

	private List<BoothModel> getListSortedByAccuracy()
	{
		List<BoothModel> sortingList = new ArrayList<>(this.boothModelList);
		Collections.sort(sortingList, BoothModel.getAccuracyComparator());
		return sortingList;
	}

	private List<BoothModel> getListSortedByName()
	{
		List<BoothModel> sortingList = new ArrayList<>(this.boothModelList);
		Collections.sort(sortingList, BoothModel.getCompanyNameComparator());
		return sortingList;
	}

	private void finishDataLoad(List<BoothModel> results)
	{
		this.boothModelList = results;
		if (this.dataChangedListener != null)
		{
			this.dataChangedListener.onDataLoaded();
		}
	}

	private LoaderCallbacks<List<BoothModel>> boothModelLoaderCallbacks = new LoaderCallbacks<List<BoothModel>>()
	{
		@Override
		public Loader<List<BoothModel>> onCreateLoader(int loaderId, Bundle bundle)
		{
			return new BoothModelLoader(getActivity());
		}

		@Override
		public void onLoadFinished(Loader<List<BoothModel>> loader, List<BoothModel> results)
		{
			finishDataLoad(results);
		}

		@Override
		public void onLoaderReset(Loader<List<BoothModel>> loader)
		{
		}
	};
}

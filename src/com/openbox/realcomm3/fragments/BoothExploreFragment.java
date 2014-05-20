package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseBoothFlipperFragment;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.enums.RealcommPhonePage;
import com.openbox.realcomm3.utilities.helpers.BoothFlipHelper;
import com.openbox.realcomm3.utilities.helpers.LogHelper;
import com.openbox.realcomm3.utilities.helpers.ViewTimerTask;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.BoothFlipperInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.TimerTaskCallbacks;

public class BoothExploreFragment extends BaseBoothFlipperFragment implements AppModeChangedCallbacks
{
	public static final String TAG = "boothExploreFragment";

	// Listeners
	private List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();
	private List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();

	@Override
	public int getNumberOfBigBooths()
	{
		return 3;
	}

	public static BoothExploreFragment newInstance()
	{
		BoothExploreFragment fragment = new BoothExploreFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_booth_explore, container, false);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		createProximityFragment();
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		
		// Clean up
		this.appModeChangedListeners.clear();
		this.dataChangedListeners.clear();
	}

	/**********************************************************************************************
	 * App Mode Changed Callbacks
	 **********************************************************************************************/
	@Override
	public void onAppModeChanged()
	{
		for (AppModeChangedCallbacks listener : this.appModeChangedListeners)
		{
			listener.onAppModeChanged();
		}
	}

	@Override
	public void onOnlineModeToOfflineMode()
	{
		// Stub. Not needed.
	}

	/**********************************************************************************************
	 * Data Changed Interface Implements
	 **********************************************************************************************/
	@Override
	public void onDataLoaded()
	{
		super.onDataLoaded();

		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onDataLoaded();
		}
	}

	@Override
	public void onDataChanged()
	{
		super.onDataChanged();

		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onDataChanged();
		}
	}

	@Override
	public void onBeaconsUpdated()
	{
		super.onBeaconsUpdated();

		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onBeaconsUpdated();
		}
	}

	/**********************************************************************************************
	 * Private Helper Methods
	 **********************************************************************************************/
	private void createProximityFragment()
	{
		ProximityFragment fragment = (ProximityFragment) getChildFragmentManager().findFragmentById(R.id.proximityContainer);
		if (fragment == null)
		{
			fragment = ProximityFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.proximityContainer, fragment).commit();
		}

		this.dataChangedListeners.add(fragment);
		this.appModeChangedListeners.add(fragment);
	}
}

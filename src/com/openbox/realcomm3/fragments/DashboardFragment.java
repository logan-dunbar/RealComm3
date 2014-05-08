package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;

public class DashboardFragment extends BaseFragment implements
	AppModeChangedCallbacks,
	DataChangedCallbacks
{
	private List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();
	private List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();

	public static DashboardFragment newInstance()
	{
		DashboardFragment fragment = new DashboardFragment();
		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		createConnectionStatusFragment();
		createProximityFragment();
		createScheduleFragment();
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
	public void onDataLoaded()
	{
		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onDataLoaded();
		}
	}

	@Override
	public void onDataChanged()
	{
		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onDataChanged();
		}
	}

	@Override
	public void onBeaconsUpdated()
	{
		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onBeaconsUpdated();
		}
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
	 * Private Helper Methods
	 **********************************************************************************************/
	private void createConnectionStatusFragment()
	{
		ConnectionStatusFragment fragment = (ConnectionStatusFragment) getChildFragmentManager().findFragmentById(R.id.connectionStatusContainer);
		if (fragment == null)
		{
			fragment = ConnectionStatusFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.connectionStatusContainer, fragment).commit();
		}

		this.appModeChangedListeners.add(fragment);
	}

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

	private void createScheduleFragment()
	{
		ScheduleFragment fragment = (ScheduleFragment) getChildFragmentManager().findFragmentById(R.id.scheduleFragmentContainer);
		if (fragment == null)
		{
			fragment = ScheduleFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.scheduleFragmentContainer, fragment).commit();
		}

		this.dataChangedListeners.add(fragment);
	}
}

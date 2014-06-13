package com.openbox.realcomm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openbox.realcomm.base.BaseBoothFlipperFragment;
import com.openbox.realcomm.R;

public class DashboardTabletFragment extends BaseBoothFlipperFragment
{
	public static final String TAG = "dashboardTabletFragment";

	@Override
	public int getNumberOfBigBooths()
	{
		return 2;
	}

	public static DashboardTabletFragment newInstance()
	{
		DashboardTabletFragment fragment = new DashboardTabletFragment();
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

		createBoothListFragment();
		createProximityFragment();
		createScheduleFragment();
	}

	/**********************************************************************************************
	 * Private helper methods
	 **********************************************************************************************/
	private void createBoothListFragment()
	{
		BoothListFragment fragment = (BoothListFragment) getChildFragmentManager().findFragmentById(R.id.boothListContainer);
		if (fragment == null)
		{
			fragment = BoothListFragment.newInstance();
			getChildFragmentManager()
				.beginTransaction()
				.add(R.id.boothListContainer, fragment)
				.commitAllowingStateLoss();

			getDataChangedListeners().add(fragment);
			getClearFocusListeners().add(fragment);
			getAppModeChangedListeners().add(fragment);
		}
	}

	private void createProximityFragment()
	{
		ProximityFragment fragment = (ProximityFragment) getChildFragmentManager().findFragmentById(R.id.proximityContainer);
		if (fragment == null)
		{
			fragment = ProximityFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.proximityContainer, fragment).commitAllowingStateLoss();
		}

		getDataChangedListeners().add(fragment);
		getAppModeChangedListeners().add(fragment);
	}

	private void createScheduleFragment()
	{
		ScheduleFragment fragment = (ScheduleFragment) getChildFragmentManager().findFragmentById(R.id.scheduleFragmentContainer);
		if (fragment == null)
		{
			fragment = ScheduleFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.scheduleFragmentContainer, fragment).commitAllowingStateLoss();
		}

		getDataChangedListeners().add(fragment);
	}
}

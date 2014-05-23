package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseBoothFlipperFragment;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.animations.FlipAnimation;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.helpers.BoothFlipHelper;
import com.openbox.realcomm3.utilities.helpers.LogHelper;
import com.openbox.realcomm3.utilities.helpers.ViewTimerTask;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.BoothFlipperInterface;
import com.openbox.realcomm3.utilities.interfaces.ClearFocusInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.TimerTaskCallbacks;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DashboardFragment_old extends BaseBoothFlipperFragment implements
	AppModeChangedCallbacks,
	ClearFocusInterface
{
	public static final String TAG = "listingPageFragment";

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	// Listeners/Interfaces
	private List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();
	private List<ClearFocusInterface> clearFocusListeners = new ArrayList<>();
	private List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();

	@Override
	public int getNumberOfBigBooths()
	{
		return 2;
	}

	public static DashboardFragment_old newInstance()
	{
		DashboardFragment_old fragment = new DashboardFragment_old();
		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.dataChangedListeners.clear();
		this.clearFocusListeners.clear();
		this.appModeChangedListeners.clear();
	}

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
	 * Clear Focus Interface Implements
	 **********************************************************************************************/
	@Override
	public List<View> getViewsToClearFocus()
	{
		List<View> views = new ArrayList<>();
		for (ClearFocusInterface listener : this.clearFocusListeners)
		{
			views.addAll(listener.getViewsToClearFocus());
		}

		return views;
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
				.commit();

			this.dataChangedListeners.add(fragment);
			this.clearFocusListeners.add(fragment);
			this.appModeChangedListeners.add(fragment);
		}
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

package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.animations.FlipAnimation;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.helpers.BoothFlipHelper;
import com.openbox.realcomm3.utilities.helpers.LogHelper;
import com.openbox.realcomm3.utilities.helpers.ViewTimerTask;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.ClearFocusInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.TimerTaskCallbacks;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListingPageFragment extends BaseFragment implements
	TimerTaskCallbacks,
	DataChangedCallbacks,
	AppModeChangedCallbacks,
	ClearFocusInterface
{
	public static final String TAG = "listingPageFragment";

	private static final int NUMBER_OF_DISPLAY_BOOTHS = 3;
	private static final int NUMBER_OF_BIG_BOOTHS = 2;

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	// Listeners/Interfaces
	private List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();
	private List<DataChangedCallbacks> boothDataChangedListeners = new ArrayList<>();
	private List<ClearFocusInterface> clearFocusListeners = new ArrayList<>();
	private List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();

	// View Timer
	private Timer viewUpdateTimer;

	// View State
	private List<Integer> boothIdsToDisplay = new ArrayList<Integer>();

	// Animation constants
	private static int betweenDelayDuration;
	private static int duration;

	public static ListingPageFragment newInstance()
	{
		ListingPageFragment fragment = new ListingPageFragment();
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
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		betweenDelayDuration = getResources().getInteger(R.integer.boothFragmentBetweenDelay);
		duration = getResources().getInteger(R.integer.boothFragmentFlipDuration);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_listing_page, container, false);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		createBoothListFragment();

		createDashboardFragment();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// Kick view timer off
		startViewTimer();
	}

	@Override
	public void onPause()
	{
		super.onPause();

		// Timers don't follow activity lifecycle, must manage ourselves
		stopViewTimer();
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);

		if (hidden)
		{
			stopViewTimer();
		}
		else
		{
			startViewTimer();
		}
	}

	/**********************************************************************************************
	 * Data Changed Interface Implements
	 **********************************************************************************************/
	@Override
	public void onDataLoaded()
	{
		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onDataLoaded();
		}

		for (DataChangedCallbacks listener : this.boothDataChangedListeners)
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

		for (DataChangedCallbacks listener : this.boothDataChangedListeners)
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

		for (DataChangedCallbacks listener : this.boothDataChangedListeners)
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
	 * Timer Task Callbacks
	 **********************************************************************************************/
	@Override
	public void onTimerTick()
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				ListingPageFragment.this.updateView();
			}
		});
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
	 * Update View Methods
	 **********************************************************************************************/
	private void updateView()
	{
		if (getAppModeInterface() != null && isVisible())
		{
			switch (getAppModeInterface().getCurrentAppMode())
			{
				case INITIALIZING:
					// Do nothing
					break;
				case OFFLINE:
					updateViewOffline();
					break;
				case ONLINE:
					updateViewOnline();
					break;
				case OUTOFRANGE:
					updateViewOutOfRange();
					break;
				case PAUSED:
					// Do nothing
				default:
					break;
			}
		}
	}

	private void updateViewOffline()
	{
		if (getDataInterface() != null)
		{
			this.boothIdsToDisplay = getDataInterface().getRandomBoothIds(NUMBER_OF_DISPLAY_BOOTHS);
		}

		updateBoothView();
	}

	private void updateViewOnline()
	{
		// TODO: I don't think this is needed (the dataChanged onBoothsUpdated method is doing it already)
		// if (this.boothListInterface != null)
		// {
		// this.boothListInterface.updateList();
		// }

		if (getDataInterface() != null)
		{
			this.boothIdsToDisplay = getDataInterface().getClosestBoothIds(NUMBER_OF_DISPLAY_BOOTHS);
		}

		updateBoothView();
	}

	private void updateViewOutOfRange()
	{
		LogHelper.Log("updateViewOutOfRange");
		// TODO Must still implement
	}

	// This method does the actual updating of the fragments i.e. doing the fragment switch on ALL of the booths
	private void updateBoothView()
	{
		this.boothDataChangedListeners.clear();
		this.boothDataChangedListeners.addAll(BoothFlipHelper.updateBoothViews(
			NUMBER_OF_BIG_BOOTHS,
			this.boothIdsToDisplay,
			duration,
			betweenDelayDuration,
			getChildFragmentManager()));
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

	private void createDashboardFragment()
	{
		DashboardFragment fragment = (DashboardFragment) getChildFragmentManager().findFragmentById(R.id.dashboardContainer);
		if (fragment == null)
		{
			fragment = DashboardFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.dashboardContainer, fragment).commit();
		}

		this.appModeChangedListeners.add(fragment);
		this.dataChangedListeners.add(fragment);
	}

	private void startViewTimer()
	{
		// Memory leak - navigate to profile page, minimize, come back, starts the timer again, but
		// because not visible GarbageCollector doesn't know to clean up
		if (this.viewUpdateTimer == null && getAppModeInterface() != null && isVisible())
		{
			this.viewUpdateTimer = new Timer();
			int delay = getAppModeInterface().getCurrentAppMode().getAnimationStartDelay();
			int period = getAppModeInterface().getCurrentAppMode().getAnimationPeriod();
			this.viewUpdateTimer.schedule(new ViewTimerTask(this), delay, period);
		}
	}

	private void stopViewTimer()
	{
		if (this.viewUpdateTimer != null)
		{
			this.viewUpdateTimer.cancel();
			this.viewUpdateTimer = null;
		}
	}
}

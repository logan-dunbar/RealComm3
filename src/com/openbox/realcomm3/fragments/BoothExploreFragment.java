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
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.enums.RealcommPhonePage;
import com.openbox.realcomm3.utilities.helpers.BoothFlipHelper;
import com.openbox.realcomm3.utilities.helpers.LogHelper;
import com.openbox.realcomm3.utilities.helpers.ViewTimerTask;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.TimerTaskCallbacks;

public class BoothExploreFragment extends BaseFragment implements
	TimerTaskCallbacks,
	DataChangedCallbacks,
	AppModeChangedCallbacks
{
	public static final String TAG = "boothExploreFragment";

	private static final int NUMBER_OF_DISPLAY_BOOTHS = 3;
	private static final int NUMBER_OF_BIG_BOOTHS = 3;

	// Animation contants
	private static int betweenDelayDuration;
	private static int duration;

	// View Timer
	private Timer viewUpdateTimer;

	// View State
	private List<Integer> boothIdsToDisplay = new ArrayList<Integer>();

	// Listeners
	private List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();
	private List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();
	private List<DataChangedCallbacks> boothDataChangedListeners = new ArrayList<>();

	public static BoothExploreFragment newInstance()
	{
		BoothExploreFragment fragment = new BoothExploreFragment();
		return fragment;
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
		View view = inflater.inflate(R.layout.fragment_booth_explore, container, false);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		createProximityFragment();
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

	@Override
	public void onTimerTick()
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				BoothExploreFragment.this.updateView();
			}
		});
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

		updateBoothViews();
	}

	private void updateViewOnline()
	{
		if (getDataInterface() != null)
		{
			this.boothIdsToDisplay = getDataInterface().getClosestBoothIds(NUMBER_OF_DISPLAY_BOOTHS);
		}

		updateBoothViews();
	}

	private void updateViewOutOfRange()
	{
		LogHelper.Log("updateViewOutOfRange");
		// TODO Must still implement
	}

	private void updateBoothViews()
	{
		this.boothDataChangedListeners.clear();
		this.boothDataChangedListeners.addAll(BoothFlipHelper.updateBoothViews(
			NUMBER_OF_BIG_BOOTHS,
			this.boothIdsToDisplay,
			duration,
			betweenDelayDuration,
			getChildFragmentManager()));
	}

	private void startViewTimer()
	{
		// Memory leak - navigate to profile page, minimize, come back, starts the timer again, but
		// because not visible GarbageCollector doesn't know to clean up. Fixed by only starting if isVisibile()
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

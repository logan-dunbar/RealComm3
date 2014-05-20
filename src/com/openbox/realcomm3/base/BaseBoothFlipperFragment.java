package com.openbox.realcomm3.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.os.Bundle;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.helpers.BoothFlipHelper;
import com.openbox.realcomm3.utilities.helpers.ViewTimerTask;
import com.openbox.realcomm3.utilities.interfaces.BoothFlipperInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.TimerTaskCallbacks;

public abstract class BaseBoothFlipperFragment extends BaseFragment implements
	TimerTaskCallbacks,
	DataChangedCallbacks,
	BoothFlipperInterface
{
	// This could be @Override-able if needed
	private static final int NUMBER_OF_DISPLAY_BOOTHS = 3;

	// Like this
	public abstract int getNumberOfBigBooths();

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	// Animation contants
	private static int betweenDelayDuration;
	private static int duration;

	// View Timer
	private Timer viewUpdateTimer;

	// View State
	private List<Integer> boothIdsToDisplay = new ArrayList<Integer>();

	// Listeners
	private List<DataChangedCallbacks> boothDataChangedListeners = new ArrayList<>();

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		betweenDelayDuration = getResources().getInteger(R.integer.boothFragmentBetweenDelay);
		duration = getResources().getInteger(R.integer.boothFragmentFlipDuration);
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
		for (DataChangedCallbacks listener : this.boothDataChangedListeners)
		{
			listener.onDataLoaded();
		}
	}

	@Override
	public void onDataChanged()
	{
		for (DataChangedCallbacks listener : this.boothDataChangedListeners)
		{
			listener.onDataChanged();
		}
	}

	@Override
	public void onBeaconsUpdated()
	{
		if (getDataInterface() != null && getAppModeInterface() != null)
		{
			AppMode currentAppMode = getAppModeInterface().getCurrentAppMode();

			List<Integer> closestBoothIds = getDataInterface().getClosestBoothIds(NUMBER_OF_DISPLAY_BOOTHS);
			if (closestBoothIds.size() < NUMBER_OF_DISPLAY_BOOTHS && currentAppMode == AppMode.ONLINE)
			{
				getAppModeInterface().changeAppMode(AppMode.OUTOFRANGE);
			}
			else if (closestBoothIds.size() >= NUMBER_OF_DISPLAY_BOOTHS && currentAppMode == AppMode.OUTOFRANGE)
			{
				getAppModeInterface().changeAppMode(AppMode.ONLINE);
			}
		}

		for (DataChangedCallbacks listener : this.boothDataChangedListeners)
		{
			listener.onBeaconsUpdated();
		}
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
				updateView();
			}
		});
	}

	/**********************************************************************************************
	 * Booth Flipper Interface
	 **********************************************************************************************/
	@Override
	public void resetTimer()
	{
		stopViewTimer();
		startViewTimer();
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
		updateBoothsWithRandom();
	}

	private void updateViewOnline()
	{
		if (getDataInterface() != null && getActivityListener() != null)
		{
			this.boothIdsToDisplay = getDataInterface().getClosestBoothIds(NUMBER_OF_DISPLAY_BOOTHS);
			updateBoothViews();
		}
	}

	private void updateViewOutOfRange()
	{
		updateBoothsWithRandom();
	}

	private void updateBoothsWithRandom()
	{
		if (getDataInterface() != null)
		{
			this.boothIdsToDisplay = getDataInterface().getRandomBoothIds(NUMBER_OF_DISPLAY_BOOTHS);
			updateBoothViews();
		}
	}

	private void updateBoothViews()
	{
		this.boothDataChangedListeners.clear();
		this.boothDataChangedListeners.addAll(BoothFlipHelper.updateBoothViews(
			getNumberOfBigBooths(),
			this.boothIdsToDisplay,
			duration,
			betweenDelayDuration,
			getChildFragmentManager()));
	}

	/**********************************************************************************************
	 * Timer Methods
	 **********************************************************************************************/
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

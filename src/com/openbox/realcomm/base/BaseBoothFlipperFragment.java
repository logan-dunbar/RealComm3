package com.openbox.realcomm.base;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;

import com.openbox.realcomm.utilities.enums.AppMode;
import com.openbox.realcomm.utilities.helpers.BoothFlipHelper;
import com.openbox.realcomm.utilities.interfaces.BoothFlipperInterface;
import com.openbox.realcomm.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm.R;

public abstract class BaseBoothFlipperFragment extends BaseFragment implements
	DataChangedCallbacks,
	BoothFlipperInterface
{
	// This could be @Override-able if needed
	private static final int NUMBER_OF_DISPLAY_BOOTHS = 3;
	private static final int DEFAULT_TIMER_PERIOD = 7000;

	// Like this
	public abstract int getNumberOfBigBooths();

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	// Animation constants
	private static int betweenDelayDuration;
	private static int duration;

	// View State
	private List<Integer> boothIdsToDisplay = new ArrayList<Integer>();

	// Listeners
	private List<DataChangedCallbacks> boothDataChangedListeners = new ArrayList<>();

	// View Timer
	private Handler timerHandler = new Handler();
	private int timerPeriod = DEFAULT_TIMER_PERIOD;
	private boolean viewTimerStarted = false;

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

		if (getIsVisible())
		{
			// Memory leak - navigate to profile page, minimize, come back, starts the timer again, but
			// because not visible GarbageCollector doesn't know to clean up. Fixed by only starting if isVisibile()
			startViewTimer();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();

		stopViewTimer();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		this.boothDataChangedListeners.clear();
	}

	/**********************************************************************************************
	 * Data Changed Interface Implements
	 **********************************************************************************************/
	@Override
	public void onBeaconsUpdated()
	{
		super.onBeaconsUpdated();

		if (getDataInterface() != null && getAppModeInterface() != null)
		{
			AppMode currentAppMode = getAppModeInterface().getCurrentAppMode();

			List<Integer> closestBoothIds = getDataInterface().getClosestBoothIds(NUMBER_OF_DISPLAY_BOOTHS);
			if (closestBoothIds.size() < NUMBER_OF_DISPLAY_BOOTHS && currentAppMode == AppMode.ONLINE)
			{
				getAppModeInterface().changeAppMode(AppMode.OUT_OF_RANGE);
			}
			else if (closestBoothIds.size() >= NUMBER_OF_DISPLAY_BOOTHS && currentAppMode == AppMode.OUT_OF_RANGE)
			{
				getAppModeInterface().changeAppMode(AppMode.ONLINE);
			}
		}
	}

	/**********************************************************************************************
	 * Booth Flipper Interface
	 **********************************************************************************************/
	@Override
	public void startViewTimer()
	{
		if (!this.viewTimerStarted &&
			getAppModeInterface() != null &&
			getAppModeInterface().getCurrentAppMode() != AppMode.INITIALIZING)
		{
			// Clear the current queue
			stopViewTimer();

			int timerDelay = getAppModeInterface().getCurrentAppMode().getAnimationStartDelay();
			this.timerPeriod = getAppModeInterface().getCurrentAppMode().getAnimationPeriod();

			this.timerHandler.postDelayed(timerRunnable, timerDelay);
			this.viewTimerStarted = true;
		}
	}

	@Override
	public void stopViewTimer()
	{
		this.timerHandler.removeCallbacks(this.timerRunnable);
		this.viewTimerStarted = false;
	}

	/**********************************************************************************************
	 * AppMode Changed Interface Implements
	 **********************************************************************************************/
	@Override
	public void onAppModeChanged(AppMode newAppMode, AppMode previousAppMode)
	{
		super.onAppModeChanged(newAppMode, previousAppMode);

		if (previousAppMode != AppMode.INITIALIZING)
		{
			stopViewTimer();
			startViewTimer();
		}
	}

	/**********************************************************************************************
	 * Update View Methods
	 **********************************************************************************************/
	private void updateView()
	{
		if (getAppModeInterface() != null && getIsVisible())
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
				case OUT_OF_RANGE:
					updateViewOutOfRange();
					break;
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
		if (getDataInterface() != null && getActivityInterface() != null)
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
		this.boothDataChangedListeners.addAll(
			BoothFlipHelper.updateBoothViews(
				getNumberOfBigBooths(),
				this.boothIdsToDisplay,
				duration,
				betweenDelayDuration,
				getChildFragmentManager()));
	}

	private Runnable timerRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			updateView();
			timerHandler.postDelayed(timerRunnable, timerPeriod);
		}
	};
}

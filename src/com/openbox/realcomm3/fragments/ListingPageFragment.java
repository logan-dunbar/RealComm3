package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.animations.FlipAnimation;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.helpers.LogHelper;
import com.openbox.realcomm3.utilities.helpers.ViewTimerTask;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.BoothListInterface;
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

	private static final int TIMER_PERIOD = 2 * 1000;
	private static final int TIMER_DELAY = TIMER_PERIOD;

	private static final int NUMBER_OF_DISPLAY_BOOTHS = 3;
	private static final int NUMBER_OF_BIG_BOOTHS = 2;

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	// Listeners/Interfaces
	private BoothListInterface boothListInterface;
	private List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();
	private List<ClearFocusInterface> clearFocusListeners = new ArrayList<>();
	private List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();

	// View Timer
	private Timer viewUpdateTimer;

	// View State
	private List<Integer> boothIdsToDisplay = new ArrayList<Integer>();

	public static ListingPageFragment newInstance()
	{
		ListingPageFragment fragment = new ListingPageFragment();
		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.boothListInterface = null;
		this.dataChangedListeners.clear();
		this.clearFocusListeners.clear();
		this.appModeChangedListeners.clear();
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
		if (getAppModeInterface() != null)
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
		if (this.boothListInterface != null)
		{
			this.boothListInterface.updateList();
		}

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
		int betweenDelayDuration = getResources().getInteger(R.integer.boothFragmentBetweenDelay);
		int betweenDelay = 0;
		for (int i = 0; i < this.boothIdsToDisplay.size(); i++)
		{
			// This is where the method starts (for reals)
			int newBoothId = this.boothIdsToDisplay.get(i);
			int containerId = getContainerId(i);

			int duration = getResources().getInteger(R.integer.boothFragmentFlipDuration);

			FragmentTransaction ft = getChildFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.id.flipInDownAnimation, R.id.flipOutDownAnimation);
			
			BoothFragment oldFragment = (BoothFragment) getChildFragmentManager().findFragmentById(containerId);
			Boolean switchFragment = true;
			if (oldFragment != null)
			{
				if (oldFragment.getBoothId() == newBoothId)
				{
					// TODO These should be through an interface
					oldFragment.updateBooth();
					switchFragment = false;
				}
				else
				{
					oldFragment.setOutAnimationDuration(duration);
					oldFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATE);
					oldFragment.setOutAnimationDelay(betweenDelay);
					oldFragment.setOutFlipStartDegrees(FlipAnimation.DEFAULT_OUT_UP_START_DEGREES);
					oldFragment.setOutFlipEndDegrees(FlipAnimation.DEFAULT_OUT_UP_END_DEGREES);

					// oldFragment.setOutFlipStartDegrees(FlipAnimation.DEFAULT_OUT_DOWN_START_DEGREES);
					// oldFragment.setOutFlipEndDegrees(FlipAnimation.DEFAULT_OUT_DOWN_END_DEGREES);
					ft.remove(oldFragment);
				}
			}

			// If first fragment, or changing booths, then add
			if (switchFragment)
			{
				Boolean isBig = i < NUMBER_OF_BIG_BOOTHS;
				BoothFragment newFragment = BoothFragment.newInstance(newBoothId, isBig);

				newFragment.setInAnimationDuration(duration);
				newFragment.setInAnimationInterpolator(AnimationInterpolator.DECELERATE);
				newFragment.setInFlipStartDegrees(FlipAnimation.DEFAULT_IN_UP_START_DEGREES);
				newFragment.setInFlipEndDegrees(FlipAnimation.DEFAULT_IN_UP_END_DEGREES);

				// newFragment.setInFlipStartDegrees(FlipAnimation.DEFAULT_IN_DOWN_START_DEGREES);
				// newFragment.setInFlipEndDegrees(FlipAnimation.DEFAULT_IN_DOWN_END_DEGREES);

				newFragment.setInAnimationDelay(betweenDelay + (oldFragment != null ? duration : 0));

				ft.add(containerId, newFragment);

				betweenDelay += betweenDelayDuration;
			}

			ft.commit();
		}
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
				// .setCustomAnimations(arg0, arg1)
				.add(R.id.boothListContainer, fragment)
				.commit();

			this.boothListInterface = fragment;
			this.dataChangedListeners.add(fragment);
			this.clearFocusListeners.add(fragment);
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
		if (this.viewUpdateTimer == null)
		{
			this.viewUpdateTimer = new Timer();
			this.viewUpdateTimer.schedule(new ViewTimerTask(this), TIMER_DELAY, TIMER_PERIOD);
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

	private int getContainerId(int index)
	{
		switch (index)
		{
			case 0:
				return R.id.topLeftBoothContainer;
			case 1:
				return R.id.bottomLeftBoothContainer;
			case 2:
				return R.id.bottomMiddleBoothContainer;
			default:
				return -1;
		}
	}
}

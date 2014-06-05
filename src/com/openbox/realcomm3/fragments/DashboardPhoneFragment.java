package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.enums.RealcommPhonePage;
import com.openbox.realcomm3.utilities.helpers.FragmentHelper;
import com.openbox.realcomm3.utilities.interfaces.BoothFlipperInterface;
import com.openbox.realcomm3.utilities.interfaces.DashboardPhoneInterface;
import com.openbox.realcomm3.utilities.managers.RealcommPhonePageManager;

public class DashboardPhoneFragment extends BaseFragment implements DashboardPhoneInterface, OnClickListener, BoothFlipperInterface
{
	public static final String TAG = "dashboardPhoneFragment";
	private static final String CURRENT_PHONE_PAGE_KEY = "currentPhonePageKey";

	// Listeners
	private BoothFlipperInterface boothFlipperListener;

	// Views
	private LinearLayout navigationDrawer;
	private List<LinearLayout> navigationLayouts = new ArrayList<>();

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	private RealcommPhonePageManager phonePageManager;

	public static DashboardPhoneFragment newInstance()
	{
		DashboardPhoneFragment fragment = new DashboardPhoneFragment();
		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		initPhonePageManager(savedInstanceState);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.boothFlipperListener = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_dashboard_phone, container, false);

		initNavigationDrawer(view);

		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		initializeFragments();
	}

	@Override
	public boolean onBackBressed()
	{
		if (this.phonePageManager.getCurrentPage() != RealcommPhonePage.INITIALIZING &&
			this.phonePageManager.getCurrentPage() != RealcommPhonePage.BOOTH_EXPLORE)
		{
			changePage(RealcommPhonePage.BOOTH_EXPLORE);
			return true;
		}

		return false;
	}

	/**********************************************************************************************
	 * Dashboard Phone Interface Implements
	 **********************************************************************************************/
	@Override
	public void changePage(RealcommPhonePage page)
	{
		this.phonePageManager.setPreviousPage(this.phonePageManager.getCurrentPage());
		this.phonePageManager.changePage(page);
	}

	@Override
	public void showBoothExploreFromInitializing()
	{
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getChildFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);
		if (boothExploreFragment != null)
		{
			FragmentHelper.showFragment(
				getChildFragmentManager(),
				boothExploreFragment);
		}
	}

	@Override
	public void showBoothExploreAndHideBoothList()
	{
		BoothListFragment boothListFragment = (BoothListFragment) getChildFragmentManager().findFragmentByTag(BoothListFragment.TAG);
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getChildFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);

		if (boothListFragment != null && boothListFragment.isVisible() && boothExploreFragment != null)
		{
			// Out
			boothListFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothListFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothExploreFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothExploreFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				boothExploreFragment,
				boothListFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showBoothExploreAndHideSchedulePage()
	{
		ScheduleFragment scheduleFragment = (ScheduleFragment) getChildFragmentManager().findFragmentByTag(ScheduleFragment.TAG);
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getChildFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);

		if (scheduleFragment != null && scheduleFragment.isVisible() && boothExploreFragment != null)
		{
			// Out
			scheduleFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			scheduleFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothExploreFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothExploreFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				boothExploreFragment,
				scheduleFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showBoothExploreAndHideInfoPage()
	{
		InfoFragment infoFragment = (InfoFragment) getChildFragmentManager().findFragmentByTag(InfoFragment.TAG);
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getChildFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);

		if (infoFragment != null && infoFragment.isVisible() && boothExploreFragment != null)
		{
			// Out
			infoFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			infoFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothExploreFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothExploreFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				boothExploreFragment,
				infoFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showBoothListAndHideBoothExplore()
	{
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getChildFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);
		BoothListFragment boothListFragment = (BoothListFragment) getChildFragmentManager().findFragmentByTag(BoothListFragment.TAG);

		if (boothExploreFragment != null && boothExploreFragment.isVisible() && boothListFragment != null)
		{
			// Out
			boothExploreFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothExploreFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothListFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothListFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				boothListFragment,
				boothExploreFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showBoothListAndHideSchedulePage()
	{
		ScheduleFragment scheduleFragment = (ScheduleFragment) getChildFragmentManager().findFragmentByTag(ScheduleFragment.TAG);
		BoothListFragment boothListFragment = (BoothListFragment) getChildFragmentManager().findFragmentByTag(BoothListFragment.TAG);

		if (scheduleFragment != null && scheduleFragment.isVisible() && boothListFragment != null)
		{
			// Out
			scheduleFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			scheduleFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothListFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothListFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				boothListFragment,
				scheduleFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showBoothListAndHideInfoPage()
	{
		InfoFragment infoFragment = (InfoFragment) getChildFragmentManager().findFragmentByTag(InfoFragment.TAG);
		BoothListFragment boothListFragment = (BoothListFragment) getChildFragmentManager().findFragmentByTag(BoothListFragment.TAG);

		if (infoFragment != null && infoFragment.isVisible() && boothListFragment != null)
		{
			// Out
			infoFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			infoFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothListFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothListFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				boothListFragment,
				infoFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showSchedulePageAndHideBoothExplore()
	{
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getChildFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);
		ScheduleFragment scheduleFragment = (ScheduleFragment) getChildFragmentManager().findFragmentByTag(ScheduleFragment.TAG);

		if (boothExploreFragment != null && boothExploreFragment.isVisible() && scheduleFragment != null)
		{
			// Out
			boothExploreFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothExploreFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			scheduleFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			scheduleFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				scheduleFragment,
				boothExploreFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showSchedulePageAndHideBoothList()
	{
		BoothListFragment boothListFragment = (BoothListFragment) getChildFragmentManager().findFragmentByTag(BoothListFragment.TAG);
		ScheduleFragment scheduleFragment = (ScheduleFragment) getChildFragmentManager().findFragmentByTag(ScheduleFragment.TAG);

		if (boothListFragment != null && boothListFragment.isVisible() && scheduleFragment != null)
		{
			// Out
			boothListFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothListFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			scheduleFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			scheduleFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				scheduleFragment,
				boothListFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showSchedulePageAndHideInfoPage()
	{
		InfoFragment infoFragment = (InfoFragment) getChildFragmentManager().findFragmentByTag(InfoFragment.TAG);
		ScheduleFragment scheduleFragment = (ScheduleFragment) getChildFragmentManager().findFragmentByTag(ScheduleFragment.TAG);

		if (infoFragment != null && infoFragment.isVisible() && scheduleFragment != null)
		{
			// Out
			infoFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			infoFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			scheduleFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			scheduleFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				scheduleFragment,
				infoFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}

	}

	@Override
	public void showInfoPageAndHideBoothExplore()
	{
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getChildFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);
		InfoFragment infoFragment = (InfoFragment) getChildFragmentManager().findFragmentByTag(InfoFragment.TAG);

		if (boothExploreFragment != null && boothExploreFragment.isVisible() && infoFragment != null)
		{
			// Out
			boothExploreFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothExploreFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			infoFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			infoFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				infoFragment,
				boothExploreFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showInfoPageAndHideBoothList()
	{
		BoothListFragment boothListFragment = (BoothListFragment) getChildFragmentManager().findFragmentByTag(BoothListFragment.TAG);
		InfoFragment infoFragment = (InfoFragment) getChildFragmentManager().findFragmentByTag(InfoFragment.TAG);

		if (boothListFragment != null && boothListFragment.isVisible() && infoFragment != null)
		{
			// Out
			boothListFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothListFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			infoFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			infoFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				infoFragment,
				boothListFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showInfoPageAndHideSchedulePage()
	{
		ScheduleFragment scheduleFragment = (ScheduleFragment) getChildFragmentManager().findFragmentByTag(ScheduleFragment.TAG);
		InfoFragment infoFragment = (InfoFragment) getChildFragmentManager().findFragmentByTag(InfoFragment.TAG);

		if (scheduleFragment != null && scheduleFragment.isVisible() && infoFragment != null)
		{
			// Out
			scheduleFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			scheduleFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			infoFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			infoFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndHideFragments(
				getChildFragmentManager(),
				infoFragment,
				scheduleFragment,
				R.id.fadeInAnimation,
				R.id.fadeOutAnimation);
		}
	}

	@Override
	public void selectPageButton()
	{
		int buttonId = this.phonePageManager.getCurrentPage().getLayoutId();
		for (LinearLayout layout : this.navigationLayouts)
		{
			if (layout.getId() == buttonId)
			{
				layout.setSelected(true);
			}
			else
			{
				layout.setSelected(false);
			}
		}
	}

	/**********************************************************************************************
	 * Booth Flipper Interface Implements
	 **********************************************************************************************/
	@Override
	public void resetTimer()
	{
		if (this.boothFlipperListener != null)
		{
			this.boothFlipperListener.resetTimer();
		}
	}

	/**********************************************************************************************
	 * Click Interface Implements
	 **********************************************************************************************/
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.dashboardPhoneExploreLayout:
				if (this.phonePageManager.getCurrentPage() != RealcommPhonePage.BOOTH_EXPLORE)
				{
					changePage(RealcommPhonePage.BOOTH_EXPLORE);
				}
				break;
			case R.id.dashboardPhoneFindLayout:
				if (this.phonePageManager.getCurrentPage() != RealcommPhonePage.BOOTH_LIST)
				{
					changePage(RealcommPhonePage.BOOTH_LIST);
				}
				break;
			case R.id.dashboardPhoneAttendLayout:
				if (this.phonePageManager.getCurrentPage() != RealcommPhonePage.SCHEDULE_PAGE)
				{
					changePage(RealcommPhonePage.SCHEDULE_PAGE);
				}
				break;
			case R.id.dashboardPhoneInfoLayout:
				if (this.phonePageManager.getCurrentPage() != RealcommPhonePage.INFO_PAGE)
				{
					changePage(RealcommPhonePage.INFO_PAGE);
				}
				break;
		}
	}

	private void initPhonePageManager(Bundle savedInstanceState)
	{
		RealcommPhonePage startingPhonePage = RealcommPhonePage.INITIALIZING;
		if (savedInstanceState != null)
		{
			startingPhonePage = (RealcommPhonePage) savedInstanceState.getSerializable(CURRENT_PHONE_PAGE_KEY);
		}

		this.phonePageManager = new RealcommPhonePageManager(startingPhonePage, this);
	}

	private void initNavigationDrawer(View view)
	{
		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		this.navigationDrawer = (LinearLayout) view.findViewById(R.id.navigationDrawerContainer);

		LinearLayout exploreLayout = (LinearLayout) view.findViewById(R.id.dashboardPhoneExploreLayout);
		LinearLayout findLayout = (LinearLayout) view.findViewById(R.id.dashboardPhoneFindLayout);
		LinearLayout attendLayout = (LinearLayout) view.findViewById(R.id.dashboardPhoneAttendLayout);
		LinearLayout infoLayout = (LinearLayout) view.findViewById(R.id.dashboardPhoneInfoLayout);

		exploreLayout.setOnClickListener(this);
		findLayout.setOnClickListener(this);
		attendLayout.setOnClickListener(this);
		infoLayout.setOnClickListener(this);

		this.navigationLayouts.add(exploreLayout);
		this.navigationLayouts.add(findLayout);
		this.navigationLayouts.add(attendLayout);
		this.navigationLayouts.add(infoLayout);

		TextView exploreTextView = (TextView) view.findViewById(R.id.dashboardPhoneExploreTextView);
		TextView findTextView = (TextView) view.findViewById(R.id.dashboardPhoneFindTextView);
		TextView attendTextView = (TextView) view.findViewById(R.id.dashboardPhoneAttendTextView);
		TextView infoTextView = (TextView) view.findViewById(R.id.dashboardPhoneInfoTextView);

		exploreTextView.setTypeface(application.getExo2Font());
		findTextView.setTypeface(application.getExo2Font());
		attendTextView.setTypeface(application.getExo2Font());
		infoTextView.setTypeface(application.getExo2Font());
	}

	private void initializeFragments()
	{
		initializeBoothExploreFragment();
		initializeBoothListFragment();
		initializeScheduleFragment();
		initializeInfoFragment();
		getChildFragmentManager().executePendingTransactions();
	}

	private void initializeBoothExploreFragment()
	{
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getChildFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);
		if (boothExploreFragment == null)
		{
			boothExploreFragment = BoothExploreFragment.newInstance();
			FragmentHelper.addAndHideFragment(getChildFragmentManager(), R.id.phonePageContainer, boothExploreFragment, BoothExploreFragment.TAG);

			getDataChangedListeners().add(boothExploreFragment);
			getAppModeChangedListeners().add(boothExploreFragment);
			this.boothFlipperListener = boothExploreFragment;
		}
	}

	private void initializeBoothListFragment()
	{
		BoothListFragment boothListFragment = (BoothListFragment) getChildFragmentManager().findFragmentByTag(BoothListFragment.TAG);
		if (boothListFragment == null)
		{
			boothListFragment = BoothListFragment.newInstance();
			FragmentHelper.addAndHideFragment(getChildFragmentManager(), R.id.phonePageContainer, boothListFragment, BoothListFragment.TAG);

			getDataChangedListeners().add(boothListFragment);
			getClearFocusListeners().add(boothListFragment);
			getAppModeChangedListeners().add(boothListFragment);
		}
	}

	private void initializeScheduleFragment()
	{
		ScheduleFragment scheduleFragment = (ScheduleFragment) getChildFragmentManager().findFragmentByTag(ScheduleFragment.TAG);
		if (scheduleFragment == null)
		{
			scheduleFragment = ScheduleFragment.newInstance();
			FragmentHelper.addAndHideFragment(getChildFragmentManager(), R.id.phonePageContainer, scheduleFragment, ScheduleFragment.TAG);

			getDataChangedListeners().add(scheduleFragment);
		}
	}

	private void initializeInfoFragment()
	{
		InfoFragment infoFragment = (InfoFragment) getChildFragmentManager().findFragmentByTag(InfoFragment.TAG);
		if (infoFragment == null)
		{
			infoFragment = InfoFragment.newInstance();
			FragmentHelper.addAndHideFragment(getChildFragmentManager(), R.id.phonePageContainer, infoFragment, InfoFragment.TAG);
		}
	}
}

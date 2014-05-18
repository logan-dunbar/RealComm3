package com.openbox.realcomm3.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.openbox.realcomm3.utilities.helpers.LogHelper;
import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseActivity;
import com.openbox.realcomm3.database.models.BoothModel;
import com.openbox.realcomm3.database.models.SelectedBoothModel;
import com.openbox.realcomm3.fragments.BoothExploreFragment;
import com.openbox.realcomm3.fragments.BoothListFragment;
import com.openbox.realcomm3.fragments.DataFragment;
import com.openbox.realcomm3.fragments.ListingPageFragment;
import com.openbox.realcomm3.fragments.ProfilePageFragment;
import com.openbox.realcomm3.fragments.ScheduleFragment;
import com.openbox.realcomm3.fragments.SplashScreenFragment;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.enums.BeaconStatus;
import com.openbox.realcomm3.utilities.enums.BoothSortMode;
import com.openbox.realcomm3.utilities.enums.ProximityRegion;
import com.openbox.realcomm3.utilities.enums.RealcommPage;
import com.openbox.realcomm3.utilities.enums.RealcommPhonePage;
import com.openbox.realcomm3.utilities.helpers.AnimationHelper;
import com.openbox.realcomm3.utilities.helpers.ClearFocusTouchListener;
import com.openbox.realcomm3.utilities.helpers.ToastHelper;
import com.openbox.realcomm3.utilities.interfaces.ActivityInterface;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.AppModeInterface;
import com.openbox.realcomm3.utilities.interfaces.BeaconManagerBoundCallbacks;
import com.openbox.realcomm3.utilities.interfaces.BeaconStatusChangeCallbacks;
import com.openbox.realcomm3.utilities.interfaces.ClearFocusInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;
import com.openbox.realcomm3.utilities.managers.AppModeManager;
import com.openbox.realcomm3.utilities.managers.BeaconStatusManager;
import com.openbox.realcomm3.utilities.managers.RealcommPageManager;
import com.openbox.realcomm3.utilities.managers.RealcommPhonePageManager;
import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

public class RealCommActivity extends BaseActivity implements
	ActivityInterface,
	DataChangedCallbacks,
	DataInterface,
	ClearFocusInterface,
	IBeaconConsumer,
	BeaconStatusChangeCallbacks,
	AppModeChangedCallbacks,
	AppModeInterface,
	RangeNotifier,
	OnClickListener
{
	private static final String APP_MODE_KEY = "appModeKey";
	private static final String APP_MODE_SELECTOR_KEY = "appModeSelectorKey";
	private static final String BEACON_STATUS_KEY = "beaconStatusKey";
	private static final String CURRENT_PAGE_KEY = "currentPageKey";

	private static final int ENABLE_BLUETOOTH_REQUEST = 1;

	// TODO Update to our UUID and Major values, when the time is right
	private static final Region ALL_BEACONS = new Region("regionId", null, null, null);

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	// Page managers
	private RealcommPageManager pageManager;
	private RealcommPhonePageManager phonePageManager;

	// App Mode
	private AppModeManager appModeManager;

	// Beacon Manager
	private IBeaconManager beaconManager;

	// Views
	private ImageView clearRealcommBackground;
	private ImageView blurRealCommBackground;
	private LinearLayout navigationDrawer;
	private List<Button> navigationButtons = new ArrayList<>();

	// Data listeners
	private List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();
	private DataInterface dataInterface;
	private List<ClearFocusInterface> clearFocusListeners = new ArrayList<>();
	private List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();
	private BeaconManagerBoundCallbacks beaconManagerBoundListener;

	// Globals
	private SelectedBoothModel selectedBooth;
	private boolean isLargeScreen;

	/**********************************************************************************************
	 * Activity Lifecycle Implements
	 **********************************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null); // Dark background not needed anymore
		setIsLargeScreen();
		setScreenOrientation();
		setContentView(R.layout.activity_realcomm);

		getWindow().getDecorView().getRootView().setOnTouchListener(new ClearFocusTouchListener(this, this));

		this.clearRealcommBackground = (ImageView) findViewById(R.id.clearRealcommBackgroundImageView);
		this.blurRealCommBackground = (ImageView) findViewById(R.id.blurRealcommBackgroundImageView);

		// Initialize the page managers
		initStateManagers(savedInstanceState);

		// Initialize the Data Fragment
		initializeDataFragment();

		// Show SplashScreen if required
		showSplashScreen();
	}

	private void showSplashScreen()
	{
		if (this.isLargeScreen)
		{
			if (this.pageManager.getCurrentPage() == RealcommPage.INITIALIZING)
			{
				this.pageManager.changePage(RealcommPage.SPLASH_SCREEN);
			}
		}
		else
		{
			if (this.phonePageManager.getCurrentPage() == RealcommPhonePage.INITIALIZING)
			{
				this.phonePageManager.changePage(RealcommPhonePage.SPLASH_SCREEN);
			}
		}
	}

	private void setScreenOrientation()
	{
		if (this.isLargeScreen)
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		else
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the current state of the system
		outState.putSerializable(APP_MODE_KEY, this.appModeManager.getCurrentAppMode());
		outState.putSerializable(APP_MODE_SELECTOR_KEY, this.appModeManager.getCurrentAppModeSelector());
		outState.putSerializable(BEACON_STATUS_KEY, this.appModeManager.getCurrentBeaconStatus());

		if (this.isLargeScreen)
		{
			outState.putSerializable(CURRENT_PAGE_KEY, this.pageManager.getCurrentPage());
		}
		else
		{
			outState.putSerializable(CURRENT_PAGE_KEY, this.phonePageManager.getCurrentPage());
		}
	}

	@Override
	public void onAttachFragment(Fragment fragment)
	{
		super.onAttachFragment(fragment);

		if (fragment instanceof DataChangedCallbacks)
		{
			this.dataChangedListeners.add((DataChangedCallbacks) fragment);
		}

		if (fragment instanceof DataInterface)
		{
			this.dataInterface = (DataInterface) fragment;
		}

		if (fragment instanceof ClearFocusInterface)
		{
			this.clearFocusListeners.add((ClearFocusInterface) fragment);
		}

		if (fragment instanceof AppModeChangedCallbacks)
		{
			this.appModeChangedListeners.add((AppModeChangedCallbacks) fragment);
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		registerBluetoothReceiver();

		bindCheckUpdateReceiver();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// Decide which mode the app is currently in
		determineAppMode();
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		// Pause the app
		changeAppMode(AppMode.PAUSED);
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		// Make sure to free up unneeded resources
		this.appModeManager.unbindBeaconManager();

		unregisterBluetoothReceiver();

		unbindCheckUpdateReceiver();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// Clean up
		this.dataChangedListeners.clear();
		this.dataInterface = null;
		this.clearFocusListeners.clear();
		this.appModeChangedListeners.clear();
		this.beaconManagerBoundListener = null;
	}

	@Override
	public void onBackPressed()
	{
		// Intercept the back button to do a custom transition when on the ProfilePage
		if (this.isLargeScreen)
		{
			if (this.pageManager.getCurrentPage() != RealcommPage.LISTING_PAGE)
			{
				this.changePage(RealcommPage.LISTING_PAGE);
			}
			else
			{
				super.onBackPressed();
			}
		}
		else
		{
			// TODO custom back stuff for phone pages
			if (this.phonePageManager.getCurrentPage() == RealcommPhonePage.PROFILE_PAGE)
			{
				this.changePage(this.phonePageManager.getPreviousPage());
			}
			else if (this.phonePageManager.getCurrentPage() != RealcommPhonePage.BOOTH_EXPLORE)
			{
				this.changePage(RealcommPhonePage.BOOTH_EXPLORE);
			}
			else
			{
				super.onBackPressed();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == ENABLE_BLUETOOTH_REQUEST)
		{
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK)
			{
				changeAppMode(AppMode.ONLINE);
			}
			else
			{
				// TODO: needed?
				// ToastHelper.showLongMessage(this, "Enable Bluetooth for Proximity Scanning");
				changeAppMode(AppMode.OFFLINE);
			}
		}
	}

	/**********************************************************************************************
	 * Broadcast Receiver Implements
	 **********************************************************************************************/
	@Override
	public void onDownloadDatabaseReceive(Intent intent)
	{
		// Let fragments know data has changed
		onDataChanged();

		// Clean up
		super.onDownloadDatabaseReceive(intent);
	}

	/**********************************************************************************************
	 * Bluetooth Receiver Methods
	 **********************************************************************************************/
	private void registerBluetoothReceiver()
	{
		if (RealCommApplication.getHasBluetoothLe())
		{
			final IntentFilter filter = new IntentFilter();
			filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			registerReceiver(this.bluetoothReceiver, filter);
		}
	}

	private void unregisterBluetoothReceiver()
	{
		if (RealCommApplication.getHasBluetoothLe())
		{
			unregisterReceiver(this.bluetoothReceiver);
		}
	}

	/**********************************************************************************************
	 * Activity Interface Implements
	 **********************************************************************************************/
	@Override
	public void setSelectedBooth(int boothId, int companyId)
	{
		this.selectedBooth = new SelectedBoothModel(boothId, companyId);
	}

	@Override
	public boolean getIsLargeScreen()
	{
		return this.isLargeScreen;
	}

	@Override
	public void changePage(RealcommPage page)
	{
		this.pageManager.changePage(page);
	}

	@Override
	public void changePage(RealcommPhonePage page)
	{
		this.phonePageManager.setPreviousPage(this.phonePageManager.getCurrentPage());
		this.phonePageManager.changePage(page);
	}

	@Override
	public void initializeFragments()
	{
		if (this.isLargeScreen)
		{
			initializeListingPageFragment();
		}
		else
		{
			initializeBoothExploreFragment();
			initializeBoothListFragment();
			initializeScheduleFragment();
		}

		initializeProfileFragment();
	}

	@Override
	public void showSplashScreenFragment()
	{
		// Does the fade between the clear and blurred backgrounds
		animateRealcommBackground();

		SplashScreenFragment fragment = (SplashScreenFragment) getSupportFragmentManager().findFragmentByTag(SplashScreenFragment.TAG);
		if (fragment == null)
		{
			fragment = SplashScreenFragment.newInstance();

			int fadeIn = getResources().getInteger(R.integer.splashFragmentFadeInDuration);
			int delay = getResources().getInteger(R.integer.splashImageFadeDuration) - fadeIn;
			fragment.setInAnimationDuration(fadeIn);
			fragment.setInAnimationDelay(delay);
			fragment.setInAnimationInterpolator(AnimationInterpolator.DECELERATE);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.id.fadeInAnimation, -1)
				.add(R.id.realcommFragmentContainer, fragment, SplashScreenFragment.TAG)
				.commit();
		}
	}

	@Override
	public void showListingPageAndRemoveSplashScreen()
	{
		SplashScreenFragment splashFragment = (SplashScreenFragment) getSupportFragmentManager().findFragmentByTag(SplashScreenFragment.TAG);
		ListingPageFragment listingFragment = (ListingPageFragment) getSupportFragmentManager().findFragmentByTag(ListingPageFragment.TAG);

		if (splashFragment != null && splashFragment.isVisible() && listingFragment != null)
		{
			// Out
			splashFragment.setOutAnimationDuration(getResources().getInteger(R.integer.splashFragmentToListingFragment));
			splashFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			listingFragment.setInAnimationDuration(getResources().getInteger(R.integer.splashFragmentToListingFragment));
			listingFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			showAndRemoveFragments(listingFragment, splashFragment, R.id.slideUpInAnimation, R.id.slideUpOutAnimation);
		}
	}

	@Override
	public void showProfilePageAndHideListingPage()
	{
		ListingPageFragment listingFragment = (ListingPageFragment) getSupportFragmentManager().findFragmentByTag(ListingPageFragment.TAG);
		ProfilePageFragment profileFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);

		if (listingFragment != null && listingFragment.isVisible() && profileFragment != null)
		{
			profileFragment.updateProfilePage(this.selectedBooth);

			// Out
			listingFragment.setOutAnimationDuration(getResources().getInteger(R.integer.listingFragmentToProfileFragment));
			listingFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// In
			profileFragment.setInAnimationDuration(getResources().getInteger(R.integer.listingFragmentToProfileFragment));
			profileFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			showAndHideFragments(profileFragment, listingFragment, R.id.slideInRightAnimation, R.id.slideOutLeftAnimation);
		}
	}

	@Override
	public void showListingPageAndHideProfilePage()
	{
		ListingPageFragment listingFragment = (ListingPageFragment) getSupportFragmentManager().findFragmentByTag(ListingPageFragment.TAG);
		ProfilePageFragment profileFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);

		if (profileFragment != null && profileFragment.isVisible() && listingFragment != null)
		{
			// Out
			listingFragment.setInAnimationDuration(getResources().getInteger(R.integer.profileFragmentToListingFragment));
			listingFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// In
			profileFragment.setOutAnimationDuration(getResources().getInteger(R.integer.profileFragmentToListingFragment));
			profileFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			showAndHideFragments(listingFragment, profileFragment, R.id.slideInLeftAnimation, R.id.slideOutRightAnimation);
		}
	}

	@Override
	public void showBoothExploreAndRemoveSplashScreen()
	{
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getSupportFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);
		SplashScreenFragment splashScreenFragment = (SplashScreenFragment) getSupportFragmentManager().findFragmentByTag(SplashScreenFragment.TAG);

		if (splashScreenFragment != null && splashScreenFragment.isVisible() && boothExploreFragment != null)
		{
			// Out
			splashScreenFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneSplashToBoothExplore));
			splashScreenFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothExploreFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneSplashToBoothExplore));
			boothExploreFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			showAndRemoveFragments(boothExploreFragment, splashScreenFragment, R.id.slideUpInAnimation, R.id.slideUpOutAnimation);
		}
	}

	@Override
	public void showBoothExploreAndHideBoothList()
	{
		BoothListFragment boothListFragment = (BoothListFragment) getSupportFragmentManager().findFragmentByTag(BoothListFragment.TAG);
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getSupportFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);

		if (boothListFragment != null && boothListFragment.isVisible() && boothExploreFragment != null)
		{
			// Out
			boothListFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothListFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothExploreFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothExploreFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			showAndHideFragments(boothExploreFragment, boothListFragment, R.id.fadeInAnimation, R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showBoothExploreAndRemoveProfilePage()
	{
		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getSupportFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);

		if (profilePageFragment != null && profilePageFragment.isVisible() && boothExploreFragment != null)
		{
			// Out
			profilePageFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationFromProfileDuration));
			profilePageFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// In
			boothExploreFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationFromProfileDuration));
			boothExploreFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			showAndHideFragments(boothExploreFragment, profilePageFragment, R.id.fadeInAnimation, R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showBoothExploreAndHideSchedulePage()
	{
		ScheduleFragment scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(ScheduleFragment.TAG);
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getSupportFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);

		if (scheduleFragment != null && scheduleFragment.isVisible() && boothExploreFragment != null)
		{
			// Out
			scheduleFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			scheduleFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothExploreFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothExploreFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			showAndHideFragments(boothExploreFragment, scheduleFragment, R.id.fadeInAnimation, R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showBoothListAndHideBoothExplore()
	{
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getSupportFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);
		BoothListFragment boothListFragment = (BoothListFragment) getSupportFragmentManager().findFragmentByTag(BoothListFragment.TAG);

		if (boothExploreFragment != null && boothExploreFragment.isVisible() && boothListFragment != null)
		{
			// Out
			boothExploreFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothExploreFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothListFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothListFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			showAndHideFragments(boothListFragment, boothExploreFragment, R.id.fadeInAnimation, R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showBoothListAndHideProfilePage()
	{
		BoothListFragment boothListFragment = (BoothListFragment) getSupportFragmentManager().findFragmentByTag(BoothListFragment.TAG);
		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);

		if (profilePageFragment != null && profilePageFragment.isVisible() && boothListFragment != null)
		{
			// Out
			profilePageFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationFromProfileDuration));
			profilePageFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// In
			boothListFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationFromProfileDuration));
			boothListFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			showAndHideFragments(boothListFragment, profilePageFragment, R.id.slideInLeftAnimation, R.id.slideOutRightAnimation);
		}
	}

	@Override
	public void showBoothListAndHideSchedulePage()
	{
		ScheduleFragment scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(ScheduleFragment.TAG);
		BoothListFragment boothListFragment = (BoothListFragment) getSupportFragmentManager().findFragmentByTag(BoothListFragment.TAG);

		if (scheduleFragment != null && scheduleFragment.isVisible() && boothListFragment != null)
		{
			// Out
			scheduleFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			scheduleFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			boothListFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothListFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			showAndHideFragments(boothListFragment, scheduleFragment, R.id.fadeInAnimation, R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showProfilePageAndHideBoothExplore()
	{
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getSupportFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);
		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);

		if (boothExploreFragment != null && boothExploreFragment.isVisible() && profilePageFragment != null)
		{
			profilePageFragment.updateProfilePage(this.selectedBooth);

			// Out
			profilePageFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationToProfileDuration));
			profilePageFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// In
			boothExploreFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationToProfileDuration));
			boothExploreFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			showAndHideFragments(profilePageFragment, boothExploreFragment, R.id.slideInRightAnimation, R.id.slideOutLeftAnimation);
		}
	}

	@Override
	public void showProfilePageAndHideBoothList()
	{
		BoothListFragment boothListFragment = (BoothListFragment) getSupportFragmentManager().findFragmentByTag(BoothListFragment.TAG);
		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);

		if (boothListFragment != null && boothListFragment.isVisible() && profilePageFragment != null)
		{
			profilePageFragment.updateProfilePage(this.selectedBooth);

			// Out
			profilePageFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationToProfileDuration));
			profilePageFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// In
			boothListFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationToProfileDuration));
			boothListFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			showAndHideFragments(profilePageFragment, boothListFragment, R.id.slideInRightAnimation, R.id.slideOutLeftAnimation);
		}
	}

	@Override
	public void showSchedulePageAndHideBoothExplore()
	{
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getSupportFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);
		ScheduleFragment scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(ScheduleFragment.TAG);

		if (boothExploreFragment != null && boothExploreFragment.isVisible() && scheduleFragment != null)
		{
			// Out
			boothExploreFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothExploreFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			scheduleFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			scheduleFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			showAndHideFragments(scheduleFragment, boothExploreFragment, R.id.fadeInAnimation, R.id.fadeOutAnimation);
		}
	}

	@Override
	public void showSchedulePageAndHideBoothList()
	{
		BoothListFragment boothListFragment = (BoothListFragment) getSupportFragmentManager().findFragmentByTag(BoothListFragment.TAG);
		ScheduleFragment scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(ScheduleFragment.TAG);

		if (boothListFragment != null && boothListFragment.isVisible() && scheduleFragment != null)
		{
			// Out
			boothListFragment.setOutAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			boothListFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			scheduleFragment.setInAnimationDuration(getResources().getInteger(R.integer.phoneNavigationDuration));
			scheduleFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			showAndHideFragments(scheduleFragment, boothListFragment, R.id.fadeInAnimation, R.id.fadeOutAnimation);
		}
	}

	@Override
	public void initNavigationDrawer()
	{
		this.navigationDrawer = (LinearLayout) findViewById(R.id.navigationDrawerContainer);

		Button exploreButton = (Button) findViewById(R.id.exploreButton);
		Button findButton = (Button) findViewById(R.id.findButton);
		Button scheduleButton = (Button) findViewById(R.id.scheduleButton);
		Button infoButton = (Button) findViewById(R.id.infoButton);

		exploreButton.setOnClickListener(this);
		findButton.setOnClickListener(this);
		scheduleButton.setOnClickListener(this);
		infoButton.setOnClickListener(this);

		this.navigationButtons.add(exploreButton);
		this.navigationButtons.add(findButton);
		this.navigationButtons.add(scheduleButton);
		this.navigationButtons.add(infoButton);
	}

	@Override
	public void showNavigationDrawer()
	{
		if (this.navigationDrawer != null)
		{
			this.navigationDrawer.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void hideNavigationDrawer()
	{
		if (this.navigationDrawer != null)
		{
			this.navigationDrawer.setVisibility(View.GONE);
		}
	}

	@Override
	public void selectPageButton()
	{
		int buttonId = this.phonePageManager.getCurrentPage().getButtonId();
		for (Button button : this.navigationButtons)
		{
			if (button.getId() == buttonId)
			{
				button.setSelected(true);
			}
			else
			{
				button.setSelected(false);
			}
		}
	}

	@Override
	public void onSplashScreenAnimationInComplete()
	{
		// Remove the clear picture
		ViewGroup vg = (ViewGroup) this.clearRealcommBackground.getParent();
		vg.removeView(this.clearRealcommBackground);

		// Keep the blurry picture
		this.blurRealCommBackground.setVisibility(View.VISIBLE);

		// Go to listing page
		if (this.isLargeScreen)
		{
			this.pageManager.changePage(RealcommPage.LISTING_PAGE);
		}
		else
		{
			this.phonePageManager.changePage(RealcommPhonePage.BOOTH_EXPLORE);
		}
	}

	@Override
	public void onSplashScreenAnimationOutComplete()
	{
		// TODO Might not need
	}

	/**********************************************************************************************
	 * Activity Interface Implements
	 **********************************************************************************************/
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.exploreButton:
				if (this.phonePageManager.getCurrentPage() != RealcommPhonePage.BOOTH_EXPLORE)
				{

					changePage(RealcommPhonePage.BOOTH_EXPLORE);
				}
				break;
			case R.id.findButton:
				if (this.phonePageManager.getCurrentPage() != RealcommPhonePage.BOOTH_LIST)
				{
					changePage(RealcommPhonePage.BOOTH_LIST);
				}
				break;
			case R.id.scheduleButton:
				if (this.phonePageManager.getCurrentPage() != RealcommPhonePage.SCHEDULE_PAGE)
				{
					changePage(RealcommPhonePage.SCHEDULE_PAGE);
				}
				break;
			case R.id.infoButton:
				// TODO
				ToastHelper.showLongMessage(this, "Still implementing...");
				break;
		}
	}

	/**********************************************************************************************
	 * Data Changed Interface Implements
	 **********************************************************************************************/
	@Override
	public void onDataChanged()
	{
		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onDataChanged();
		}
	}

	@Override
	public void onDataLoaded()
	{
		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onDataLoaded();
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
	 * Data Interface Implements
	 **********************************************************************************************/
	@Override
	public BoothModel getBoothModelForBoothId(int boothId)
	{
		if (this.dataInterface != null)
		{
			return this.dataInterface.getBoothModelForBoothId(boothId);
		}

		return null;
	}

	@Override
	public List<Integer> getClosestBoothIds(int numberOfDisplayBooths)
	{
		if (this.dataInterface != null)
		{
			return this.dataInterface.getClosestBoothIds(numberOfDisplayBooths);
		}

		return null;
	}

	@Override
	public List<Integer> getRandomBoothIds(int numberOfDisplayBooths)
	{
		if (this.dataInterface != null)
		{
			return this.dataInterface.getRandomBoothIds(numberOfDisplayBooths);
		}

		return null;
	}

	@Override
	public List<BoothModel> getBoothModelList(BoothSortMode sortMode)
	{
		if (this.dataInterface != null)
		{
			return this.dataInterface.getBoothModelList(sortMode);
		}

		return null;
	}

	@Override
	public Map<ProximityRegion, Integer> getBoothProximityCounts()
	{
		if (this.dataInterface != null)
		{
			return this.dataInterface.getBoothProximityCounts();
		}

		return null;
	}

	@Override
	public void updateAccuracy(Collection<IBeacon> beaconList)
	{
		if (this.dataInterface != null)
		{
			this.dataInterface.updateAccuracy(beaconList);
		}
	}

	@Override
	public void resetAccuracy()
	{
		if (this.dataInterface != null)
		{
			this.dataInterface.resetAccuracy();
		}
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
		resetAccuracy();
	}

	/**********************************************************************************************
	 * App Mode Interface
	 **********************************************************************************************/
	@Override
	public AppMode getCurrentAppMode()
	{
		if (this.appModeManager != null)
		{
			return this.appModeManager.getCurrentAppMode();
		}

		return null;
	}

	@Override
	public AppMode getPreviousAppMode()
	{
		if (this.appModeManager != null)
		{
			return this.appModeManager.getPreviousAppMode();
		}

		return null;
	}

	/**********************************************************************************************
	 * Beacon Consumer Callbacks
	 **********************************************************************************************/
	@Override
	public void onIBeaconServiceConnect()
	{
		this.beaconManager.setRangeNotifier(this);

		if (this.beaconManagerBoundListener != null)
		{
			this.beaconManagerBoundListener.onBeaconManagerBound();
			this.beaconManagerBoundListener = null;
		}
	}

	/**********************************************************************************************
	 * Range Notifier Callbacks
	 **********************************************************************************************/
	@Override
	public void didRangeBeaconsInRegion(final Collection<IBeacon> beaconList, Region region)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (RealCommActivity.this.dataInterface != null)
				{
					RealCommActivity.this.dataInterface.updateAccuracy(beaconList);
				}
			}
		});
	}

	/**********************************************************************************************
	 * Beacon Status Change Callbacks
	 **********************************************************************************************/
	@Override
	public void initializeBeaconManager()
	{
		this.beaconManager = IBeaconManager.getInstanceForApplication(this);
	}

	@Override
	public void bindBeaconManager()
	{
		this.beaconManager.bind(this);
	}

	@Override
	public void bindBeaconManagerAndRange(BeaconManagerBoundCallbacks listener)
	{
		this.beaconManagerBoundListener = listener;
		this.beaconManager.bind(this);
	}

	@Override
	public void unbindBeaconManager()
	{
		this.beaconManager.unBind(this);
	}

	@Override
	public void startRangingBeacons()
	{
		try
		{
			this.beaconManager.startRangingBeaconsInRegion(ALL_BEACONS);
		}
		catch (RemoteException e)
		{
			LogHelper.Log("Can't range beacons: " + e);
		}
	}

	@Override
	public void stopRangingBeacons()
	{
		try
		{
			this.beaconManager.stopRangingBeaconsInRegion(ALL_BEACONS);
		}
		catch (RemoteException e)
		{
			LogHelper.Log("Can't stop ranging beacons: " + e);
		}
	}

	/**********************************************************************************************
	 * Private helper methods
	 **********************************************************************************************/
	private void initStateManagers(Bundle savedInstanceState)
	{
		BeaconStatus startingBeaconStatus = BeaconStatus.NONEXISTENT;
		AppMode startingAppMode = AppMode.INITIALIZING;
		AppMode startingAppModeSelector = AppMode.ONLINE;
		RealcommPage startingPage = RealcommPage.INITIALIZING;
		RealcommPhonePage startingPhonePage = RealcommPhonePage.INITIALIZING;

		if (savedInstanceState != null)
		{
			startingBeaconStatus = (BeaconStatus) savedInstanceState.getSerializable(BEACON_STATUS_KEY);
			startingAppMode = (AppMode) savedInstanceState.getSerializable(APP_MODE_KEY);
			startingAppModeSelector = (AppMode) savedInstanceState.getSerializable(APP_MODE_SELECTOR_KEY);

			if (this.isLargeScreen)
			{
				startingPage = (RealcommPage) savedInstanceState.getSerializable(CURRENT_PAGE_KEY);
			}
			else
			{
				startingPhonePage = (RealcommPhonePage) savedInstanceState.getSerializable(CURRENT_PAGE_KEY);
			}
		}

		BeaconStatusManager beaconStatusManager = new BeaconStatusManager(this, startingBeaconStatus, RealCommApplication.getHasBluetoothLe());
		this.appModeManager = new AppModeManager(startingAppMode, startingAppModeSelector, beaconStatusManager, this);

		if (this.isLargeScreen)
		{
			this.pageManager = new RealcommPageManager(startingPage, this);
		}
		else
		{
			this.phonePageManager = new RealcommPhonePageManager(startingPhonePage, this);
		}
	}

	private void setIsLargeScreen()
	{
		// Lookup Configuration#screenLayout for more details
		this.isLargeScreen = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
			>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	private void initializeDataFragment()
	{
		DataFragment fragment = (DataFragment) getSupportFragmentManager().findFragmentByTag(DataFragment.TAG);
		if (fragment == null)
		{
			getSupportFragmentManager().beginTransaction().add(DataFragment.newInstance(), DataFragment.TAG).commit();
		}
	}

	private void initializeListingPageFragment()
	{
		ListingPageFragment listingFragment = (ListingPageFragment) getSupportFragmentManager().findFragmentByTag(ListingPageFragment.TAG);
		if (listingFragment == null)
		{
			listingFragment = ListingPageFragment.newInstance();
			addAndHideFragment(R.id.realcommFragmentContainer, listingFragment, ListingPageFragment.TAG);
		}
	}

	private void initializeBoothExploreFragment()
	{
		BoothExploreFragment boothExploreFragment = (BoothExploreFragment) getSupportFragmentManager().findFragmentByTag(BoothExploreFragment.TAG);
		if (boothExploreFragment == null)
		{
			boothExploreFragment = BoothExploreFragment.newInstance();
			addAndHideFragment(R.id.realcommFragmentContainer, boothExploreFragment, BoothExploreFragment.TAG);
			this.dataChangedListeners.add(boothExploreFragment);
			this.appModeChangedListeners.add(boothExploreFragment);
		}
	}

	private void initializeBoothListFragment()
	{
		BoothListFragment boothListFragment = (BoothListFragment) getSupportFragmentManager().findFragmentByTag(BoothListFragment.TAG);
		if (boothListFragment == null)
		{
			boothListFragment = BoothListFragment.newInstance();
			addAndHideFragment(R.id.realcommFragmentContainer, boothListFragment, BoothListFragment.TAG);
			this.dataChangedListeners.add(boothListFragment);
			this.clearFocusListeners.add(boothListFragment);
		}
	}

	private void initializeScheduleFragment()
	{
		ScheduleFragment scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(ScheduleFragment.TAG);
		if (scheduleFragment == null)
		{
			scheduleFragment = ScheduleFragment.newInstance();
			addAndHideFragment(R.id.realcommFragmentContainer, scheduleFragment, ScheduleFragment.TAG);
			this.dataChangedListeners.add(scheduleFragment);
		}
	}

	private void initializeProfileFragment()
	{
		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);
		if (profilePageFragment == null)
		{
			profilePageFragment = ProfilePageFragment.newInstance();
			addAndHideFragment(R.id.realcommFragmentContainer, profilePageFragment, ProfilePageFragment.TAG);
			this.dataChangedListeners.add(profilePageFragment);
		}
	}

	private void addAndHideFragment(int containerId, Fragment fragment, String tag)
	{
		getSupportFragmentManager()
			.beginTransaction()
			.add(containerId, fragment, tag)
			.hide(fragment)
			.commit();
	}

	private void showAndRemoveFragments(Fragment showFragment, Fragment removeFragment, int inAnimationId, int outAnimationId)
	{
		getSupportFragmentManager()
			.beginTransaction()
			.setCustomAnimations(inAnimationId, outAnimationId)
			.remove(removeFragment)
			.show(showFragment)
			.commit();
	}

	private void showAndHideFragments(Fragment showFragment, Fragment hideFragment, int inAnimationId, int outAnimationId)
	{
		getSupportFragmentManager()
			.beginTransaction()
			.setCustomAnimations(inAnimationId, outAnimationId)
			.hide(hideFragment)
			.show(showFragment)
			.commit();
	}

	private void animateRealcommBackground()
	{
		int imageFadeDuration = getResources().getInteger(R.integer.splashImageFadeDuration);
		Animation backgroundImageFadeIn = AnimationHelper.getFadeInAnimation(
			AnimationInterpolator.DECELERATE,
			imageFadeDuration,
			0,
			null);
		Animation backgroundImageFadeOut = AnimationHelper.getFadeOutAnimation(
			AnimationInterpolator.ACCELERATE,
			imageFadeDuration,
			0,
			null);

		this.blurRealCommBackground.setAnimation(backgroundImageFadeIn);
		this.clearRealcommBackground.setAnimation(backgroundImageFadeOut);
	}

	private void changeAppMode(AppMode newAppMode)
	{
		this.appModeManager.setPreviousAppMode(this.appModeManager.getCurrentAppMode());
		this.appModeManager.changeAppMode(newAppMode);
	}

	private void determineAppMode()
	{
		if (this.appModeManager.getCurrentAppModeSelector() == AppMode.ONLINE)
		{
			if (RealCommApplication.getHasBluetoothLe())
			{
				if (RealCommApplication.isBluetoothEnabled(this))
				{
					changeAppMode(AppMode.ONLINE);
				}
				else
				{
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST);
				}
			}
			else
			{
				changeAppMode(AppMode.OFFLINE);
			}
		}
		else
		{
			changeAppMode(AppMode.OFFLINE);
		}
	}

	/**********************************************************************************************
	 * Misc Fields, Methods and Classes
	 **********************************************************************************************/
	private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
			{
				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				switch (state)
				{
					case BluetoothAdapter.STATE_OFF:
						ToastHelper.showLongMessage(RealCommActivity.this, "Offline mode engaged!");
						changeAppMode(AppMode.OFFLINE);
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						// ToastHelper.showShortMessage(ListingPageActivity.this, "Bluetooth turning off!");
						break;
					case BluetoothAdapter.STATE_ON:
						ToastHelper.showLongMessage(RealCommActivity.this, "Online mode engaged!");
						changeAppMode(AppMode.ONLINE);
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						// ToastHelper.showShortMessage(ListingPageActivity.this, "Bluetooth turning on!");
						break;
				}
			}
		}
	};
}

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
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.openbox.realcomm3.services.WebService;
import com.openbox.realcomm3.utilities.helpers.LogHelper;
import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseActivity;
import com.openbox.realcomm3.database.models.BoothModel;
import com.openbox.realcomm3.database.models.SelectedBoothModel;
import com.openbox.realcomm3.fragments.DashboardPhoneFragment;
import com.openbox.realcomm3.fragments.DashboardTabletFragment;
import com.openbox.realcomm3.fragments.DataFragment;
import com.openbox.realcomm3.fragments.ProfilePageFragment;
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
import com.openbox.realcomm3.utilities.helpers.FragmentHelper;
import com.openbox.realcomm3.utilities.helpers.ToastHelper;
import com.openbox.realcomm3.utilities.interfaces.ActivityInterface;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.AppModeInterface;
import com.openbox.realcomm3.utilities.interfaces.BeaconManagerBoundCallbacks;
import com.openbox.realcomm3.utilities.interfaces.BeaconStatusChangeCallbacks;
import com.openbox.realcomm3.utilities.interfaces.BoothFlipperInterface;
import com.openbox.realcomm3.utilities.interfaces.ClearFocusInterface;
import com.openbox.realcomm3.utilities.interfaces.DashboardPhoneInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;
import com.openbox.realcomm3.utilities.managers.AppModeManager;
import com.openbox.realcomm3.utilities.managers.BeaconSaverManager;
import com.openbox.realcomm3.utilities.managers.BeaconStatusManager;
import com.openbox.realcomm3.utilities.managers.RealcommPageManager;
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
	BoothFlipperInterface
{
	private static final String APP_MODE_KEY = "appModeKey";
	private static final String APP_MODE_SELECTOR_KEY = "appModeSelectorKey";
	private static final String BEACON_STATUS_KEY = "beaconStatusKey";
	private static final String CURRENT_PAGE_KEY = "currentPageKey";

	private static final int ENABLE_BLUETOOTH_REQUEST = 1;

	private static final long SLOW_FOREGROUND_BEACON_SCAN_BETWEEN_PERIOD = 1000;

	// TODO Update to our UUID and Major values, when the time is right
	private static final Region ALL_BEACONS = new Region("regionId", null, null, null);

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	// Page managers
	private RealcommPageManager pageManager;
	// private RealcommPhonePageManager phonePageManager;

	// App Mode
	private AppModeManager appModeManager;

	// Beacon Manager
	private IBeaconManager beaconManager;

	// Beacon Saver Manager
	private BeaconSaverManager beaconSaverManager;

	// Views
	private ImageView clearRealcommBackground;
	private ImageView blurRealCommBackground;

	// Data listeners
	private DataInterface dataInterface;
	private BeaconManagerBoundCallbacks beaconManagerBoundListener;
	private BoothFlipperInterface boothFlipperListener;
	private DashboardPhoneInterface dashboardPhoneListener;

	private final List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();
	private final List<ClearFocusInterface> clearFocusListeners = new ArrayList<>();
	private final List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();

	// Globals
	private SelectedBoothModel selectedBooth;
	private long beaconScanBetweenPeriod = IBeaconManager.DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD;

	/**********************************************************************************************
	 * Activity Lifecycle Implements
	 **********************************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme();
		setupActionBar(); // Crashes saying "call requestFeature() before setting content"

		super.onCreate(savedInstanceState);

		setScreenOrientation();
		getWindow().setBackgroundDrawable(null); // Dark background not needed anymore
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

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the current state of the system
		outState.putSerializable(APP_MODE_KEY, this.appModeManager.getCurrentAppMode());
		outState.putSerializable(APP_MODE_SELECTOR_KEY, this.appModeManager.getCurrentAppModeSelector());
		outState.putSerializable(BEACON_STATUS_KEY, this.appModeManager.getCurrentBeaconStatus());
		outState.putSerializable(CURRENT_PAGE_KEY, this.pageManager.getCurrentPage());
	}

	@Override
	public void onAttachFragment(Fragment fragment)
	{
		super.onAttachFragment(fragment);

		if (fragment instanceof DataInterface)
		{
			this.dataInterface = (DataInterface) fragment;
		}

		if (fragment instanceof DataChangedCallbacks)
		{
			this.dataChangedListeners.add((DataChangedCallbacks) fragment);
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
		registerDownloadStartingReceiver();
		registerDownloadCompleteReceiver();

		// Check each time app opens
		startWebService();
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

		unregisterDownloadStartingReceiver();
		unregisterDownloadCompleteReceiver();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// Clean up
		this.dataInterface = null;
		this.beaconManagerBoundListener = null;
		this.boothFlipperListener = null;
		this.dashboardPhoneListener = null;

		this.dataChangedListeners.clear();
		this.clearFocusListeners.clear();
		this.appModeChangedListeners.clear();
	}

	@Override
	public void onBackPressed()
	{
		// Intercept the back button to do a custom transition when on the ProfilePage
		if (this.pageManager.getCurrentPage() == RealcommPage.PROFILE_PAGE)
		{
			changePage(RealcommPage.DASHBOARD_PAGE);
		}
		else if (!RealCommApplication.getIsLargeScreen())
		{
			if (this.dashboardPhoneListener != null)
			{
				boolean handled = this.dashboardPhoneListener.onBackBressed();
				if (!handled)
				{
					super.onBackPressed();
				}
			}
		}
		else
		{
			super.onBackPressed();
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
				changeAppMode(AppMode.OFFLINE);
			}
		}
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
	 * Download Database Receiver Methods
	 **********************************************************************************************/
	private void registerDownloadStartingReceiver()
	{
		final IntentFilter filter = new IntentFilter(WebService.DOWNLOAD_STARTING_INTENT);
		LocalBroadcastManager.getInstance(this).registerReceiver(this.downloadStartingReceiver, filter);
	}

	private void unregisterDownloadStartingReceiver()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(this.downloadStartingReceiver);
	}

	private void registerDownloadCompleteReceiver()
	{
		final IntentFilter filter = new IntentFilter(WebService.DOWNLOAD_FINISHED_INTENT);
		LocalBroadcastManager.getInstance(this).registerReceiver(this.downloadCompleteReceiver, filter);
	}

	private void unregisterDownloadCompleteReceiver()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(this.downloadCompleteReceiver);
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
	public void changePage(RealcommPage page)
	{
		this.pageManager.changePage(page);
	}

	@Override
	public void initializeFragments()
	{
		initializeDashboardFragment();
		initializeProfileFragment();
	}

	@Override
	public void showSplashScreenFragment()
	{
		// Does the fade between the clear and blurred backgrounds
		animateRealcommBackground();

		SplashScreenFragment splashScreenFragment = (SplashScreenFragment) getSupportFragmentManager().findFragmentByTag(SplashScreenFragment.TAG);
		if (splashScreenFragment == null)
		{
			splashScreenFragment = SplashScreenFragment.newInstance();

			int fadeIn = getResources().getInteger(R.integer.splashFragmentFadeInDuration);
			int delay = getResources().getInteger(R.integer.splashImageFadeDuration) - fadeIn;
			splashScreenFragment.setInAnimationDuration(fadeIn);
			splashScreenFragment.setInAnimationDelay(delay);
			splashScreenFragment.setInAnimationInterpolator(AnimationInterpolator.DECELERATE);
			splashScreenFragment.setInAnimationCompleteListener(this.inSplashScreenAnimationCompleteRunnable);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.id.fadeInAnimation, -1)
				.add(R.id.realcommFragmentContainer, splashScreenFragment, SplashScreenFragment.TAG)
				.commit();
		}
	}

	@Override
	public void showDashboardAndRemoveSplashScreen()
	{
		if (RealCommApplication.getIsLargeScreen())
		{
			showDashboardTabletAndRemoveSplashScreen();
		}
		else
		{
			showDashboardPhoneAndRemoveSplashScreen();
		}
	}

	private void showDashboardTabletAndRemoveSplashScreen()
	{
		SplashScreenFragment splashScreenFragment = (SplashScreenFragment) getSupportFragmentManager().findFragmentByTag(SplashScreenFragment.TAG);
		DashboardTabletFragment dashboardTabletFragment = (DashboardTabletFragment) getSupportFragmentManager().findFragmentByTag(DashboardTabletFragment.TAG);
		if (splashScreenFragment != null && splashScreenFragment.isVisible() && dashboardTabletFragment != null)
		{
			// Out
			splashScreenFragment.setOutAnimationDuration(getResources().getInteger(R.integer.splashFragmentToDashboardFragment));
			splashScreenFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			dashboardTabletFragment.setInAnimationDuration(getResources().getInteger(R.integer.splashFragmentToDashboardFragment));
			dashboardTabletFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			FragmentHelper.showAndRemoveFragments(
				getSupportFragmentManager(),
				dashboardTabletFragment,
				splashScreenFragment,
				R.id.slideUpInAnimation,
				R.id.slideUpOutAnimation);
		}
	}

	private void showDashboardPhoneAndRemoveSplashScreen()
	{
		SplashScreenFragment splashScreenFragment = (SplashScreenFragment) getSupportFragmentManager().findFragmentByTag(SplashScreenFragment.TAG);
		DashboardPhoneFragment dashboardPhoneFragment = (DashboardPhoneFragment) getSupportFragmentManager().findFragmentByTag(DashboardPhoneFragment.TAG);
		if (splashScreenFragment != null && splashScreenFragment.isVisible() && dashboardPhoneFragment != null)
		{
			// Out
			splashScreenFragment.setOutAnimationDuration(getResources().getInteger(R.integer.splashFragmentToDashboardFragment));
			splashScreenFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			dashboardPhoneFragment.setInAnimationDuration(getResources().getInteger(R.integer.splashFragmentToDashboardFragment));
			dashboardPhoneFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			if (this.dashboardPhoneListener != null)
			{
				this.dashboardPhoneListener.changePage(RealcommPhonePage.BOOTH_EXPLORE);
			}

			FragmentHelper.showAndRemoveFragments(
				getSupportFragmentManager(),
				dashboardPhoneFragment,
				splashScreenFragment,
				R.id.slideUpInAnimation,
				R.id.slideUpOutAnimation);
		}
	}

	@Override
	public void showProfilePageAndHideDashboard()
	{
		if (RealCommApplication.getIsLargeScreen())
		{
			showProfilePageAndHideDashboardTablet();
		}
		else
		{
			showProfilePageAndHideDashboardPhone();
		}
	}

	private void showProfilePageAndHideDashboardTablet()
	{
		DashboardTabletFragment dashboardTabletFragment = (DashboardTabletFragment) getSupportFragmentManager().findFragmentByTag(DashboardTabletFragment.TAG);
		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);
		if (dashboardTabletFragment != null && dashboardTabletFragment.isVisible() && profilePageFragment != null)
		{
			profilePageFragment.updateProfilePage(this.selectedBooth);

			// Out
			dashboardTabletFragment.setOutAnimationDuration(getResources().getInteger(R.integer.dashboardFragmentToProfileFragment));
			dashboardTabletFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// In
			profilePageFragment.setInAnimationDuration(getResources().getInteger(R.integer.dashboardFragmentToProfileFragment));
			profilePageFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			FragmentHelper.showAndHideFragments(
				getSupportFragmentManager(),
				profilePageFragment,
				dashboardTabletFragment,
				R.id.slideInRightAnimation,
				R.id.slideOutLeftAnimation);
		}
	}

	private void showProfilePageAndHideDashboardPhone()
	{
		DashboardPhoneFragment dashboardPhoneFragment = (DashboardPhoneFragment) getSupportFragmentManager().findFragmentByTag(DashboardPhoneFragment.TAG);
		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);
		if (dashboardPhoneFragment != null && dashboardPhoneFragment.isVisible() && profilePageFragment != null)
		{
			profilePageFragment.updateProfilePage(this.selectedBooth);

			// Out
			dashboardPhoneFragment.setOutAnimationDuration(getResources().getInteger(R.integer.dashboardFragmentToProfileFragment));
			dashboardPhoneFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// In
			profilePageFragment.setInAnimationDuration(getResources().getInteger(R.integer.dashboardFragmentToProfileFragment));
			profilePageFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			FragmentHelper.showAndHideFragments(
				getSupportFragmentManager(),
				profilePageFragment,
				dashboardPhoneFragment,
				R.id.slideInRightAnimation,
				R.id.slideOutLeftAnimation);
		}
	}

	@Override
	public void showDashboardAndHideProfilePage()
	{
		if (RealCommApplication.getIsLargeScreen())
		{
			showDashboardTabletAndHideProfilePage();
		}
		else
		{
			showDashboardPhoneAndHideProfilePage();
		}
	}

	private void showDashboardTabletAndHideProfilePage()
	{
		DashboardTabletFragment dashboardTabletFragment = (DashboardTabletFragment) getSupportFragmentManager().findFragmentByTag(DashboardTabletFragment.TAG);
		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);
		if (profilePageFragment != null && profilePageFragment.isVisible() && dashboardTabletFragment != null)
		{
			// Out
			dashboardTabletFragment.setInAnimationDuration(getResources().getInteger(R.integer.profileFragmentToDashboardFragment));
			dashboardTabletFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// In
			profilePageFragment.setOutAnimationDuration(getResources().getInteger(R.integer.profileFragmentToDashboardFragment));
			profilePageFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			FragmentHelper.showAndHideFragments(
				getSupportFragmentManager(),
				dashboardTabletFragment,
				profilePageFragment,
				R.id.slideInLeftAnimation,
				R.id.slideOutRightAnimation);
		}
	}

	private void showDashboardPhoneAndHideProfilePage()
	{
		DashboardPhoneFragment dashboardPhoneFragment = (DashboardPhoneFragment) getSupportFragmentManager().findFragmentByTag(DashboardPhoneFragment.TAG);
		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);
		if (profilePageFragment != null && profilePageFragment.isVisible() && dashboardPhoneFragment != null)
		{
			// Out
			dashboardPhoneFragment.setInAnimationDuration(getResources().getInteger(R.integer.profileFragmentToDashboardFragment));
			dashboardPhoneFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// In
			profilePageFragment.setOutAnimationDuration(getResources().getInteger(R.integer.profileFragmentToDashboardFragment));
			profilePageFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			FragmentHelper.showAndHideFragments(
				getSupportFragmentManager(),
				dashboardPhoneFragment,
				profilePageFragment,
				R.id.slideInLeftAnimation,
				R.id.slideOutRightAnimation);
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
	public BoothModel getBoothModelForCompanyName(String companyName)
	{
		if (this.dataInterface != null)
		{
			return this.dataInterface.getBoothModelForCompanyName(companyName);
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
	public List<BoothModel> getBoothModelList()
	{
		if (this.dataInterface != null)
		{
			return this.dataInterface.getBoothModelList();
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
	 * App Mode Interface
	 **********************************************************************************************/
	@Override
	public void changeAppMode(AppMode newAppMode)
	{
		this.appModeManager.setPreviousAppMode(this.appModeManager.getCurrentAppMode());
		this.appModeManager.changeAppMode(newAppMode);
	}

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
		updateBeaconScanPeriod();
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
	 * Booth Flipper Implements
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
	 * Private helper methods
	 **********************************************************************************************/
	private void initStateManagers(Bundle savedInstanceState)
	{
		BeaconStatus startingBeaconStatus = BeaconStatus.NONEXISTENT;
		AppMode startingAppMode = AppMode.INITIALIZING;
		AppMode startingAppModeSelector = AppMode.ONLINE;
		RealcommPage startingPage = RealcommPage.INITIALIZING;

		// TODO this is wrong, should not 'set' the state, should save and then retrace steps
		if (savedInstanceState != null)
		{
			startingBeaconStatus = (BeaconStatus) savedInstanceState.getSerializable(BEACON_STATUS_KEY);
			startingAppMode = (AppMode) savedInstanceState.getSerializable(APP_MODE_KEY);
			startingAppModeSelector = (AppMode) savedInstanceState.getSerializable(APP_MODE_SELECTOR_KEY);
			startingPage = (RealcommPage) savedInstanceState.getSerializable(CURRENT_PAGE_KEY);
		}

		BeaconStatusManager beaconStatusManager = new BeaconStatusManager(this, startingBeaconStatus, RealCommApplication.getHasBluetoothLe());
		this.appModeManager = new AppModeManager(startingAppMode, startingAppModeSelector, beaconStatusManager, this, this);
		this.pageManager = new RealcommPageManager(startingPage, this);

		this.beaconSaverManager = new BeaconSaverManager(this, this, this);
		this.dataChangedListeners.add(this.beaconSaverManager);
		this.appModeChangedListeners.add(this.beaconSaverManager);
	}

	private void setTheme()
	{
		if (RealCommApplication.getIsLargeScreen())
		{
			setTheme(R.style.TabletTheme);
		}
	}

	private void setupActionBar()
	{
		if (RealCommApplication.getIsLargeScreen())
		{
			getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		}
	}

	private void showSplashScreen()
	{
		if (this.pageManager.getCurrentPage() == RealcommPage.INITIALIZING)
		{
			this.pageManager.changePage(RealcommPage.SPLASH_SCREEN);
		}
	}

	private void setScreenOrientation()
	{
		if (RealCommApplication.getIsLargeScreen())
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		else
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	private void determineAppMode()
	{
		// ToastHelper.showShortMessage(this, "Determining App Mode...");
		// Technically should sit in the AppModeManager, with method calls
		// for all of these, but this is neater/more readable
		if (this.appModeManager.getCurrentAppModeSelector() == AppMode.ONLINE)
		{
			if (RealCommApplication.getHasBluetoothLe())
			{
				if (RealCommApplication.isBluetoothEnabled())
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

	private void startWebService()
	{
		Intent serviceIntent = new Intent(this, WebService.class);
		serviceIntent.setAction(WebService.DOWNLOAD_DATABASE_ACTION);
		startService(serviceIntent);
	}

	// Slow down and speed up needed because RadiusNetworks' service ranges on the UI Thread,
	// which has a much higher priority than a background http request, and was causing the
	// db sync to fail with JsonIOException - SocketException (Connection reset by peer)
	private void slowDownBeaconUpdateRate()
	{
		this.beaconScanBetweenPeriod = SLOW_FOREGROUND_BEACON_SCAN_BETWEEN_PERIOD;
		updateBeaconScanPeriod();
	}

	private void speedUpBeaconUpdateRate()
	{
		this.beaconScanBetweenPeriod = IBeaconManager.DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD;
		updateBeaconScanPeriod();
	}

	private void updateBeaconScanPeriod()
	{
		if (this.beaconManager != null)
		{
			this.beaconManager.setForegroundBetweenScanPeriod(this.beaconScanBetweenPeriod);

			if (this.beaconManager.isBound(this))
			{
				try
				{
					this.beaconManager.updateScanPeriods();
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void initializeDataFragment()
	{
		DataFragment fragment = (DataFragment) getSupportFragmentManager().findFragmentByTag(DataFragment.TAG);
		if (fragment == null)
		{
			getSupportFragmentManager().beginTransaction().add(DataFragment.newInstance(), DataFragment.TAG).commit();
		}
	}

	private void initializeDashboardFragment()
	{
		if (RealCommApplication.getIsLargeScreen())
		{
			initializeDashboardTabletFragment();
		}
		else
		{
			initializeDashboardPhoneFragment();
		}
	}

	private void initializeDashboardTabletFragment()
	{
		DashboardTabletFragment dashboardTabletFragment = (DashboardTabletFragment) getSupportFragmentManager().findFragmentByTag(
			DashboardTabletFragment.TAG);
		if (dashboardTabletFragment == null)
		{
			dashboardTabletFragment = DashboardTabletFragment.newInstance();
			FragmentHelper.addAndHideFragment(
				getSupportFragmentManager(),
				R.id.realcommFragmentContainer,
				dashboardTabletFragment,
				DashboardTabletFragment.TAG);

			this.boothFlipperListener = dashboardTabletFragment;
			this.dataChangedListeners.add(dashboardTabletFragment);
			this.appModeChangedListeners.add(dashboardTabletFragment);
			this.clearFocusListeners.add(dashboardTabletFragment);
		}
	}

	private void initializeDashboardPhoneFragment()
	{
		DashboardPhoneFragment dashboardPhoneFragment = (DashboardPhoneFragment) getSupportFragmentManager().findFragmentByTag(
			DashboardPhoneFragment.TAG);
		if (dashboardPhoneFragment == null)
		{
			dashboardPhoneFragment = DashboardPhoneFragment.newInstance();
			FragmentHelper.addAndHideFragment(
				getSupportFragmentManager(),
				R.id.realcommFragmentContainer,
				dashboardPhoneFragment,
				DashboardPhoneFragment.TAG);

			this.boothFlipperListener = dashboardPhoneFragment;
			this.dataChangedListeners.add(dashboardPhoneFragment);
			this.appModeChangedListeners.add(dashboardPhoneFragment);
			this.clearFocusListeners.add(dashboardPhoneFragment);
			this.dashboardPhoneListener = dashboardPhoneFragment;
		}
	}

	private void initializeProfileFragment()
	{
		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);
		if (profilePageFragment == null)
		{
			profilePageFragment = ProfilePageFragment.newInstance();
			FragmentHelper.addAndHideFragment(
				getSupportFragmentManager(),
				R.id.realcommFragmentContainer,
				profilePageFragment,
				ProfilePageFragment.TAG);

			this.dataChangedListeners.add(profilePageFragment);
		}
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

	private void completeSplashAnimation()
	{
		// Remove the clear picture
		ViewGroup vg = (ViewGroup) this.clearRealcommBackground.getParent();
		vg.removeView(this.clearRealcommBackground);

		// Keep the blurry picture
		this.blurRealCommBackground.setVisibility(View.VISIBLE);

		// Go to dashboard
		this.pageManager.changePage(RealcommPage.DASHBOARD_PAGE);
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
						if (RealCommApplication.getHasBluetoothLe())
						{
							changeAppMode(AppMode.ONLINE);
						}
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						// ToastHelper.showShortMessage(ListingPageActivity.this, "Bluetooth turning on!");
						break;
				}
			}
		}
	};

	private BroadcastReceiver downloadStartingReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			slowDownBeaconUpdateRate();
		}
	};

	private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			onDataChanged();
			speedUpBeaconUpdateRate();
		}
	};

	private Runnable inSplashScreenAnimationCompleteRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			completeSplashAnimation();
		}
	};
}

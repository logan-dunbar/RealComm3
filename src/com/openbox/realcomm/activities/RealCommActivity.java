package com.openbox.realcomm.activities;

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
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.database.models.BoothModel;
import com.openbox.realcomm.database.models.SelectedBoothModel;
import com.openbox.realcomm.dialogfragments.InfoDialogFragment;
import com.openbox.realcomm.fragments.DashboardPhoneFragment;
import com.openbox.realcomm.fragments.DashboardTabletFragment;
import com.openbox.realcomm.fragments.DataFragment;
import com.openbox.realcomm.fragments.ProfilePageFragment;
import com.openbox.realcomm.fragments.SplashScreenFragment;
import com.openbox.realcomm.services.WebService;
import com.openbox.realcomm.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm.utilities.enums.AppMode;
import com.openbox.realcomm.utilities.enums.BoothSortMode;
import com.openbox.realcomm.utilities.enums.ProximityRegion;
import com.openbox.realcomm.utilities.enums.RealcommPage;
import com.openbox.realcomm.utilities.enums.RealcommPhonePage;
import com.openbox.realcomm.utilities.helpers.AnimationHelper;
import com.openbox.realcomm.utilities.helpers.ClearFocusTouchListener;
import com.openbox.realcomm.utilities.helpers.FragmentHelper;
import com.openbox.realcomm.utilities.interfaces.ActivityInterface;
import com.openbox.realcomm.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm.utilities.interfaces.AppModeInterface;
import com.openbox.realcomm.utilities.interfaces.BoothFlipperInterface;
import com.openbox.realcomm.utilities.interfaces.ClearFocusInterface;
import com.openbox.realcomm.utilities.interfaces.DashboardPhoneInterface;
import com.openbox.realcomm.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm.utilities.interfaces.DataInterface;
import com.openbox.realcomm.utilities.managers.AppModeManager;
import com.openbox.realcomm.utilities.managers.BeaconSaverManager;
import com.openbox.realcomm.utilities.managers.RealcommPageManager;
import com.openbox.realcomm.R;
import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

public class RealCommActivity extends FragmentActivity implements
	ActivityInterface,
	DataChangedCallbacks,
	DataInterface,
	ClearFocusInterface,
	IBeaconConsumer,
	AppModeChangedCallbacks,
	AppModeInterface,
	RangeNotifier,
	BoothFlipperInterface
{
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
	private BoothFlipperInterface boothFlipperListener;
	private DashboardPhoneInterface dashboardPhoneListener;

	private final List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();
	private final List<ClearFocusInterface> clearFocusListeners = new ArrayList<>();
	private final List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();

	// Globals
	private SelectedBoothModel selectedBooth;
	private long beaconScanBetweenPeriod = IBeaconManager.DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD;
	private boolean bluetoothReceiverIsRegistered = false;
	private boolean showingEnableBluetoothRequest = false;
	private boolean showInfoMenuItem = false;

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

		// For closing keyboard
		getWindow().getDecorView().getRootView().setOnTouchListener(new ClearFocusTouchListener(this, this));

		this.clearRealcommBackground = (ImageView) findViewById(R.id.clearRealcommBackgroundImageView);
		this.blurRealCommBackground = (ImageView) findViewById(R.id.blurRealcommBackgroundImageView);

		// Initialize the managers
		initManagers(savedInstanceState);

		// Initialize the Data Fragment
		initializeDataFragment();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the current page
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

		// Create each start so it can go through the motions of determining BT state etc
		this.appModeManager = new AppModeManager(this, this);

		registerDownloadStartingReceiver();
		registerDownloadCompleteReceiver();

		// Check each time app opens
		startWebService();

		if (RealCommApplication.getHasBluetoothLe())
		{
			registerBluetoothReceiver();

			if (RealCommApplication.isBluetoothEnabled())
			{
				changeAppMode(AppMode.ONLINE);
			}
			else
			{
				if (!this.showingEnableBluetoothRequest)
				{
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST);
					showingEnableBluetoothRequest = true;
				}
			}
		}
		else
		{
			changeAppMode(AppMode.OFFLINE);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		this.beaconSaverManager.stop();

		unregisterDownloadStartingReceiver();
		unregisterDownloadCompleteReceiver();

		if (RealCommApplication.getHasBluetoothLe())
		{
			unregisterBluetoothReceiver();
			unbindBeaconManager();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// Clean up
		this.dataInterface = null;
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
				boolean handled = this.dashboardPhoneListener.onBackPressed();
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
		super.onActivityResult(requestCode, resultCode, data);

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

			this.showingEnableBluetoothRequest = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Careful when this is called in the Lifecycle
		if (RealCommApplication.getIsLargeScreen())
		{
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.main_menu, menu);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.infoMenuItem:
				showInfoDialogFragment();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuItem item = menu.findItem(R.id.infoMenuItem);
		if (item != null)
		{
			item.setVisible(showInfoMenuItem);
		}

		return true;
	}

	/**********************************************************************************************
	 * Bluetooth Receiver Methods
	 **********************************************************************************************/
	private void registerBluetoothReceiver()
	{
		if (RealCommApplication.getHasBluetoothLe() && !this.bluetoothReceiverIsRegistered)
		{
			final IntentFilter filter = new IntentFilter();
			filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			registerReceiver(this.bluetoothReceiver, filter);

			this.bluetoothReceiverIsRegistered = true;
		}
	}

	private void unregisterBluetoothReceiver()
	{
		if (RealCommApplication.getHasBluetoothLe() && this.bluetoothReceiverIsRegistered)
		{
			unregisterReceiver(this.bluetoothReceiver);

			this.bluetoothReceiverIsRegistered = false;
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
	public void setInfoMenuItemVisibility(boolean visible)
	{
		this.showInfoMenuItem = visible;
		invalidateOptionsMenu();
	}

	@Override
	public void showSplashScreen()
	{
		changePage(RealcommPage.SPLASH_SCREEN);
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
				.commitAllowingStateLoss();
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
		if (splashScreenFragment != null && splashScreenFragment.getIsVisible() && dashboardTabletFragment != null)
		{
			// Out
			splashScreenFragment.setOutAnimationDuration(getResources().getInteger(R.integer.splashFragmentToDashboardFragment));
			splashScreenFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			// In
			dashboardTabletFragment.setInAnimationDuration(getResources().getInteger(R.integer.splashFragmentToDashboardFragment));
			dashboardTabletFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);
			dashboardTabletFragment.setInAnimationCompleteListener(this.showMenuItemRunnable);

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
		if (splashScreenFragment != null && splashScreenFragment.getIsVisible() && dashboardPhoneFragment != null)
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
		if (dashboardTabletFragment != null && dashboardTabletFragment.getIsVisible() && profilePageFragment != null)
		{
			profilePageFragment.updateProfilePage(this.selectedBooth);

			// Out
			dashboardTabletFragment.setOutAnimationDuration(getResources().getInteger(R.integer.dashboardFragmentToProfileFragment));
			dashboardTabletFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATE_DECELERATE);

			// In
			profilePageFragment.setInAnimationDuration(getResources().getInteger(R.integer.dashboardFragmentToProfileFragment));
			profilePageFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATE_DECELERATE);

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
		if (dashboardPhoneFragment != null && dashboardPhoneFragment.getIsVisible() && profilePageFragment != null)
		{
			profilePageFragment.updateProfilePage(this.selectedBooth);

			// Out
			dashboardPhoneFragment.setOutAnimationDuration(getResources().getInteger(R.integer.dashboardFragmentToProfileFragment));
			dashboardPhoneFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATE_DECELERATE);

			// In
			profilePageFragment.setInAnimationDuration(getResources().getInteger(R.integer.dashboardFragmentToProfileFragment));
			profilePageFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATE_DECELERATE);

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
		if (profilePageFragment != null && profilePageFragment.getIsVisible() && dashboardTabletFragment != null)
		{
			// Out
			profilePageFragment.setOutAnimationDuration(getResources().getInteger(R.integer.profileFragmentToDashboardFragment));
			profilePageFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATE_DECELERATE);

			// In
			dashboardTabletFragment.setInAnimationDuration(getResources().getInteger(R.integer.profileFragmentToDashboardFragment));
			dashboardTabletFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATE_DECELERATE);
			dashboardTabletFragment.setInAnimationCompleteListener(this.showMenuItemRunnable);

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
		if (profilePageFragment != null && profilePageFragment.getIsVisible() && dashboardPhoneFragment != null)
		{
			// Out
			dashboardPhoneFragment.setInAnimationDuration(getResources().getInteger(R.integer.profileFragmentToDashboardFragment));
			dashboardPhoneFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATE_DECELERATE);

			// In
			profilePageFragment.setOutAnimationDuration(getResources().getInteger(R.integer.profileFragmentToDashboardFragment));
			profilePageFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATE_DECELERATE);

			FragmentHelper.showAndHideFragments(
				getSupportFragmentManager(),
				dashboardPhoneFragment,
				profilePageFragment,
				R.id.slideInLeftAnimation,
				R.id.slideOutRightAnimation);
		}
	}

	@Override
	public void initBeaconManager()
	{
		if (RealCommApplication.getHasBluetoothLe())
		{
			beaconManager = IBeaconManager.getInstanceForApplication(this);
			beaconManager.setRangeNotifier(this);
		}
	}

	@Override
	public void bindBeaconManager()
	{
		if (RealCommApplication.getHasBluetoothLe() &&
			RealCommApplication.isBluetoothEnabled() &&
			this.beaconManager != null &&
			!this.beaconManager.isBound(this))
		{
			this.beaconManager.bind(this);
		}
	}

	@Override
	public void unbindBeaconManager()
	{
		if (RealCommApplication.getHasBluetoothLe() &&
			this.beaconManager != null &&
			this.beaconManager.isBound(this))
		{
			this.beaconManager.unBind(this);
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

		ProfilePageFragment profilePageFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);
		if (profilePageFragment != null)
		{
			profilePageFragment.updateProfilePage(this.selectedBooth);
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
	public void onAppModeChanged(AppMode newAppMode, AppMode previousAppMode)
	{
		for (AppModeChangedCallbacks listener : this.appModeChangedListeners)
		{
			listener.onAppModeChanged(newAppMode, previousAppMode);
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
	 * App Mode Interface
	 **********************************************************************************************/
	@Override
	public void changeAppMode(AppMode newAppMode)
	{
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

	/**********************************************************************************************
	 * Beacon Consumer Callbacks
	 **********************************************************************************************/
	@Override
	public void onIBeaconServiceConnect()
	{
		try
		{
			this.beaconManager.startRangingBeaconsInRegion(ALL_BEACONS);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
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
				if (dataInterface != null)
				{
					dataInterface.updateAccuracy(beaconList);
				}
			}
		});
	}

	/**********************************************************************************************
	 * Booth Flipper Implements
	 **********************************************************************************************/
	@Override
	public void startViewTimer()
	{
		if (this.boothFlipperListener != null)
		{
			this.boothFlipperListener.startViewTimer();
		}
	}

	@Override
	public void stopViewTimer()
	{
		if (this.boothFlipperListener != null)
		{
			this.boothFlipperListener.stopViewTimer();
		}
	}

	/**********************************************************************************************
	 * Private helper methods
	 **********************************************************************************************/
	private void initManagers(Bundle savedInstanceState)
	{
		initPageManager(savedInstanceState);
		initBeaconSaverManager();
	}

	private void initPageManager(Bundle savedInstanceState)
	{
		RealcommPage startingPage = RealcommPage.INITIALIZING;

		if (savedInstanceState != null)
		{
			startingPage = (RealcommPage) savedInstanceState.getSerializable(CURRENT_PAGE_KEY);
		}

		this.pageManager = new RealcommPageManager(startingPage, this);
	}

	private void initBeaconSaverManager()
	{
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

	private void startWebService()
	{
		Intent serviceIntent = new Intent(this, WebService.class);
		serviceIntent.setAction(WebService.DOWNLOAD_DATABASE_ACTION);
		startService(serviceIntent);
	}

	// TODO check this is actually the case, before slagging off the library...
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
		changePage(RealcommPage.DASHBOARD_PAGE);
	}

	private void showInfoDialogFragment()
	{
		InfoDialogFragment infoDialogFragment = new InfoDialogFragment();
		infoDialogFragment.show(getSupportFragmentManager(), InfoDialogFragment.TAG);
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
					case BluetoothAdapter.STATE_TURNING_ON:
						break;
					case BluetoothAdapter.STATE_ON:
						changeAppMode(AppMode.ONLINE);
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						changeAppMode(AppMode.OFFLINE);
						break;
					case BluetoothAdapter.STATE_OFF:
						break;
					case BluetoothAdapter.ERROR:
						changeAppMode(AppMode.OFFLINE);
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

	private Runnable showMenuItemRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			setInfoMenuItemVisibility(true);
		}
	};
}

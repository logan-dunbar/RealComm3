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
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.openbox.realcomm3.utilities.helpers.LogHelper;
import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseActivity;
import com.openbox.realcomm3.database.models.BoothModel;
import com.openbox.realcomm3.database.models.SelectedBoothModel;
import com.openbox.realcomm3.fragments.DataFragment;
import com.openbox.realcomm3.fragments.ListingPageFragment;
import com.openbox.realcomm3.fragments.ProfilePageFragment;
import com.openbox.realcomm3.fragments.SplashScreenFragment;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.enums.BeaconStatus;
import com.openbox.realcomm3.utilities.enums.BoothSortMode;
import com.openbox.realcomm3.utilities.enums.ProximityRegion;
import com.openbox.realcomm3.utilities.enums.RealcommPage;
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
	RangeNotifier
{
	private static final String APP_MODE_KEY = "appModeKey";
	private static final String APP_MODE_SELECTOR_KEY = "appModeSelectorKey";
	private static final String BEACON_STATUS_KEY = "beaconStatusKey";
	private static final String CURRENT_PAGE_KEY = "currentPageKey";

	private static final int ENABLE_BLUETOOTH_REQUEST = 1;

	private static final Region ALL_BEACONS = new Region("regionId", null, null, null);

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	// Page manager
	private RealcommPageManager pageManager;

	// App Mode
	private AppModeManager appModeManager;

	// Beacon Manager
	private IBeaconManager beaconManager;

	// Views
	private ImageView clearRealcommBackground;
	private ImageView blurRealCommBackground;

	// Data listeners
	private List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();
	private DataInterface dataInterface;
	private List<ClearFocusInterface> clearFocusListeners = new ArrayList<>();
	private List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();
	private BeaconManagerBoundCallbacks beaconManagerBoundListener;

	private SelectedBoothModel selectedBooth;

	/**********************************************************************************************
	 * Activity Lifecycle Implements
	 **********************************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null); // Dark background not needed anymore
		setContentView(R.layout.activity_realcomm);

		getWindow().getDecorView().getRootView().setOnTouchListener(new ClearFocusTouchListener(this, this));

		this.clearRealcommBackground = (ImageView) findViewById(R.id.clearRealcommBackgroundImageView);
		this.blurRealCommBackground = (ImageView) findViewById(R.id.blurRealcommBackgroundImageView);

		// Initialize the page manager
		initStateManagers(savedInstanceState);

		// Initialize the Data Fragment
		createDataFragment();

		// Show SplashScreen if required
		if (this.pageManager.getCurrentPage() == RealcommPage.INITIALIZING)
		{
			this.pageManager.changePage(RealcommPage.SPLASHSCREEN);
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
		outState.putSerializable(CURRENT_PAGE_KEY, this.pageManager.getCurrentPage());
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
		this.appModeManager.changeAppMode(AppMode.PAUSED);
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
		if (this.pageManager.getCurrentPage() == RealcommPage.PROFILEPAGE)
		{
			this.changePage(RealcommPage.LISTINGPAGE);
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
				this.appModeManager.changeAppMode(AppMode.ONLINE);
			}
			else
			{
				// TODO: update wording
				ToastHelper.showLongMessage(this, "Enable Bluetooth for Proximity Scanning");
				this.appModeManager.changeAppMode(AppMode.OFFLINE);
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
	public void changePage(RealcommPage page)
	{
		this.pageManager.changePage(page);
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
			fragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.id.fadeInAnimation, -1)
				.add(R.id.realcommFragmentContainer, fragment, SplashScreenFragment.TAG)
				.commit();
		}
	}

	@Override
	public void hideSplashScreenFragmentAndShowListingPageFragment()
	{
		SplashScreenFragment splashFragment = (SplashScreenFragment) getSupportFragmentManager().findFragmentByTag(SplashScreenFragment.TAG);
		ListingPageFragment listingFragment = (ListingPageFragment) getSupportFragmentManager().findFragmentByTag(ListingPageFragment.TAG);

		// TODO check this if (do a quick change to and from listing page, might need to executePendingTransactions())
		if (splashFragment != null && listingFragment == null)
		{
			splashFragment.setOutAnimationDuration(getResources().getInteger(R.integer.splashFragmentToListingFragment));
			splashFragment.setOutAnimationInterpolator(AnimationInterpolator.LINEAR);

			listingFragment = ListingPageFragment.newInstance();

			listingFragment.setInAnimationDuration(getResources().getInteger(R.integer.splashFragmentToListingFragment));
			listingFragment.setInAnimationInterpolator(AnimationInterpolator.LINEAR);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.id.slideUpInAnimation, R.id.slideUpOutAnimation)
				.remove(splashFragment)
				.add(R.id.realcommFragmentContainer, listingFragment, ListingPageFragment.TAG)
				.commit();
		}
	}

	@Override
	public void showListingPageFragmentAndRemoveProfileFragment()
	{
		ListingPageFragment listingFragment = (ListingPageFragment) getSupportFragmentManager().findFragmentByTag(ListingPageFragment.TAG);
		ProfilePageFragment profileFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);

		// TODO check this if (do a quick change to and from listing page, might need to executePendingTransactions())
		if (listingFragment != null && profileFragment != null)
		{
			int duration = getResources().getInteger(R.integer.profileFragmentToListingFragment);

			listingFragment.setInAnimationDuration(duration);
			listingFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			profileFragment.setOutAnimationDuration(duration);
			profileFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.id.slideInLeftAnimation, R.id.slideOutRightAnimation, R.id.slideInRightAnimation, R.id.slideOutLeftAnimation)
				.remove(profileFragment)
				.show(listingFragment)
				.commit();
		}
	}

	@Override
	public void addProfilePageAndHideListingPage()
	{
		ListingPageFragment listingFragment = (ListingPageFragment) getSupportFragmentManager().findFragmentByTag(ListingPageFragment.TAG);

		// TODO might not need this
		ProfilePageFragment profileFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);

		// TODO check this if (do a quick change to and from listing page, might need to executePendingTransactions())
		if (listingFragment != null && profileFragment == null)
		{
			int duration = getResources().getInteger(R.integer.listingFragmentToProfileFragment);

			listingFragment.setOutAnimationDuration(duration);
			listingFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			// TODO see what happens if you replace a fragment that is still on the page O.o
			profileFragment = ProfilePageFragment.newInstance(this.selectedBooth);

			profileFragment.setInAnimationDuration(duration);
			profileFragment.setInAnimationInterpolator(AnimationInterpolator.ACCELERATEDECELERATE);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.id.slideInRightAnimation, R.id.slideOutLeftAnimation, R.id.slideInLeftAnimation, R.id.slideOutRightAnimation)
				.hide(listingFragment)
				.add(R.id.realcommFragmentContainer, profileFragment, ProfilePageFragment.TAG)
				.commit();
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
		this.pageManager.changePage(RealcommPage.LISTINGPAGE);
	}

	@Override
	public void onSplashScreenAnimationOutComplete()
	{
		// TODO Might not need
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

		if (savedInstanceState != null)
		{
			startingBeaconStatus = (BeaconStatus) savedInstanceState.getSerializable(BEACON_STATUS_KEY);
			startingAppMode = (AppMode) savedInstanceState.getSerializable(APP_MODE_KEY);
			startingAppModeSelector = (AppMode) savedInstanceState.getSerializable(APP_MODE_SELECTOR_KEY);
			startingPage = (RealcommPage) savedInstanceState.getSerializable(CURRENT_PAGE_KEY);
		}

		BeaconStatusManager beaconStatusManager = new BeaconStatusManager(this, startingBeaconStatus, RealCommApplication.getHasBluetoothLe());
		this.appModeManager = new AppModeManager(startingAppMode, startingAppModeSelector, beaconStatusManager, this);
		this.pageManager = new RealcommPageManager(startingPage, this);
	}

	private void createDataFragment()
	{
		DataFragment fragment = (DataFragment) getSupportFragmentManager().findFragmentByTag(DataFragment.TAG);
		if (fragment == null)
		{
			getSupportFragmentManager().beginTransaction().add(DataFragment.newInstance(), DataFragment.TAG).commit();
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

	private void determineAppMode()
	{
		if (this.appModeManager.getCurrentAppModeSelector() == AppMode.ONLINE)
		{
			if (RealCommApplication.getHasBluetoothLe())
			{
				if (RealCommApplication.isBluetoothEnabled(this))
				{
					this.appModeManager.changeAppMode(AppMode.ONLINE);
				}
				else
				{
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST);
				}
			}
			else
			{
				this.appModeManager.changeAppMode(AppMode.OFFLINE);
			}
		}
		else
		{
			this.appModeManager.changeAppMode(AppMode.OFFLINE);
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
						RealCommActivity.this.appModeManager.changeAppMode(AppMode.OFFLINE);
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						// ToastHelper.showShortMessage(ListingPageActivity.this, "Bluetooth turning off!");
						break;
					case BluetoothAdapter.STATE_ON:
						ToastHelper.showLongMessage(RealCommActivity.this, "Online mode engaged!");
						RealCommActivity.this.appModeManager.changeAppMode(AppMode.ONLINE);
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						// ToastHelper.showShortMessage(ListingPageActivity.this, "Bluetooth turning on!");
						break;
				}
			}
		}
	};
}

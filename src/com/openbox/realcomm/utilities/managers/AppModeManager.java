package com.openbox.realcomm.utilities.managers;

import com.openbox.realcomm.utilities.enums.AppMode;
import com.openbox.realcomm.utilities.interfaces.ActivityInterface;
import com.openbox.realcomm.utilities.interfaces.AppModeChangedCallbacks;

public class AppModeManager
{
	private AppMode currentAppMode;
	private AppMode previousAppMode;

	private ActivityInterface activityInterface;
	private AppModeChangedCallbacks appModeChangedListener;

	public AppMode getCurrentAppMode()
	{
		return this.currentAppMode;
	}

	public AppModeManager(ActivityInterface activityInterface, AppModeChangedCallbacks appModeChangedListener)
	{
		this.currentAppMode = this.previousAppMode = AppMode.INITIALIZING;

		this.activityInterface = activityInterface;
		this.appModeChangedListener = appModeChangedListener;
	}

	public void changeAppMode(AppMode newAppMode)
	{
		this.previousAppMode = this.currentAppMode;
		doAppModeChange(newAppMode);
	}

	private void doAppModeChange(AppMode newAppMode)
	{
		switch (newAppMode)
		{
			case INITIALIZING:
				// Should not get here
				break;
			case OFFLINE:
				changeAppModeOffline(newAppMode);
				break;
			case ONLINE:
				changeAppModeOnline(newAppMode);
				break;
			case OUT_OF_RANGE:
				changeAppModeOutOfRange(newAppMode);
				break;
			default:
				break;
		}
	}

	private void changeAppModeOffline(AppMode newAppMode)
	{
		switch (this.currentAppMode)
		{
			case INITIALIZING:
				changeInitializingToOffline();
				doAppModeChange(newAppMode);
				break;
			case OFFLINE:
				// Stay here
				break;
			case ONLINE:
				changeOnlineToOffline();
				doAppModeChange(newAppMode);
				break;
			case OUT_OF_RANGE:
				changeOutOfRangeToOffline();
				doAppModeChange(newAppMode);
				break;
			default:
				break;
		}
	}

	private void changeAppModeOnline(AppMode newAppMode)
	{
		switch (this.currentAppMode)
		{
			case INITIALIZING:
				changeInitializingToOnline();
				doAppModeChange(newAppMode);
				break;
			case OFFLINE:
				changeOfflineToOnline();
				doAppModeChange(newAppMode);
				break;
			case ONLINE:
				// Stay here
				break;
			case OUT_OF_RANGE:
				changeOutOfRangeToOnline();
				doAppModeChange(newAppMode);
				break;
			default:
				break;
		}
	}

	private void changeAppModeOutOfRange(AppMode newAppMode)
	{
		switch (this.currentAppMode)
		{
			case INITIALIZING:
				// Can't do this
				break;
			case OFFLINE:
				// Can't do this
				break;
			case ONLINE:
				changeOnlineToOutOfRange();
				doAppModeChange(newAppMode);
				break;
			case OUT_OF_RANGE:
				// Stay here
				break;
			default:
				break;
		}
	}

	private void changeInitializingToOffline()
	{
		this.activityInterface.initBeaconManager();
		this.activityInterface.showSplashScreen();

		updateAppMode(AppMode.OFFLINE);
	}

	private void changeOnlineToOffline()
	{
		this.activityInterface.unbindBeaconManager();

		updateAppMode(AppMode.OFFLINE);
	}

	private void changeOutOfRangeToOffline()
	{
		this.activityInterface.unbindBeaconManager();

		updateAppMode(AppMode.OFFLINE);
	}

	private void changeInitializingToOnline()
	{
		this.activityInterface.initBeaconManager();
		this.activityInterface.bindBeaconManager();
		this.activityInterface.showSplashScreen();

		updateAppMode(AppMode.ONLINE);
	}

	private void changeOfflineToOnline()
	{
		this.activityInterface.bindBeaconManager();

		updateAppMode(AppMode.ONLINE);
	}

	private void changeOutOfRangeToOnline()
	{
		// Nothing to do, the firing of onAppModeChanged will handle the rest
		updateAppMode(AppMode.ONLINE);
	}

	private void changeOnlineToOutOfRange()
	{
		// Nothing to do, the firing of onAppModeChanged will handle the rest
		updateAppMode(AppMode.OUT_OF_RANGE);
	}

	private void updateAppMode(AppMode newAppMode)
	{
		this.currentAppMode = newAppMode;

		if (this.appModeChangedListener != null)
		{
			this.appModeChangedListener.onAppModeChanged(newAppMode, this.previousAppMode);
		}
	}
}

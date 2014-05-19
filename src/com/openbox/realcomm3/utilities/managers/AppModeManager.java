package com.openbox.realcomm3.utilities.managers;

import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.enums.BeaconStatus;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.AppModeOfflineInterface;
import com.openbox.realcomm3.utilities.interfaces.BoothFlipperInterface;

public class AppModeManager
{
	private AppMode currentAppMode;
	private AppMode previousAppMode;
	private AppMode currentAppModeSelector;
	private BeaconStatusManager beaconStatusManager;
	private AppModeChangedCallbacks appModeChangedListener;
	private BoothFlipperInterface boothFlipperListener;

	public AppMode getCurrentAppMode()
	{
		return this.currentAppMode;
	}

	public AppMode getPreviousAppMode()
	{
		return previousAppMode;
	}

	public void setPreviousAppMode(AppMode previousAppMode)
	{
		this.previousAppMode = previousAppMode;
	}

	public void setCurrentAppMode(AppMode currentAppMode)
	{
		this.currentAppMode = currentAppMode;
	}

	public AppMode getCurrentAppModeSelector()
	{
		return this.currentAppModeSelector;
	}

	public void setCurrentAppModeSelector(AppMode currentAppModeSelector)
	{
		this.currentAppModeSelector = currentAppModeSelector;
	}

	public BeaconStatus getCurrentBeaconStatus()
	{
		return this.beaconStatusManager.getCurrentBeaconStatus();
	}

	public void setCurrentBeaconStatus(BeaconStatus currentBeaconStatus)
	{
		this.beaconStatusManager.setCurrentBeaconStatus(currentBeaconStatus);
	}

	public AppModeManager(
		AppMode startingAppMode,
		AppMode startingAppModeSelector,
		BeaconStatusManager beaconStatusManager,
		AppModeChangedCallbacks appModeListener,
		BoothFlipperInterface boothFlipperListener)
	{
		this.currentAppMode = startingAppMode;
		this.currentAppModeSelector = startingAppModeSelector;
		this.beaconStatusManager = beaconStatusManager;
		this.appModeChangedListener = appModeListener;
		this.boothFlipperListener = boothFlipperListener;
	}

	public void unbindBeaconManager()
	{
		this.beaconStatusManager.changeBeaconStatus(BeaconStatus.UNBOUND);
	}

	public void changeAppMode(AppMode newAppMode)
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
			case OUTOFRANGE:
				changeAppModeOutOfRange(newAppMode);
				break;
			case PAUSED:
				changeAppModePaused(newAppMode);
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
				changeAppMode(newAppMode);
				break;
			case OFFLINE:
				// Stay here
				break;
			case ONLINE:
				changeOnlineToOffline();
				changeAppMode(newAppMode);
				break;
			case OUTOFRANGE:
				changeOutOfRangeToOnline();
				changeAppMode(newAppMode);
				break;
			case PAUSED:
				changePausedToOffline();
				changeAppMode(newAppMode);
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
				changeAppMode(newAppMode);
				break;
			case OFFLINE:
				changeOfflineToOnline();
				changeAppMode(newAppMode);
				break;
			case ONLINE:
				// Stay here
				break;
			case OUTOFRANGE:
				changeOutOfRangeToOnline();
				changeAppMode(newAppMode);
				break;
			case PAUSED:
				changePausedToOnline();
				changeAppMode(newAppMode);
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
				changeAppMode(newAppMode);
				break;
			case OUTOFRANGE:
				// Stay here
				break;
			case PAUSED:
				changePausedToOnline();
				changeAppMode(newAppMode);
				break;
			default:
				break;
		}
	}

	private void changeAppModePaused(AppMode newAppMode)
	{
		switch (this.currentAppMode)
		{
			case INITIALIZING:
				// Can't do this
				break;
			case OFFLINE:
				changeOfflineToPaused();
				changeAppMode(newAppMode);
				break;
			case ONLINE:
				changeOnlineToPaused();
				changeAppMode(newAppMode);
				break;
			case OUTOFRANGE:
				changeOutOfRangeToPaused();
				changeAppMode(newAppMode);
				break;
			case PAUSED:
				// Stay here
				break;
			default:
				break;
		}
	}

	private void changeInitializingToOffline()
	{
		this.beaconStatusManager.changeBeaconStatus(BeaconStatus.UNBOUND);
		updateAppMode(AppMode.OFFLINE, true);
	}

	private void changeInitializingToOnline()
	{
		this.beaconStatusManager.changeBeaconStatus(BeaconStatus.RANGING);
		updateAppMode(AppMode.ONLINE, true);
	}

	private void changeOfflineToOnline()
	{
		this.beaconStatusManager.changeBeaconStatus(BeaconStatus.RANGING);
		updateAppMode(AppMode.ONLINE, true);
	}

	private void changeOnlineToOffline()
	{
		this.beaconStatusManager.changeBeaconStatus(BeaconStatus.UNBOUND);
		updateAppMode(AppMode.OFFLINE, true);
		resetBoothDistances();
	}

	private void changeOfflineToPaused()
	{
		this.beaconStatusManager.changeBeaconStatus(BeaconStatus.BOUND);
		updateAppMode(AppMode.PAUSED, false);
	}

	private void changePausedToOffline()
	{
		this.beaconStatusManager.changeBeaconStatus(BeaconStatus.UNBOUND);
		updateAppMode(AppMode.OFFLINE, true);
		resetBoothDistances();
	}

	private void changeOnlineToPaused()
	{
		this.beaconStatusManager.changeBeaconStatus(BeaconStatus.BOUND);
		updateAppMode(AppMode.PAUSED, false);
	}

	private void changePausedToOnline()
	{
		this.beaconStatusManager.changeBeaconStatus(BeaconStatus.RANGING);
		updateAppMode(AppMode.ONLINE, true);
	}

	private void changeOnlineToOutOfRange()
	{
		updateAppMode(AppMode.OUTOFRANGE, false);
		this.boothFlipperListener.resetTimer();
	}

	private void changeOutOfRangeToOnline()
	{
		updateAppMode(AppMode.ONLINE, true);
		this.boothFlipperListener.resetTimer();
	}

	private void changeOutOfRangeToPaused()
	{
		// TODO maybe?
		updateAppMode(AppMode.PAUSED, false);
	}

	private void updateAppMode(AppMode appMode, Boolean updateAppModeSelector)
	{
		this.currentAppMode = appMode;

		if (updateAppModeSelector)
		{
			this.currentAppModeSelector = appMode;
		}

		if (this.appModeChangedListener != null)
		{
			this.appModeChangedListener.onAppModeChanged();
		}
	}

	private void resetBoothDistances()
	{
		// Doesn't happen from Initializing -> Offline (no need)
		if (this.appModeChangedListener != null)
		{
			this.appModeChangedListener.onOnlineModeToOfflineMode();
		}
	}

	// private void template()
	// {
	// switch (this.currentAppMode)
	// {
	// case Initializing:
	// break;
	// case Offline:
	// break;
	// case Online:
	// break;
	// case OutOfRange:
	// break;
	// case Paused:
	// break;
	// default:
	// break;
	// }
	// }
}

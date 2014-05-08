package com.openbox.realcomm3.utilities.interfaces;

public interface BeaconStatusChangeCallbacks
{
	void initializeBeaconManager();

	void bindBeaconManager();

	void bindBeaconManagerAndRange(BeaconManagerBoundCallbacks listener);

	void unbindBeaconManager();

	void startRangingBeacons();

	void stopRangingBeacons();
}

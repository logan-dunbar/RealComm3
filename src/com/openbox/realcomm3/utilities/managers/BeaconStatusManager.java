package com.openbox.realcomm3.utilities.managers;

import com.openbox.realcomm3.utilities.enums.BeaconStatus;
import com.openbox.realcomm3.utilities.interfaces.BeaconManagerBoundCallbacks;
import com.openbox.realcomm3.utilities.interfaces.BeaconStatusChangeCallbacks;

public class BeaconStatusManager implements BeaconManagerBoundCallbacks
{
	private BeaconStatusChangeCallbacks listener;

	private BeaconStatus currentBeaconStatus;
	private BeaconStatus tempNewBeaconStatus;
	private Boolean hasBluetoothLe;

	public BeaconStatus getCurrentBeaconStatus()
	{
		return this.currentBeaconStatus;
	}

	public void setCurrentBeaconStatus(BeaconStatus currentBeaconStatus)
	{
		this.currentBeaconStatus = currentBeaconStatus;
	}

	public BeaconStatusManager(BeaconStatusChangeCallbacks listener, BeaconStatus startingBeaconStatus, Boolean hasBluetoothLe)
	{
		this.listener = listener;
		this.currentBeaconStatus = startingBeaconStatus;
		this.hasBluetoothLe = hasBluetoothLe;
	}

	public void changeBeaconStatus(BeaconStatus newBeaconStatus)
	{
		if (!this.hasBluetoothLe)
		{
			// No beacons needed if no BLE
			return;
		}

		switch (newBeaconStatus)
		{
			case NONEXISTENT:
				// Should not get here
				break;
			case UNBOUND:
				changeBeaconStatusUnbound(newBeaconStatus);
				break;
			case BOUND:
				changeBeaconStatusBound(newBeaconStatus);
				break;
			case RANGING:
				changeBeaconStatusRanging(newBeaconStatus);
				break;
			default:
				break;
		}
	}

	private void changeBeaconStatusUnbound(BeaconStatus newBeaconStatus)
	{
		switch (this.currentBeaconStatus)
		{
			case NONEXISTENT:
				changeNonExistentToUnbound();
				changeBeaconStatus(newBeaconStatus);
				break;
			case UNBOUND:
				// Stay here
				break;
			case BOUND:
				changeBoundToUnbound();
				changeBeaconStatus(newBeaconStatus);
				break;
			case RANGING:
				changeRangingToBound();
				changeBeaconStatus(newBeaconStatus);
				break;
			default:
				break;
		}
	}

	private void changeBeaconStatusBound(BeaconStatus newBeaconStatus)
	{
		switch (this.currentBeaconStatus)
		{
			case NONEXISTENT:
				changeNonExistentToUnbound();
				changeBeaconStatus(newBeaconStatus);
				break;
			case UNBOUND:
				changeUnboundToBound();
				changeBeaconStatus(newBeaconStatus);
				break;
			case BOUND:
				// Stay here
				break;
			case RANGING:
				changeRangingToBound();
				changeBeaconStatus(newBeaconStatus);
				break;
			default:
				break;
		}
	}

	private void changeBeaconStatusRanging(BeaconStatus newBeaconStatus)
	{
		switch (this.currentBeaconStatus)
		{
			case NONEXISTENT:
				changeNonExistentToUnbound();
				changeBeaconStatus(newBeaconStatus);
				break;
			case UNBOUND:
				changeUnboundToBoundAndRange();
				this.tempNewBeaconStatus = newBeaconStatus;
				break;
			case BOUND:
				changeBoundToRanging();
				changeBeaconStatus(newBeaconStatus);
				break;
			case RANGING:
				// Stay here
				break;
			default:
				break;
		}
	}

	private void changeNonExistentToUnbound()
	{
		this.listener.initializeBeaconManager();
		this.currentBeaconStatus = BeaconStatus.UNBOUND;
	}

	private void changeUnboundToBound()
	{
		this.listener.bindBeaconManager();
		this.currentBeaconStatus = BeaconStatus.BOUND;
	}

	private void changeUnboundToBoundAndRange()
	{
		this.listener.bindBeaconManagerAndRange(this);
		this.currentBeaconStatus = BeaconStatus.BOUND;
	}

	@Override
	public void onBeaconManagerBound()
	{
		changeBeaconStatus(this.tempNewBeaconStatus);
		this.tempNewBeaconStatus = null;
	}

	private void changeBoundToUnbound()
	{
		this.listener.unbindBeaconManager();
		this.currentBeaconStatus = BeaconStatus.UNBOUND;
	}

	private void changeBoundToRanging()
	{
		this.listener.startRangingBeacons();
		this.currentBeaconStatus = BeaconStatus.RANGING;
	}

	private void changeRangingToBound()
	{
		this.listener.stopRangingBeacons();
		this.currentBeaconStatus = BeaconStatus.BOUND;
	}
	// private void template()
	// {
	// switch (this.currentBeaconStatus)
	// {
	// case NonExistent:
	// break;
	// case Unbound:
	// break;
	// case Bound:
	// break;
	// case Ranging:
	// break;
	// default:
	// break;
	// }
	// }
}

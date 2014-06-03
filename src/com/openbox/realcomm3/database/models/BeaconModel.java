package com.openbox.realcomm3.database.models;

import java.util.Date;

import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.database.objects.Beacon;

public class BeaconModel
{
	private int boothId;
	private double accuracy;
	private double rssi;
	private double weightedAccuracy;
	private String deviceId;

	private int count;
	private long startTime;
	private long latestTime;

	public BeaconModel(BoothModel boothModel)
	{
		this.count = 1;
		this.startTime = this.latestTime = new Date().getTime();

		this.boothId = boothModel.getBoothId();
		// TODO need to expose the pre-weighted accuracy
		this.accuracy = boothModel.getAccuracy();
		this.rssi = boothModel.getRssi();
		this.weightedAccuracy = boothModel.getAccuracy();
		this.deviceId = RealCommApplication.getDeviceId();
	}

	public BeaconModel(Beacon beacon)
	{
		this.count = 1;
		this.startTime = this.latestTime = new Date().getTime();

		this.boothId = beacon.getBoothId();
		// TODO need to expose the pre-weighted accuracy
		this.accuracy = beacon.getAccuracy();
		this.rssi = beacon.getRssi();
		this.weightedAccuracy = beacon.getAccuracy();
		this.deviceId = RealCommApplication.getDeviceId();
	}

	public void updateBeacon(BoothModel boothModel)
	{
		count++;
		this.latestTime = new Date().getTime();

		this.rssi += boothModel.getRssi();
		this.accuracy += boothModel.getAccuracy();
		this.weightedAccuracy += boothModel.getAccuracy();
	}

	public void updateBeacon(BeaconModel beaconModel)
	{
		count++;
		this.latestTime = new Date().getTime();

		this.rssi += beaconModel.getRssi();
		this.accuracy += beaconModel.getAccuracy();
		this.weightedAccuracy += beaconModel.getAccuracy();
	}

	public int getBoothId()
	{
		return boothId;
	}

	public double getAccuracy()
	{
		return this.accuracy / count;
	}

	public double getRssi()
	{
		return this.rssi / count;
	}

	public double getWeightedAccuracy()
	{
		return this.weightedAccuracy / count;
	}

	public long getRangedDate()
	{
		// TODO check if this is necessary
		return this.startTime + (long) ((this.latestTime - this.startTime) / 2.0);
	}

	public String getDeviceId()
	{
		return deviceId;
	}
}

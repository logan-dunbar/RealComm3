package com.openbox.realcomm3.database.models;

import java.util.Date;

import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.database.objects.Beacon;

public class BeaconModel
{
	private int boothId;
	private double accuracy;
	private double weightedAccuracy;
	private double rssi;
	private String deviceId;

	private int count;
	private long startTime;
	private long latestTime;

	public BeaconModel(BoothModel boothModel)
	{
		this.count = 1;
		this.startTime = this.latestTime = new Date().getTime();

		this.boothId = boothModel.getBoothId();
		this.accuracy = boothModel.getAccuracy();
		this.weightedAccuracy = boothModel.getWeightedAccuracy();
		this.rssi = boothModel.getRssi();
		this.deviceId = RealCommApplication.getDeviceId();
	}

	public BeaconModel(Beacon beacon)
	{
		this.count = 1;
		this.startTime = this.latestTime = new Date().getTime();

		this.boothId = beacon.getBoothId();
		this.accuracy = beacon.getAccuracy();
		this.weightedAccuracy = beacon.getWeightedAccuracy();
		this.rssi = beacon.getRssi();
		this.deviceId = RealCommApplication.getDeviceId();
	}

	public void updateBeacon(BoothModel boothModel)
	{
		count++;
		this.latestTime = new Date().getTime();

		this.accuracy += boothModel.getAccuracy();
		this.weightedAccuracy += boothModel.getWeightedAccuracy();
		this.rssi += boothModel.getRssi();
	}

	public void updateBeacon(BeaconModel beaconModel)
	{
		count++;
		this.latestTime = new Date().getTime();

		this.accuracy += beaconModel.getAccuracy();
		this.weightedAccuracy += beaconModel.getWeightedAccuracy();
		this.rssi += beaconModel.getRssi();
	}

	public int getBoothId()
	{
		return boothId;
	}

	public double getAccuracy()
	{
		return this.accuracy / count;
	}

	public double getWeightedAccuracy()
	{
		return this.weightedAccuracy / count;
	}

	public double getRssi()
	{
		return this.rssi / count;
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

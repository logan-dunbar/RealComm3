package com.openbox.realcomm3.database.objects;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.openbox.realcomm3.database.models.BeaconModel;

@DatabaseTable(tableName = "Beacon")
public class Beacon implements Serializable
{
	// Generated Serializable UID
	private static final long serialVersionUID = 1249730497413717759L;

	public static final Class<?> ID_CLASS = Integer.class;

	/**********************************************************************************************
	 * Column Names
	 **********************************************************************************************/
	public static final String BEACON_ID_COLUMN_NAME = "BeaconId";
	public static final String ACCURACY_COLUMN_NAME = "Accuracy";
	public static final String RSSI_COLUMN_NAME = "Rssi";
	public static final String WEIGHTED_ACCURACY_COLUMN_NAME = "WeightedAccuracy";
	public static final String RANGED_DATE_COLUMN_NAME = "RangedDate";

	public static final String DEVICE_ID_COLUMN_NAME = "DeviceId";
	public static final String IS_ANDROID_COLUMN_NAME = "IsAndroid";

	public static final String BOOTH_ID_JSON_NAME = "bid";
	public static final String ACCURACY_JSON_NAME = "a";
	public static final String RSSI_JSON_NAME = "r";
	public static final String WEIGHTED_ACCURACY_JSON_NAME = "wa";
	public static final String RANGED_DATE_JSON_NAME = "rd";
	public static final String DEVICE_ID_JSON_NAME = "did";
	public static final String IS_ANDROID_JSON_NAME = "ia";

	/**********************************************************************************************
	 * Database Fields
	 **********************************************************************************************/
	@DatabaseField(generatedId = true, columnName = BEACON_ID_COLUMN_NAME)
	@SerializedName(BEACON_ID_COLUMN_NAME)
	private int beaconId;

	@DatabaseField(columnName = Booth.BOOTH_ID_COLUMN_NAME)
	@SerializedName(BOOTH_ID_JSON_NAME)
	@Expose
	private int boothId;

	@DatabaseField(columnName = ACCURACY_COLUMN_NAME)
	@SerializedName(ACCURACY_JSON_NAME)
	@Expose
	private double accuracy;

	@DatabaseField(columnName = RSSI_COLUMN_NAME)
	@SerializedName(RSSI_JSON_NAME)
	@Expose
	private double rssi;

	@DatabaseField(columnName = WEIGHTED_ACCURACY_COLUMN_NAME)
	@SerializedName(WEIGHTED_ACCURACY_JSON_NAME)
	@Expose
	private double weightedAccuracy;

	@DatabaseField(columnName = RANGED_DATE_COLUMN_NAME)
	@SerializedName(RANGED_DATE_JSON_NAME)
	@Expose
	private Date rangedDate;

	@DatabaseField(columnName = DEVICE_ID_COLUMN_NAME)
	@SerializedName(DEVICE_ID_JSON_NAME)
	@Expose
	private String deviceId;

	@DatabaseField(columnName = IS_ANDROID_COLUMN_NAME)
	@SerializedName(IS_ANDROID_JSON_NAME)
	@Expose
	private int isAndroid;

	public Beacon()
	{
		// all persisted classes must define a no-arg constructor with at least package visibility
	}

	public Beacon(BeaconModel model)
	{
		this.boothId = model.getBoothId();
		this.accuracy = model.getAccuracy();
		this.rssi = model.getRssi();
		this.weightedAccuracy = model.getWeightedAccuracy();
		this.rangedDate = new Date(model.getRangedDate());
		this.deviceId = model.getDeviceId();
		this.isAndroid = 1;
	}

	/**********************************************************************************************
	 * Property Get and Setters
	 **********************************************************************************************/
	public int getBeaconId()
	{
		return beaconId;
	}

	public void setBeaconId(int beaconId)
	{
		this.beaconId = beaconId;
	}

	public int getBoothId()
	{
		return boothId;
	}

	public void setBoothId(int boothId)
	{
		this.boothId = boothId;
	}

	public double getAccuracy()
	{
		return accuracy;
	}

	public void setAccuracy(double accuracy)
	{
		this.accuracy = accuracy;
	}

	public double getRssi()
	{
		return rssi;
	}

	public void setRssi(double rssi)
	{
		this.rssi = rssi;
	}

	public double getWeightedAccuracy()
	{
		return weightedAccuracy;
	}

	public void setWeightedAccuracy(double weightedAccuracy)
	{
		this.weightedAccuracy = weightedAccuracy;
	}

	public Date getRangedDate()
	{
		return rangedDate;
	}

	public void setRangedDate(Date rangedDate)
	{
		this.rangedDate = rangedDate;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public int isAndroid()
	{
		return isAndroid;
	}

	public void setAndroid(int isAndroid)
	{
		this.isAndroid = isAndroid;
	}
}

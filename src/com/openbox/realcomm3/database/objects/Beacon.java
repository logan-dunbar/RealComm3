package com.openbox.realcomm3.database.objects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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
	public static final String WEIGHTED_ACCURACY_COLUMN_NAME = "WeightedAccuracy";
	public static final String RSSI_COLUMN_NAME = "Rssi";
	public static final String RANGED_DATE_COLUMN_NAME = "RangedDate";
	
	/**********************************************************************************************
	 * Database Fields
	 **********************************************************************************************/
	@DatabaseField(generatedId = true, columnName = BEACON_ID_COLUMN_NAME)
	@SerializedName(BEACON_ID_COLUMN_NAME)
	private int beaconId;
	
	@DatabaseField(generatedId = true, columnName = Booth.BOOTH_ID_COLUMN_NAME)
	@SerializedName(Booth.BOOTH_ID_COLUMN_NAME)
	private int boothId;
}

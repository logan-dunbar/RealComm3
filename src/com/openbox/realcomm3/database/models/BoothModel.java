package com.openbox.realcomm3.database.models;

import java.util.Comparator;
import java.util.LinkedList;

import android.content.res.Resources;

import com.openbox.realcomm3.database.objects.Booth;
import com.openbox.realcomm3.database.objects.Company;
import com.openbox.realcomm3.utilities.enums.ProximityRegion;
import com.radiusnetworks.ibeacon.IBeacon;

public class BoothModel
{
	private static final int WINDOW_LENGTH = 5;
	private static final int DEFAULT_RSSI = -120;
	private static final int DEFAULT_TX_POWER = -76;

	public static final double DEFAULT_ACCURACY = calculateAccuracy(DEFAULT_RSSI, DEFAULT_TX_POWER);

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	private LinkedList<Integer> rssiHistory = new LinkedList<Integer>();
	private int txPower;

	private Double accuracy = null;
	private int rssi;

	private int boothId;
	private String UUID;
	private int major;
	private int minor;
	private int boothNumber;

	private int companyId;
	private String companyName;
	private String companyDescription;
	private String companyShortDescription;
	private String conferenceName;

	/**********************************************************************************************
	 * Constructor
	 **********************************************************************************************/
	public BoothModel(Booth booth, Company company)
	{
		if (booth != null)
		{
			this.boothId = booth.getBoothId();
			this.UUID = booth.getUUID();
			this.major = booth.getMajor();
			this.minor = booth.getMinor();
			this.boothNumber = booth.getBoothNumber();
		}

		if (company != null)
		{
			this.companyId = company.getCompanyId();
			this.companyName = company.getName();
			this.companyDescription = company.getDescription();
			this.companyShortDescription = company.getShortDescription();
			this.conferenceName = company.getConferenceName();
		}
	}

	/**********************************************************************************************
	 * Public Methods
	 **********************************************************************************************/
	public Double getAccuracy()
	{
		if (accuracy == null)
		{
			updateAccuracyWithDefault();
		}

		return accuracy;
	}

	public void resetAccuracy()
	{
		this.rssiHistory.clear();
		updateAccuracyWithDefault();
	}

	public void updateAccuracyWithBeacon(IBeacon beacon)
	{
		this.rssiHistory.add(beacon.getRssi());
		this.rssi = beacon.getRssi();
		this.txPower = beacon.getTxPower();
		updateAccuracy();

	}

	public void updateAccuracyWithDefault()
	{
		this.rssiHistory.add(DEFAULT_RSSI);
		this.rssi = DEFAULT_RSSI;
		this.txPower = DEFAULT_TX_POWER;
		updateAccuracy();
	}

	public int getColor(Resources resources)
	{
		return resources.getColor(getProximityRegion(getAccuracy()).getColorId());
	}

	public static ProximityRegion getProximityRegion(double accuracy)
	{
		if (accuracy < ProximityRegion.IMMEDIATE.getProximityLimit())
		{
			return ProximityRegion.IMMEDIATE;
		}
		else if (accuracy < ProximityRegion.NEAR.getProximityLimit())
		{
			return ProximityRegion.NEAR;
		}
		else if (accuracy < ProximityRegion.FAR.getProximityLimit())
		{
			return ProximityRegion.FAR;
		}
		else
		{
			return ProximityRegion.OUTOFRANGE;
		}
	}

	public static Comparator<BoothModel> getAccuracyComparator()
	{
		return new Comparator<BoothModel>()
		{
			@Override
			public int compare(BoothModel lhs, BoothModel rhs)
			{
				double lhsAccuracy = lhs.getAccuracy();
				double rhsAccuracy = rhs.getAccuracy();
				if (lhsAccuracy < rhsAccuracy)
				{
					// 50 - 23
					return -1;
				}
				else if (lhsAccuracy > rhsAccuracy)
				{
					return 1;
				}
				else
				{
					return BoothModel.getCompanyNameComparator().compare(lhs, rhs);
				}
			}
		};
	}

	public static Comparator<BoothModel> getCompanyNameComparator()
	{
		return new Comparator<BoothModel>()
		{
			@Override
			public int compare(BoothModel lhs, BoothModel rhs)
			{
				return lhs.getCompanyName().compareToIgnoreCase(rhs.getCompanyName());
			}
		};
	}

	/**********************************************************************************************
	 * Private Methods
	 **********************************************************************************************/
	private void updateAccuracy()
	{
		if (this.rssiHistory.size() >= WINDOW_LENGTH)
		{
			this.rssiHistory.poll();
		}

		double runningAverageRssi = calculateRunningAverageRssi();
		this.accuracy = calculateAccuracy(runningAverageRssi, this.txPower);
	}

	private double calculateRunningAverageRssi()
	{
		if (this.rssiHistory.size() == 0)
		{
			return (double) DEFAULT_RSSI;
		}

		double runningAverageRssi = 0.0;
		for (int i = 0; i < this.rssiHistory.size(); i++)
		{
			runningAverageRssi += this.rssiHistory.get(i);
		}

		runningAverageRssi = runningAverageRssi / this.rssiHistory.size();
		return runningAverageRssi;
	}

	private static double calculateAccuracy(double runningAverageRssi, int txPower)
	{
		double accuracy;
		double ratio = (runningAverageRssi * 1.0) / txPower;
		if (ratio < 1.0)
		{
			accuracy = Math.pow(ratio, 10);
		}
		else
		{
			accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
		}

		return accuracy;
	}

	public boolean getHasConferenceName()
	{
		return this.conferenceName != null && this.conferenceName != "";
	}

	/**********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/
	public int getRssi()
	{
		return rssi;
	}
	
	public int getBoothId()
	{
		return boothId;
	}

	public void setBoothId(int boothId)
	{
		this.boothId = boothId;
	}

	public String getUUID()
	{
		return UUID;
	}

	public void setUUID(String uUID)
	{
		UUID = uUID;
	}

	public int getMajor()
	{
		return major;
	}

	public void setMajor(int major)
	{
		this.major = major;
	}

	public int getMinor()
	{
		return minor;
	}

	public void setMinor(int minor)
	{
		this.minor = minor;
	}

	public int getBoothNumber()
	{
		return boothNumber;
	}

	public void setBoothNumber(int boothNumber)
	{
		this.boothNumber = boothNumber;
	}

	public int getCompanyId()
	{
		return companyId;
	}

	public void setCompanyId(int companyId)
	{
		this.companyId = companyId;
	}

	public String getCompanyName()
	{
		return companyName;
	}

	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	public String getCompanyDescription()
	{
		return companyDescription;
	}

	public void setCompanyDescription(String companyDescription)
	{
		this.companyDescription = companyDescription;
	}

	public String getCompanyShortDescription()
	{
		return companyShortDescription;
	}

	public void setCompanyShortDescription(String companyShortDescription)
	{
		this.companyShortDescription = companyShortDescription;
	}

	public String getConferenceName()
	{
		return conferenceName;
	}

	public void setConferenceName(String conferenceName)
	{
		this.conferenceName = conferenceName;
	}
}
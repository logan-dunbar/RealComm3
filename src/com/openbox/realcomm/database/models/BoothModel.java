package com.openbox.realcomm.database.models;

import java.util.Comparator;
import java.util.LinkedList;
import android.content.res.Resources;

import com.openbox.realcomm.database.objects.Booth;
import com.openbox.realcomm.database.objects.Company;
import com.openbox.realcomm.utilities.enums.ProximityRegion;
import com.openbox.realcomm.utilities.helpers.StringHelper;
import com.radiusnetworks.ibeacon.IBeacon;

public class BoothModel
{
	public static final int DEFAULT_RSSI = -120;
	public static final double DEFAULT_ACCURACY = 20;

	private static final int WINDOW_LENGTH = 5;
	private static final int MAX_NOT_FOUND_COUNT = 3;

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

	private double accuracy = DEFAULT_ACCURACY;
	private int rssi = DEFAULT_RSSI;

	private LinkedList<Double> runningAccuracyList = new LinkedList<>();

	private int runningNotFoundCount = 0;

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
	public void updateBoothModel(IBeacon beacon)
	{
		this.rssi = beacon.getRssi();
		this.accuracy = beacon.getAccuracy() > 0 ? beacon.getAccuracy() : DEFAULT_ACCURACY;

		if (this.accuracy != DEFAULT_ACCURACY)
		{
			updateRunningAccuracy();
			this.runningNotFoundCount = 0;
		}
	}

	public void updateNotFound()
	{
		if (this.runningNotFoundCount++ > MAX_NOT_FOUND_COUNT)
		{
			resetAccuracy();
		}
	}

	public void resetAccuracy()
	{
		this.accuracy = DEFAULT_ACCURACY;
		this.runningAccuracyList.clear();
		this.runningNotFoundCount = MAX_NOT_FOUND_COUNT;
	}

	private void updateRunningAccuracy()
	{
		if (this.runningAccuracyList.size() >= WINDOW_LENGTH)
		{
			this.runningAccuracyList.poll();
		}

		this.runningAccuracyList.add(this.accuracy);
	}

	public int getColor(Resources resources)
	{
		return resources.getColor(getProximityRegion().getColorId());
	}

	public ProximityRegion getProximityRegion()
	{
		double tempAccuracy = getWeightedAccuracy();
		if (tempAccuracy < 0)
		{
			return ProximityRegion.OUT_OF_RANGE;
		}

		if (tempAccuracy < ProximityRegion.IMMEDIATE.getProximityLimit())
		{
			return ProximityRegion.IMMEDIATE;
		}
		else if (tempAccuracy < ProximityRegion.NEAR.getProximityLimit())
		{
			return ProximityRegion.NEAR;
		}
		else if (tempAccuracy < ProximityRegion.FAR.getProximityLimit())
		{
			return ProximityRegion.FAR;
		}
		else
		{
			return ProximityRegion.OUT_OF_RANGE;
		}
	}

	public static Comparator<BoothModel> getAccuracyComparator()
	{
		return new Comparator<BoothModel>()
		{
			@Override
			public int compare(BoothModel lhs, BoothModel rhs)
			{
				double lhsAccuracy = lhs.getWeightedAccuracy();
				double rhsAccuracy = rhs.getWeightedAccuracy();
				if (lhsAccuracy < rhsAccuracy)
				{
					// 23 : 50
					return -1;
				}
				else if (lhsAccuracy > rhsAccuracy)
				{
					// 50 : 23
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

	public boolean getHasConferenceName()
	{
		return !StringHelper.isNullOrEmpty(this.conferenceName);
	}

	/**********************************************************************************************
	 * Private Methods
	 **********************************************************************************************/

	/**********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/
	public double getAccuracy()
	{
		return this.accuracy;
	}

	public double getWeightedAccuracy()
	{
		if (this.runningAccuracyList.size() == 0)
		{
			return this.accuracy;
		}

		double accuracy = 0;
		double weight = 0;

		for (int i = 0; i < this.runningAccuracyList.size(); i++)
		{
			double tempAccuracy = this.runningAccuracyList.get(i);
			double tempWeight = Math.exp(i * -0.2);
			accuracy += tempAccuracy * tempWeight;
			weight += tempWeight;
		}

		return accuracy / weight;
	}

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

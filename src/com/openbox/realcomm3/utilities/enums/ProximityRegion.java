package com.openbox.realcomm3.utilities.enums;

import com.openbox.realcomm3.R;

public enum ProximityRegion
{
	IMMEDIATE(3.0, R.color.opaque_red),
	NEAR(8.0, R.color.opaque_orange),
	FAR(18.0, R.color.opaque_blue),
	OUTOFRANGE(1000.0, R.color.opaque_light_grey);
	
	private final double proximityLimit;
	private final int colorId;
	
	private ProximityRegion(double limit, int colorId)
	{
		this.proximityLimit = limit;
		this.colorId = colorId;
	}

	public double getProximityLimit()
	{
		return proximityLimit;
	}

	public int getColorId()
	{
		return colorId;
	}
}

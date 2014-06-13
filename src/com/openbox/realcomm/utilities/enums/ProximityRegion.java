package com.openbox.realcomm.utilities.enums;

import java.util.Locale;

import com.openbox.realcomm.R;

public enum ProximityRegion
{
	IMMEDIATE(2.0, 10, R.color.opaque_red),
	NEAR(3.0, 15, R.color.opaque_orange),
	FAR(6.0, 30, R.color.opaque_blue),
	OUT_OF_RANGE(1000.0, -1, R.color.opaque_light_grey);

	private final double proximityLimit;
	private final int feetLabel;
	private final int colorId;

	private ProximityRegion(double limit, int feetLabel, int colorId)
	{
		this.proximityLimit = limit;
		this.feetLabel = feetLabel;
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

	public String getLabel()
	{
		// TODO do some calculation here for feet?
		return String.format(Locale.getDefault(), "%dft", (int) this.feetLabel);
	}
}

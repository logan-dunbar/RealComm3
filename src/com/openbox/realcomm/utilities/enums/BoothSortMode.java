package com.openbox.realcomm.utilities.enums;

import com.openbox.realcomm.R;

public enum BoothSortMode
{
	NEAR_ME("Near Me", R.id.boothListNearMeButton),
	A_TO_Z("A - Z", R.id.boothListAtoZButton);

	private String displayName;
	private int buttonId;

	private BoothSortMode(String displayName, int buttonId)
	{
		this.displayName = displayName;
		this.buttonId = buttonId;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public int getButtonId()
	{
		return buttonId;
	}
}

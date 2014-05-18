package com.openbox.realcomm3.utilities.enums;

import com.openbox.realcomm3.R;

public enum RealcommPhonePage
{
	INITIALIZING(-1),
	SPLASH_SCREEN(-1),
	BOOTH_EXPLORE(R.id.exploreButton),
	BOOTH_LIST(R.id.findButton),
	PROFILE_PAGE(-1),
	SCHEDULE_PAGE(R.id.scheduleButton);

	private int buttonId;

	private RealcommPhonePage(int buttonId)
	{
		this.buttonId = buttonId;
	}

	public int getButtonId()
	{
		return buttonId;
	}
}

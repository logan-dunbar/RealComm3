package com.openbox.realcomm3.utilities.enums;

import com.openbox.realcomm3.R;

public enum RealcommPhonePage
{
	INITIALIZING(-1),
	BOOTH_EXPLORE(R.id.exploreButton),
	BOOTH_LIST(R.id.findButton),
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

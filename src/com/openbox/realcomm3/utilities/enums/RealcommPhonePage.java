package com.openbox.realcomm3.utilities.enums;

import com.openbox.realcomm3.R;

public enum RealcommPhonePage
{
	INITIALIZING(-1),
	BOOTH_EXPLORE(R.id.dashboardPhoneExploreLayout),
	BOOTH_LIST(R.id.dashboardPhoneFindLayout),
	SCHEDULE_PAGE(R.id.dashboardPhoneAttendLayout),
	INFO_PAGE(R.id.dashboardPhoneInfoLayout);

	private int layoutId;

	private RealcommPhonePage(int buttonId)
	{
		this.layoutId = buttonId;
	}

	public int getLayoutId()
	{
		return layoutId;
	}
}

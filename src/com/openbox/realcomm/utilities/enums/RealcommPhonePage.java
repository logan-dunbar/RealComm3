package com.openbox.realcomm.utilities.enums;

import com.openbox.realcomm.R;

public enum RealcommPhonePage
{
	INITIALIZING(-1),
	BOOTH_EXPLORE(R.id.dashboardPhoneExploreLayout),
	BOOTH_LIST(R.id.dashboardPhoneSearchLayout),
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

package com.openbox.realcomm.base;

import java.util.Date;

import android.os.Bundle;

import com.openbox.realcomm.database.models.TalkDayModel;
import com.openbox.realcomm.database.models.VenueModel;
import com.openbox.realcomm.utilities.interfaces.ScheduleDataInterface;

public class BaseScheduleFragment extends BaseFragment implements ScheduleDataInterface
{
	private ScheduleDataInterface scheduleDataInterface;

	public ScheduleDataInterface getScheduleDataInterface()
	{
		return scheduleDataInterface;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (getParentFragment() instanceof ScheduleDataInterface)
		{
			this.scheduleDataInterface = (ScheduleDataInterface) getParentFragment();
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.scheduleDataInterface = null;
	}

	@Override
	public TalkDayModel getTalkDayForDate(Date talkDate)
	{
		if (this.scheduleDataInterface != null)
		{
			return this.scheduleDataInterface.getTalkDayForDate(talkDate);
		}

		return null;
	}

	@Override
	public VenueModel getVenueForDate(Date talkDate, int venueId)
	{
		if (this.scheduleDataInterface != null)
		{
			return this.scheduleDataInterface.getVenueForDate(talkDate, venueId);
		}

		return null;
	}
}

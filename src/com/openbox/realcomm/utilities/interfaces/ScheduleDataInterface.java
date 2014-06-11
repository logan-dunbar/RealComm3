package com.openbox.realcomm.utilities.interfaces;

import java.util.Date;

import com.openbox.realcomm.database.models.TalkDayModel;
import com.openbox.realcomm.database.models.VenueModel;

public interface ScheduleDataInterface
{
	TalkDayModel getTalkDayForDate(Date talkDate);
	
	VenueModel getVenueForDate(Date talkDate, int venueId);
}

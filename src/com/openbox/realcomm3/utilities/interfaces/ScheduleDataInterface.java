package com.openbox.realcomm3.utilities.interfaces;

import java.util.Date;

import com.openbox.realcomm3.database.models.TalkDayModel;
import com.openbox.realcomm3.database.models.VenueModel;

public interface ScheduleDataInterface
{
	TalkDayModel getTalkDayForDate(Date talkDate);
	
	VenueModel getVenueForDate(Date talkDate, int venueId);
}

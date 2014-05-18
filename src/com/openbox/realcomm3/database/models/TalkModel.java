package com.openbox.realcomm3.database.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.openbox.realcomm3.database.objects.Talk;
import com.openbox.realcomm3.database.objects.TalkTrack;
import com.openbox.realcomm3.utilities.helpers.DateHelper;

public class TalkModel
{
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
	
	private int venueId;
	private String name;
	private String description;
	private Date startTime;
	private Date endTime;
	private String talkTrack;

	public TalkModel(Talk talk, TalkTrack talkTrack)
	{
		this.venueId = talk.getVenueId();
		this.name = talk.getName();
		this.description = talk.getDescription();
		this.startTime = talk.getStartTime();
		this.endTime = talk.getEndTime();

		this.talkTrack = talkTrack.getName();
	}

	public int getVenueId()
	{
		return venueId;
	}

	public void setVenueId(int venueId)
	{
		this.venueId = venueId;
	}

	public Date getDate()
	{
		return DateHelper.getDateOnly(this.startTime);
	}

	public String getTalkTimeString()
	{
		return dateFormat.format(this.startTime) + " - " + dateFormat.format(this.endTime);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}

	public String getTalkTrack()
	{
		return talkTrack;
	}

	public void setTalkTrack(String talkTrack)
	{
		this.talkTrack = talkTrack;
	}
}

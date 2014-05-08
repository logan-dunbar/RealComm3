package com.openbox.realcomm3.fragments;

import java.util.Date;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.database.models.TalkDayModel;
import com.openbox.realcomm3.database.models.VenueModel;
import com.openbox.realcomm3.utilities.adapters.VenueFragmentAdapter;
import com.openbox.realcomm3.utilities.interfaces.ScheduleDataInterface;

public class TalkDayFragment extends BaseFragment implements ScheduleDataInterface
{
	public static final String TALK_DATE_KEY = "talkDateKey";

	private ScheduleDataInterface scheduleDataInterface;

	private ViewPager venuePager;

	public static TalkDayFragment newInstance()
	{
		TalkDayFragment fragment = new TalkDayFragment();
		return fragment;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_talk_day, container, false);

		this.venuePager = (ViewPager) view.findViewById(R.id.venuePager);
		VenueFragmentAdapter venueAdapter = new VenueFragmentAdapter(getChildFragmentManager());
		this.venuePager.setAdapter(venueAdapter);
		this.venuePager.setOffscreenPageLimit(5);

		if (this.scheduleDataInterface != null)
		{
			Date talkDate = (Date) getArguments().getSerializable(TALK_DATE_KEY);
			TalkDayModel talkDay = this.scheduleDataInterface.getTalkDayForDate(talkDate);
			if (talkDay != null)
			{
				venueAdapter.setItems(talkDate, talkDay.getVenueList());
			}
		}

		return view;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// TODO maybe do something clever, rather than just always to the start?
		this.venuePager.setCurrentItem(0);
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

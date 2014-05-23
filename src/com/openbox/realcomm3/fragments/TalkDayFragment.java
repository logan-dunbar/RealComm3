package com.openbox.realcomm3.fragments;

import java.util.Date;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseScheduleFragment;
import com.openbox.realcomm3.database.models.TalkDayModel;
import com.openbox.realcomm3.utilities.adapters.VenueFragmentAdapter;
import com.viewpagerindicator.LinePageIndicator;

public class TalkDayFragment extends BaseScheduleFragment
{
	public static final String TALK_DATE_KEY = "talkDateKey";

	private ViewPager venuePager;

	public static TalkDayFragment newInstance()
	{
		TalkDayFragment fragment = new TalkDayFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_talk_day, container, false);

		this.venuePager = (ViewPager) view.findViewById(R.id.venuePager);
		VenueFragmentAdapter venueAdapter = new VenueFragmentAdapter(getChildFragmentManager());
		this.venuePager.setAdapter(venueAdapter);
		this.venuePager.setOffscreenPageLimit(5);

		LinePageIndicator indicator = (LinePageIndicator) view.findViewById(R.id.venuePagerIndicator);
		indicator.setViewPager(this.venuePager);

		if (getScheduleDataInterface() != null)
		{
			Date talkDate = (Date) getArguments().getSerializable(TALK_DATE_KEY);
			TalkDayModel talkDay = getScheduleDataInterface().getTalkDayForDate(talkDate);
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
}

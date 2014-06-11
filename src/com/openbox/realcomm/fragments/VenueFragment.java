package com.openbox.realcomm.fragments;

import java.util.Date;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.base.BaseScheduleFragment;
import com.openbox.realcomm.database.models.VenueModel;
import com.openbox.realcomm.utilities.adapters.TalkListAdapter;
import com.openbox.realcomm.R;

public class VenueFragment extends BaseScheduleFragment
{
	private static final String VENUE_ID_KEY = "venueIdKey";

	private TextView venueTextView;
	private ListView talkListView;
	private TalkListAdapter talkAdapter;

	public static VenueFragment newInstance(Date talkDate, int venueId)
	{
		VenueFragment fragment = new VenueFragment();

		Bundle args = new Bundle();
		args.putSerializable(TalkDayFragment.TALK_DATE_KEY, talkDate);
		args.putInt(VENUE_ID_KEY, venueId);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_venue, container, false);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		this.venueTextView = (TextView) view.findViewById(R.id.venueTextView);
		this.venueTextView.setTypeface(application.getExo2Font());

		this.talkListView = (ListView) view.findViewById(R.id.talkListView);
		this.talkAdapter = new TalkListAdapter(getActivity(), application);
		this.talkListView.setAdapter(this.talkAdapter);

		int dividerHeight = (int) getResources().getDimension(R.dimen.talkListItemDividerHeight);
		this.talkListView.setDividerHeight(dividerHeight);

		if (getScheduleDataInterface() != null)
		{
			VenueModel venue = getScheduleDataInterface().getVenueForDate(
				(Date) getArguments().getSerializable(TalkDayFragment.TALK_DATE_KEY),
				getArguments().getInt(VENUE_ID_KEY));

			if (venue != null)
			{
				this.venueTextView.setText(venue.getRoom());
				this.talkAdapter.setItems(venue.getTalkList());
			}
		}

		return view;
	}
}

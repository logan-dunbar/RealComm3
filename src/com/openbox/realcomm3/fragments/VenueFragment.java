package com.openbox.realcomm3.fragments;

import java.util.Date;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.database.models.VenueModel;
import com.openbox.realcomm3.utilities.adapters.TalkListAdapter;
import com.openbox.realcomm3.utilities.interfaces.ScheduleDataInterface;

public class VenueFragment extends BaseFragment
{
	private static final String VENUE_ID_KEY = "venueIdKey";

	private TextView venueTextView;
	private ListView talkListView;
	private TalkListAdapter talkAdapter;

	private ScheduleDataInterface scheduleDataInterface;

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
		View view = inflater.inflate(R.layout.fragment_venue, container, false);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		this.venueTextView = (TextView) view.findViewById(R.id.venueTextView);
		this.venueTextView.setTypeface(application.getExo2Font());

		this.talkListView = (ListView) view.findViewById(R.id.talkListView);
		this.talkAdapter = new TalkListAdapter(getActivity(), application);
		this.talkListView.setAdapter(this.talkAdapter);

		if (this.scheduleDataInterface != null)
		{
			Bundle args = getArguments();
			VenueModel venue = this.scheduleDataInterface
				.getVenueForDate((Date) args.getSerializable(TalkDayFragment.TALK_DATE_KEY), args.getInt(VENUE_ID_KEY));
			if (venue != null)
			{
				this.venueTextView.setText(venue.getRoom());
				this.talkAdapter.setItems(venue.getTalkList());
			}
		}

		return view;
	}
}

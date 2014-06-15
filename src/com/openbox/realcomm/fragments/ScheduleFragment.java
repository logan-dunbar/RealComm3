package com.openbox.realcomm.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabWidget;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.application.SharedPreferencesManager;
import com.openbox.realcomm.base.BaseScheduleFragment;
import com.openbox.realcomm.database.models.TalkDayModel;
import com.openbox.realcomm.database.models.VenueModel;
import com.openbox.realcomm.utilities.helpers.DateHelper;
import com.openbox.realcomm.utilities.loaders.ScheduleLoader;
import com.openbox.realcomm.R;

public class ScheduleFragment extends BaseScheduleFragment
{
	public static final String TAG = "scheduleFragment";

	private static final String DAY_PREFIX = "Day ";
	private static final int SCHEDULE_LOADER_ID = 1;

	private FragmentTabHost scheduleTabHost;
	private FrameLayout scheduleLayout;

	private List<TalkDayModel> talkDayList = new ArrayList<>();

	private boolean onPauseCalled = false;
	private Runnable startLoaderRunnable;

	public static ScheduleFragment newInstance()
	{
		ScheduleFragment fragment = new ScheduleFragment();
		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		startScheduleLoader();
	}

	private void startScheduleLoader()
	{
		getLoaderManager().restartLoader(SCHEDULE_LOADER_ID, null, this.scheduleLoaderCallbacks);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_schedule, container, false);

		this.scheduleLayout = (FrameLayout) view.findViewById(R.id.scheduleLayout);

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if (this.startLoaderRunnable != null)
		{
			this.startLoaderRunnable.run();
			this.startLoaderRunnable = null;
		}

		onPauseCalled = false;
	}

	@Override
	public void onPause()
	{
		super.onPause();

		onPauseCalled = true;
	}

	/**********************************************************************************************
	 * Data Changed Callbacks
	 **********************************************************************************************/
	@Override
	public void onDataChanged()
	{
		if (!onPauseCalled)
		{
			startScheduleLoader();
		}
		else
		{
			this.startLoaderRunnable = new Runnable()
			{
				@Override
				public void run()
				{
					startScheduleLoader();
				}
			};
		}
	}

	/**********************************************************************************************
	 * Schedule Data Interface Implements
	 **********************************************************************************************/
	@Override
	public TalkDayModel getTalkDayForDate(Date talkDate)
	{
		for (TalkDayModel talkDayModel : this.talkDayList)
		{
			if (talkDayModel.getDate().equals(talkDate))
			{
				return talkDayModel;
			}
		}

		return null;
	}

	@Override
	public VenueModel getVenueForDate(Date talkDate, int venueId)
	{
		TalkDayModel talkDay = getTalkDayForDate(talkDate);

		if (talkDay != null)
		{
			for (VenueModel venue : talkDay.getVenueList())
			{
				if (venue.getVenueId() == venueId)
				{
					return venue;
				}
			}
		}

		return null;
	}

	/**********************************************************************************************
	 * Private Helper Methods
	 **********************************************************************************************/
	private void finishScheduleLoad(List<TalkDayModel> results)
	{
		this.talkDayList = results;
		updateScheduleFragment();
	}

	// TODO This is a seriously siff method, needs refactoring, no time
	private void updateScheduleFragment()
	{
		TreeMap<Date, String> distinctDateList = getDistinctDayList();
		if (distinctDateList != null && distinctDateList.size() > 0)
		{
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			RealCommApplication application = (RealCommApplication) getActivity().getApplication();

			// Create tabHost or populate reference
			boolean created = createTabHost(inflater);

			// Get the prefix for the tab fragments' tags
			String tabTagPrefix = getLastUpdateDate();

			this.scheduleTabHost.clearAllTabs();

			// For each day, create a tab holding a TalkDayFragment
			int index = 0;
			int selectedDateIndex = 0;
			for (TreeMap.Entry<Date, String> entry : distinctDateList.entrySet())
			{
				Date talkDate = entry.getKey();
				String talkDateDisplayName = entry.getValue();

				// Set up the indicator (i.e. tab)
				View view = inflater.inflate(R.layout.tab_item_schedule, null);
				TextView title = (TextView) view.findViewById(R.id.tabTitle);
				title.setTypeface(application.getExo2Font());
				title.setText(talkDateDisplayName);

				// Create unique name based on update so it doesn't reuse old fragment
				String tabFragmentTag = tabTagPrefix + talkDateDisplayName;

				Bundle args = new Bundle();
				args.putSerializable(TalkDayFragment.TALK_DATE_KEY, talkDate);
				TabSpec tabSepc = this.scheduleTabHost.newTabSpec(tabFragmentTag).setIndicator(view);
				this.scheduleTabHost.addTab(tabSepc, TalkDayFragment.class, args);

				if (DateHelper.getDateOnly(new Date()).equals(DateHelper.getDateOnly(talkDate)))
				{
					selectedDateIndex = index;
				}

				index++;
			}

			// TabWidget doesn't respect padding/margin in xml, need to workaround like this
			TabWidget tw = this.scheduleTabHost.getTabWidget();
			int tabSpacing = (int) getResources().getDimension(R.dimen.tabSpacing);
			int tabWidth = (int) getResources().getDimension(R.dimen.tabWidth);
			int tabHeight = (int) getResources().getDimension(R.dimen.tabHeight);
			for (int i = 0; i < tw.getChildCount(); i++)
			{
				View tab = tw.getChildAt(i);

				LinearLayout.LayoutParams params = (LayoutParams) tab.getLayoutParams();
				params.width = tabWidth;
				params.height = tabHeight;

				// Don't right pad the last one
				if (i != tw.getChildCount() - 1)
				{
					params.setMargins(0, 0, tabSpacing, 0);
				}
			}

			tw.requestLayout();

			// Set the tab to the current date if it is a tab, or first
			this.scheduleTabHost.setCurrentTab(selectedDateIndex);

			if (created)
			{
				// Can only add to the layout once all the tabs have been initialized
				this.scheduleLayout.addView(this.scheduleTabHost);
			}
		}
	}

	private String getLastUpdateDate()
	{
		String lastUpdateDate = "";
		Long lastUpdatedDateLong = SharedPreferencesManager.getLastUpdateDate(getActivity());
		if (lastUpdatedDateLong != SharedPreferencesManager.DEFAULT_LONG_VALUE)
		{
			lastUpdateDate = String.valueOf(lastUpdatedDateLong);
		}

		return lastUpdateDate;
	}

	private boolean createTabHost(LayoutInflater inflater)
	{
		this.scheduleTabHost = (FragmentTabHost) this.scheduleLayout.findViewById(R.id.scheduleTabHost);
		if (this.scheduleTabHost == null)
		{
			this.scheduleTabHost = new FragmentTabHost(getActivity());

			// This line caused me so much grief. I actually cried a little bit. Because
			// I never set the Id, it would never find it, and therefore keep adding new TabHosts :'(
			// So many hours... so many...
			this.scheduleTabHost.setId(R.id.scheduleTabHost);

			LinearLayout ll = new LinearLayout(getActivity());
			ll.setOrientation(LinearLayout.VERTICAL);
			this.scheduleTabHost.addView(ll, new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));

			TabWidget tw = new TabWidget(getActivity());
			tw.setId(android.R.id.tabs);
			tw.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
			params.gravity = Gravity.CENTER;
			ll.addView(tw, params);

			FrameLayout fl = new FrameLayout(getActivity());
			fl.setId(android.R.id.tabcontent);
			ll.addView(fl, new LinearLayout.LayoutParams(0, 0, 0));

			fl = new FrameLayout(getActivity());
			fl.setId(R.id.scheduleTabContent);
			ll.addView(fl, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
			this.scheduleTabHost.setup(getActivity(), getChildFragmentManager(), R.id.scheduleTabContent);

			return true;
		}
		else
		{
			return false;
		}
	}

	private TreeMap<Date, String> getDistinctDayList()
	{
		// TreeMap used because it is automatically sorted on key

		// Get list of dates
		List<Date> dateList = new ArrayList<>();
		for (TalkDayModel talkDay : this.talkDayList)
		{
			dateList.add(talkDay.getDate());
		}

		// Sort dates in ascending order
		Collections.sort(dateList);

		// Create the distinct list, including its "display name" (Day 1, Day 2, etc.)
		TreeMap<Date, String> distinctDateList = new TreeMap<>();
		for (int i = 0; i < dateList.size(); i++)
		{
			// + 1 to display 0 indexed number
			distinctDateList.put(dateList.get(i), DAY_PREFIX + (i + 1));
		}

		return distinctDateList;
	}

	/**********************************************************************************************
	 * Loader Callbacks
	 **********************************************************************************************/
	private LoaderCallbacks<List<TalkDayModel>> scheduleLoaderCallbacks = new LoaderCallbacks<List<TalkDayModel>>()
	{
		@Override
		public Loader<List<TalkDayModel>> onCreateLoader(int loaderId, Bundle bundle)
		{
			return new ScheduleLoader(getActivity());
		}

		@Override
		public void onLoadFinished(Loader<List<TalkDayModel>> loader, List<TalkDayModel> results)
		{
			finishScheduleLoad(results);
		}

		@Override
		public void onLoaderReset(Loader<List<TalkDayModel>> loader)
		{
		}
	};
}

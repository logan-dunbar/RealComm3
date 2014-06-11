package com.openbox.realcomm.utilities.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.openbox.realcomm.database.models.VenueModel;
import com.openbox.realcomm.fragments.VenueFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class VenueFragmentAdapter extends FragmentPagerAdapter
{
	List<VenueModel> venueList = new ArrayList<VenueModel>();
	private Date talkDate;

	public VenueFragmentAdapter(FragmentManager fm)
	{
		super(fm);
	}

	public void setItems(Date talkDate, List<VenueModel> venueList)
	{
		if (talkDate != null && venueList != null)
		{
			this.talkDate = talkDate;
			this.venueList = venueList;
			notifyDataSetChanged();
		}
	}

	@Override
	public Fragment getItem(int position)
	{
		// TODO: Maybe sort or something
		return VenueFragment.newInstance(this.talkDate, this.venueList.get(position).getVenueId());
	}

	@Override
	public int getCount()
	{
		return this.venueList.size();
	}
}

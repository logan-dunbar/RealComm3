package com.openbox.realcomm.utilities.adapters;

import java.util.ArrayList;
import java.util.List;

import com.openbox.realcomm.fragments.ContactFragment;
import com.openbox.realcomm.utilities.interfaces.AdapterFinishUpdateCallbacks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class ContactFragmentAdapter extends FragmentStatePagerAdapter
{
	private List<Integer> contactIds = new ArrayList<>();
	private AdapterFinishUpdateCallbacks listener;

	public ContactFragmentAdapter(FragmentManager fm, AdapterFinishUpdateCallbacks listener)
	{
		super(fm);
		this.listener = listener;
	}

	public void setItems(List<Integer> contactIds)
	{
		this.contactIds.clear();
		if (contactIds != null)
		{
			this.contactIds = contactIds;
		}

		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position)
	{
		// TODO Sort based on isPrimary
		return ContactFragment.newInstance(this.contactIds.get(position));
	}

	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}

	@Override
	public int getCount()
	{
		return this.contactIds.size();
	}

	@Override
	public void finishUpdate(ViewGroup container)
	{
		super.finishUpdate(container);

		if (this.listener != null)
		{
			this.listener.onFinishUpdate();
		}
	}
}

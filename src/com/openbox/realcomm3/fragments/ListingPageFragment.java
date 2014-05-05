package com.openbox.realcomm3.fragments;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.enums.RealcommPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ListingPageFragment extends BaseFragment
{
	public static final String TAG = "listingPageFragment";

	public static ListingPageFragment newInstance()
	{
		ListingPageFragment fragment = new ListingPageFragment();

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_listing_page, container, false);

		Button button = (Button) view.findViewById(R.id.goToProfilePage);
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (getActivityListener() != null)
				{
					getActivityListener().changePage(RealcommPage.ProfilePage);
				}
			}
		});

		return view;
	}
}

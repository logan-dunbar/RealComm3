package com.openbox.realcomm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openbox.realcomm.base.BaseBoothFlipperFragment;
import com.openbox.realcomm.R;

public class BoothExploreFragment extends BaseBoothFlipperFragment
{
	public static final String TAG = "boothExploreFragment";

	@Override
	public int getNumberOfBigBooths()
	{
		return 3;
	}

	public static BoothExploreFragment newInstance()
	{
		BoothExploreFragment fragment = new BoothExploreFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_booth_explore, container, false);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		createProximityFragment();
	}

	/**********************************************************************************************
	 * Private Helper Methods
	 **********************************************************************************************/
	private void createProximityFragment()
	{
		ProximityFragment fragment = (ProximityFragment) getChildFragmentManager().findFragmentById(R.id.proximityContainer);
		if (fragment == null)
		{
			fragment = ProximityFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.proximityContainer, fragment).commitAllowingStateLoss();
		}

		getDataChangedListeners().add(fragment);
		getAppModeChangedListeners().add(fragment);
	}
}

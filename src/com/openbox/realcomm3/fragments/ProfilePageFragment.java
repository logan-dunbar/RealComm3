package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseFragment;

public class ProfilePageFragment extends BaseFragment
{
	public static final String TAG = "profilePageFragment";

	public static ProfilePageFragment newInstance()
	{
		ProfilePageFragment fragment = new ProfilePageFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

		return view;
	}

}

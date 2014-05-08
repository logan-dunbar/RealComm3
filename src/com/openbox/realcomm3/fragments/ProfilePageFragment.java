package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.database.objects.Company;
import com.openbox.realcomm3.utilities.helpers.ToastHelper;

public class ProfilePageFragment extends BaseFragment
{
	public static final String TAG = "profilePageFragment";

	public static ProfilePageFragment newInstance(int companyId)
	{
		ProfilePageFragment fragment = new ProfilePageFragment();

		Bundle args = new Bundle();
		args.putInt(Company.COMPANY_ID_COLUMN_NAME, companyId);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

		ToastHelper.showShortMessage(getActivity(), "Company ID: " + getArguments().getInt(Company.COMPANY_ID_COLUMN_NAME));

		return view;
	}

}

package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseProfileFragment;

public class CompanyContactsFragment extends BaseProfileFragment
{
	public static CompanyContactsFragment newInstance()
	{
		CompanyContactsFragment fragment = new CompanyContactsFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_company_contacts, container, false);

		updateView();

		return view;
	}

	private void updateView()
	{

	}
}

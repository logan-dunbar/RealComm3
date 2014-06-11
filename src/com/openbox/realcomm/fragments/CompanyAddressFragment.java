package com.openbox.realcomm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.base.BaseProfileFragment;
import com.openbox.realcomm.database.models.CompanyModel;
import com.openbox.realcomm.R;

public class CompanyAddressFragment extends BaseProfileFragment
{
	private TextView companyAddress;

	public static CompanyAddressFragment newInstance()
	{
		CompanyAddressFragment fragment = new CompanyAddressFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_company_address, container, false);

		TextView addressHeader = (TextView) view.findViewById(R.id.companyAddressHeader);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();
		this.companyAddress = (TextView) view.findViewById(R.id.companyAddressTextView);

		addressHeader.setTypeface(application.getExo2Font());
		this.companyAddress.setTypeface(application.getExo2Font());

		return view;
	}

	@Override
	public void onCompanyLoaded()
	{
		updateView();
	}

	private void updateView()
	{
		CompanyModel model = getCompany();
		if (model != null)
		{
			this.companyAddress.setText(model.getFormattedAddress());
		}
	}
}

package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;

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

		this.companyAddress = (TextView) view.findViewById(R.id.companyAddressTextView);

		updateView();

		return view;
	}

	private void updateView()
	{
		if (getProfilePageInterface() != null)
		{
			CompanyModel model = getProfilePageInterface().getCompany();
			if (model != null)
			{
				StringBuilder sb = new StringBuilder();

				// TODO obviously need to do null checks etc, just lazy at the moment
				sb.append(model.getAddress1());
				sb.append("\n");
				sb.append(model.getAddress2());
				sb.append("\n");
				sb.append(model.getAddress3());
				sb.append("\n");

				sb.append(model.getCity() + ", " + model.getState() + ", " + model.getPostalCode());
				// sb.append("\n");

				// TODO Country?
				// sb.append(model.getCountry());

				this.companyAddress.setText(sb.toString());
			}
		}
	}

}

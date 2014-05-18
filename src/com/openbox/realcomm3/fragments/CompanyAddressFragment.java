package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;
import com.openbox.realcomm3.utilities.interfaces.ProfileDataChangedCallbacks;

public class CompanyAddressFragment extends BaseProfileFragment implements ProfileDataChangedCallbacks
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
	
	public void showBorder()
	{
		
	}

	private void updateView()
	{
		CompanyModel model = getCompanyModel();
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

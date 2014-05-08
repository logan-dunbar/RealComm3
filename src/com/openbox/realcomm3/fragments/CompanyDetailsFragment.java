package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;

public class CompanyDetailsFragment extends BaseProfileFragment
{
	private ImageView companyLogo;
	private TextView companyName;
	private TextView boothNumber;
	private TextView companyDescription;

	public static CompanyDetailsFragment newInstance()
	{
		CompanyDetailsFragment fragment = new CompanyDetailsFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_company_details, container, false);

		this.companyLogo = (ImageView) view.findViewById(R.id.companyLogoImageView);
		this.companyName = (TextView) view.findViewById(R.id.companyNameTextView);
		this.boothNumber = (TextView) view.findViewById(R.id.boothNumberTextView);
		this.companyDescription = (TextView) view.findViewById(R.id.companyDescriptionTextView);

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
				this.companyLogo.setImageBitmap(model.getCompanyLogo());
				this.companyName.setText(model.getName());
				this.boothNumber.setText(String.valueOf(model.getBoothNumber()));
				this.companyDescription.setText(model.getDescription());
			}
		}
	}
}

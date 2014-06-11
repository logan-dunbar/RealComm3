package com.openbox.realcomm.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.base.BaseProfileFragment;
import com.openbox.realcomm.database.models.CompanyModel;
import com.openbox.realcomm.utilities.helpers.BitmapHelper;
import com.openbox.realcomm.R;

public class CompanyDetailsFragment extends BaseProfileFragment
{
	private static final String BOOTH_NUMBER_PREFIX = "BOOTH ";

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

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		this.companyLogo = (ImageView) view.findViewById(R.id.companyLogoImageView);
		this.companyName = (TextView) view.findViewById(R.id.companyNameTextView);
		this.boothNumber = (TextView) view.findViewById(R.id.boothNumberTextView);
		this.companyDescription = (TextView) view.findViewById(R.id.companyDescriptionTextView);

		this.companyName.setTypeface(application.getExo2Font());
		this.boothNumber.setTypeface(application.getExo2FontBold());
		this.companyDescription.setTypeface(application.getExo2Font());

		return view;
	}

	private void updateView()
	{
		CompanyModel model = getCompany();
		if (model != null)
		{
			if (model.getCompanyLogo() != null)
			{
				float radius = getResources().getDimension(R.dimen.defaultCornerRadius);
				int height = (int) getResources().getDimension(R.dimen.boothCompanyLogoHeight);

				double aspectRatio = ((double) model.getCompanyLogo().getWidth()) / model.getCompanyLogo().getHeight();
				int width = (int) Math.round(height * aspectRatio);

				Bitmap logo = BitmapHelper.getRoundedBitmap(model.getCompanyLogo(), width, height, radius);
				this.companyLogo.setImageBitmap(logo);
			}

			this.companyName.setText(model.getName());

			String boothNumberText = BOOTH_NUMBER_PREFIX + String.valueOf(model.getBoothNumber());
			if (model.getHasConferenceName())
			{
				boothNumberText += " - " + model.getConferenceName();
			}

			this.boothNumber.setText(boothNumberText);

			this.companyDescription.setText(model.getDescription());
		}
	}

	@Override
	public void onCompanyLoaded()
	{
		updateView();
	}
}

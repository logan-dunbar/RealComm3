package com.openbox.realcomm3.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;
import com.openbox.realcomm3.utilities.helpers.BitmapHelper;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.ProfileDataChangedCallbacks;

public class CompanyDetailsFragment extends BaseProfileFragment implements ProfileDataChangedCallbacks, DataChangedCallbacks
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
		CompanyModel model = getCompanyModel();
		if (model != null)
		{
			float radius = getResources().getDimension(R.dimen.defaultCornerRadius);
			int width = (int) getResources().getDimension(R.dimen.companyDetailsCompanyLogoWidth);
			int height = (int) getResources().getDimension(R.dimen.companyDetailsCompanyLogoHeight);
			Bitmap logo = BitmapHelper.getRoundedBitmap(model.getCompanyLogo(), width, height, radius);

			this.companyLogo.setImageBitmap(logo);
			this.companyName.setText(model.getName());
			this.boothNumber.setText(String.valueOf(model.getBoothNumber()));
			this.companyDescription.setText(model.getDescription());
		}
	}

	@Override
	public void onCompanyLoaded()
	{
		updateView();
	}

	@Override
	public void onDataLoaded()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onDataChanged()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBeaconsUpdated()
	{
		// TODO Do color stuff to be cool
	}
}

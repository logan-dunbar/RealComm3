package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;

public class CompanySocialNetworksFragment extends BaseProfileFragment
{
	private LinearLayout socialNetworksLayout;
	private ImageView facebook;
	private ImageView twitter;
	private ImageView linkedIn;

	public static CompanySocialNetworksFragment newInstance()
	{
		CompanySocialNetworksFragment fragment = new CompanySocialNetworksFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_company_social_networks, container, false);

		// TODO add the links as clickable etc...

		this.socialNetworksLayout = (LinearLayout) view.findViewById(R.id.companySocialNetworksLayout);
		this.facebook = (ImageView) view.findViewById(R.id.facebookImageView);
		this.twitter = (ImageView) view.findViewById(R.id.twitterImageView);
		this.linkedIn = (ImageView) view.findViewById(R.id.linkedInImageView);

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
				updateFacebook(model);

				updateTwitter(model);

				updateLinkedIn(model);
			}
		}
	}

	private void updateFacebook(CompanyModel model)
	{
		if (model.getHasFacebook())
		{
			this.facebook.setVisibility(View.VISIBLE);
		}
		else
		{
			this.socialNetworksLayout.removeView(this.facebook);
		}
	}

	private void updateTwitter(CompanyModel model)
	{
		if (model.getHasTwitter())
		{
			this.twitter.setVisibility(View.VISIBLE);
		}
		else
		{
			this.socialNetworksLayout.removeView(this.twitter);
		}
	}

	private void updateLinkedIn(CompanyModel model)
	{
		if (model.getHasLinkedIn())
		{
			this.linkedIn.setVisibility(View.VISIBLE);
		}
		else
		{
			this.socialNetworksLayout.removeView(this.linkedIn);
		}
	}
}

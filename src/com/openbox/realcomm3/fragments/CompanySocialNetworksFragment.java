package com.openbox.realcomm3.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;

public class CompanySocialNetworksFragment extends BaseProfileFragment
{
	private ImageButton facebook;
	private ImageButton twitter;
	private ImageButton linkedIn;

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

		this.facebook = (ImageButton) view.findViewById(R.id.facebookImageButton);
		this.twitter = (ImageButton) view.findViewById(R.id.twitterImageButton);
		this.linkedIn = (ImageButton) view.findViewById(R.id.linkedInImageButton);

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
			updateFacebook(model);

			updateTwitter(model);

			updateLinkedIn(model);
		}
	}

	private void updateFacebook(final CompanyModel model)
	{
		if (model.getHasFacebook())
		{
			this.facebook.setVisibility(View.VISIBLE);
			// attachOnClickListener(this.facebook, model.getFacebook());
			this.facebook.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent i = getOpenFacebookIntent(model.getFacebook(), model.getFacebookProfileId());
					startActivity(i);
				}
			});
		}
		else
		{
			this.facebook.setVisibility(View.GONE);
		}
	}

	private void updateTwitter(CompanyModel model)
	{
		if (model.getHasTwitter())
		{
			this.twitter.setVisibility(View.VISIBLE);
			attachOnClickListener(this.twitter, model.getTwitter());
		}
		else
		{
			this.twitter.setVisibility(View.GONE);
		}
	}

	private void updateLinkedIn(CompanyModel model)
	{
		if (model.getHasLinkedIn())
		{
			this.linkedIn.setVisibility(View.VISIBLE);
			attachOnClickListener(this.linkedIn, model.getLinkedIn());
		}
		else
		{
			this.linkedIn.setVisibility(View.GONE);
		}
	}

	private void attachOnClickListener(ImageButton button, final String address)
	{
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
				startActivity(i);
			}
		});
	}

	private Intent getOpenFacebookIntent(String address, String profileId)
	{
		try
		{
			getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
			return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + profileId));
		}
		catch (Exception e)
		{
			return new Intent(Intent.ACTION_VIEW, Uri.parse(address));
		}
	}
}

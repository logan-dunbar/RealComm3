package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;

public class CompanyLinksFragment extends BaseProfileFragment
{
	private TextView companyLinks;

	public static CompanyLinksFragment newInstance()
	{
		CompanyLinksFragment fragment = new CompanyLinksFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_company_links, container, false);

		this.companyLinks = (TextView) view.findViewById(R.id.companyLinksTextView);

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

				sb.append(model.getWebsite());
				// sb.append("\n");
				// TODO more here?

				this.companyLinks.setText(sb.toString());
			}
		}
	}
}

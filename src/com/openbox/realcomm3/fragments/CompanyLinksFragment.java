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

public class CompanyLinksFragment extends BaseProfileFragment implements ProfileDataChangedCallbacks
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

		// TODO make these actual links
		this.companyLinks = (TextView) view.findViewById(R.id.companyLinksTextView);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();
		TextView linksHeader = (TextView) view.findViewById(R.id.companyLinksHeader);
		linksHeader.setTypeface(application.getExo2Font());

		return view;
	}

	@Override
	public void onCompanyLoaded()
	{
		updateView();
	}

	private void updateView()
	{
		CompanyModel model = getCompanyModel();
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

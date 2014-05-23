package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.controls.VariableHeightViewPager;
import com.openbox.realcomm3.database.models.CompanyModel;
import com.openbox.realcomm3.utilities.adapters.ContactFragmentAdapter;
import com.openbox.realcomm3.utilities.interfaces.AdapterFinishUpdateCallbacks;
import com.viewpagerindicator.LinePageIndicator;

public class CompanyContactsFragment extends BaseProfileFragment implements AdapterFinishUpdateCallbacks
{
	private static final int CONTACT_PAGER_OFF_SCREEN_LIMIT = 3;

	private VariableHeightViewPager contactPager;
	private ContactFragmentAdapter contactAdapter;
	private LinePageIndicator indicator;

	public static CompanyContactsFragment newInstance()
	{
		CompanyContactsFragment fragment = new CompanyContactsFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_company_contacts, container, false);

		this.contactPager = (VariableHeightViewPager) view.findViewById(R.id.contactPager);
		this.contactPager.setOffscreenPageLimit(CONTACT_PAGER_OFF_SCREEN_LIMIT);
		this.contactAdapter = new ContactFragmentAdapter(getChildFragmentManager(), this);
		this.contactPager.setAdapter(this.contactAdapter);

		this.indicator = (LinePageIndicator) view.findViewById(R.id.contactPagerIndicator);
		this.indicator.setViewPager(this.contactPager);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();
		TextView contactsHeader = (TextView) view.findViewById(R.id.companyContactsHeader);
		contactsHeader.setTypeface(application.getExo2Font());

		return view;
	}

	@Override
	public void onFinishUpdate()
	{
		this.contactPager.setVariableHeight();
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
			this.contactAdapter.setItems(model.getContactIds());
			if (model.getContactIds().size() > 1)
			{
				this.indicator.setVisibility(View.VISIBLE);
			}
			else
			{
				this.indicator.setVisibility(View.GONE);
			}
		}
	}
}

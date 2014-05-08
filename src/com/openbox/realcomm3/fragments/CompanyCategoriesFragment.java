package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;

public class CompanyCategoriesFragment extends BaseProfileFragment
{
	private TextView categories;

	public static CompanyCategoriesFragment newInstance()
	{
		CompanyCategoriesFragment fragment = new CompanyCategoriesFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_company_categories, container, false);

		this.categories = (TextView) view.findViewById(R.id.companyCategoriesTextView);

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
				// TODO format shit here
				StringBuilder sb = new StringBuilder();

				// Blah System.get wada wada newLine();
				sb.append(model.getMainCatergories());
				sb.append("\n");
				sb.append(model.getSubCategories());
				sb.append("\n");
				sb.append(model.getTargetMarkets());
				sb.append("\n");
				sb.append(model.getGeographicMarkets());

				this.categories.setText(sb.toString());
			}
		}
	}
}

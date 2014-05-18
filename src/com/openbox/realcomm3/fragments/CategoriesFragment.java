package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseProfileFragment;

public class CategoriesFragment extends BaseProfileFragment
{
	private static final String CATEGORY_HEADER_KEY = "categoryHeaderKey";
	private static final String CATEGORY_DETAILS_KEY = "categoryDetailsKey";

	private static final int BULLET_SPACING = 10;

	public static CategoriesFragment newInstance(String categoryHeader, String categoryDetails)
	{
		CategoriesFragment fragment = new CategoriesFragment();

		Bundle args = new Bundle();
		args.putString(CATEGORY_HEADER_KEY, categoryHeader);
		args.putString(CATEGORY_DETAILS_KEY, categoryDetails);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// This allows multiple fragments to be added without covering each other
		View view = inflater.inflate(R.layout.fragment_categories, null);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		TextView header = (TextView) view.findViewById(R.id.categoryHeader);
		TextView details = (TextView) view.findViewById(R.id.categoryDetails);

		header.setTypeface(application.getExo2FontBold());
		details.setTypeface(application.getExo2Font());

		// Do stuff here
		header.setText(getArguments().getString(CATEGORY_HEADER_KEY));

		String categoryDetailsString = getArguments().getString(CATEGORY_DETAILS_KEY);
		String[] categoryDetails = categoryDetailsString.split("[\\s]*,[\\s]*");

		CharSequence spannedCategories = "";
		for (String categoryDetail : categoryDetails)
		{
			categoryDetail = categoryDetail + "\n";
			SpannableString span = new SpannableString(categoryDetail);
			span.setSpan(new BulletSpan(BULLET_SPACING), 0, categoryDetail.length(), 0);
			spannedCategories = TextUtils.concat(spannedCategories, span);
		}

		details.setText(spannedCategories);

		return view;
	}
}

package com.openbox.realcomm.fragments;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.base.BaseProfileFragment;
import com.openbox.realcomm.database.models.CompanyModel;
import com.openbox.realcomm.utilities.enums.CompanyCategory;
import com.openbox.realcomm.utilities.helpers.StringHelper;
import com.openbox.realcomm.R;

public class CategoriesFragment extends BaseProfileFragment
{
	private static final String COMPANY_CATEGORY_KEY = "companyCategoryKey";

	private static final int BULLET_SPACING = 10;

	public static CategoriesFragment newInstance(CompanyCategory companyCategory)
	{
		CategoriesFragment fragment = new CategoriesFragment();

		Bundle args = new Bundle();
		args.putSerializable(COMPANY_CATEGORY_KEY, companyCategory);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_categories, container, false);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		TextView header = (TextView) view.findViewById(R.id.categoryHeader);
		TextView details = (TextView) view.findViewById(R.id.categoryDetails);

		header.setTypeface(application.getExo2FontBold());
		details.setTypeface(application.getExo2Font());

		CompanyCategory companyCategory = (CompanyCategory) getArguments().getSerializable(COMPANY_CATEGORY_KEY);
		CompanyModel model = getCompany();
		if (companyCategory != null && model != null)
		{
			header.setText(companyCategory.getHeader());

			String categoryDetailsString = model.getCompanyCategoryDetails(companyCategory);
			String[] categoryDetails = categoryDetailsString.split("[\\s]*,[\\s]*");

			CharSequence spannedCategories = "";
			for (int i = 0; i < categoryDetails.length; i++)
			{
				if (categoryDetails.length > 1 && i < categoryDetails.length - 1)
				{
					categoryDetails[i] = categoryDetails[i] + StringHelper.NEW_LINE;
				}

				SpannableString span = new SpannableString(categoryDetails[i]);
				span.setSpan(new BulletSpan(BULLET_SPACING), 0, categoryDetails[i].length(), 0);
				spannedCategories = TextUtils.concat(spannedCategories, span);
			}

			details.setText(spannedCategories);
		}

		return view;
	}

	public void updateIsLast()
	{
		MarginLayoutParams params = (MarginLayoutParams) getView().getLayoutParams();
		params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 0);
		getView().requestLayout();
	}
}

package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;
import com.openbox.realcomm3.utilities.enums.CompanyCategory;
import com.openbox.realcomm3.utilities.interfaces.ProfileDataChangedCallbacks;

public class CompanyCategoriesFragment extends BaseProfileFragment implements ProfileDataChangedCallbacks
{
	private static final String CURRENTLY_ADDED_FRAGMENTS_KEY = "currentlyAddedFragmentsKey";
	List<String> currentlyAddedFragments = new ArrayList<>();

	public static CompanyCategoriesFragment newInstance()
	{
		CompanyCategoriesFragment fragment = new CompanyCategoriesFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null)
		{
			this.currentlyAddedFragments = savedInstanceState.getStringArrayList(CURRENTLY_ADDED_FRAGMENTS_KEY);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putStringArrayList(CURRENTLY_ADDED_FRAGMENTS_KEY, (ArrayList<String>) this.currentlyAddedFragments);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_company_categories, container, false);
	}

	@Override
	public void onCompanyLoaded()
	{
		updateView();
	}

	private void updateView()
	{
		removeCurrentlyAddedFragments();

		CompanyModel model = getCompanyModel();
		if (model != null)
		{
			FragmentTransaction ft = getChildFragmentManager().beginTransaction();

			CategoriesFragment mainCategoriesFragment = (CategoriesFragment) getChildFragmentManager().findFragmentByTag(
				CompanyCategory.MAIN_CATEGORY.getFragmentTag());
			if (model.getHasMainCategories() && mainCategoriesFragment == null)
			{
				mainCategoriesFragment = CategoriesFragment.newInstance(CompanyCategory.MAIN_CATEGORY.getHeader(), model.getMainCatergories());
				addCategoryFragment(ft, mainCategoriesFragment, CompanyCategory.MAIN_CATEGORY.getFragmentTag());
			}

			CategoriesFragment subCategoriesFragment = (CategoriesFragment) getChildFragmentManager().findFragmentByTag(
				CompanyCategory.SUB_CATEGORY.getFragmentTag());
			if (model.getHasSubCategories() && subCategoriesFragment == null)
			{
				subCategoriesFragment = CategoriesFragment.newInstance(CompanyCategory.SUB_CATEGORY.getHeader(), model.getSubCategories());
				addCategoryFragment(ft, subCategoriesFragment, CompanyCategory.SUB_CATEGORY.getFragmentTag());
			}

			CategoriesFragment targetMarketFragment = (CategoriesFragment) getChildFragmentManager().findFragmentByTag(
				CompanyCategory.TARGET_MARKET.getFragmentTag());
			if (model.getHasTargetMarkets() && targetMarketFragment == null)
			{
				targetMarketFragment = CategoriesFragment.newInstance(CompanyCategory.TARGET_MARKET.getHeader(), model.getTargetMarkets());
				addCategoryFragment(ft, targetMarketFragment, CompanyCategory.TARGET_MARKET.getFragmentTag());
			}

			CategoriesFragment geographicMarketFragment = (CategoriesFragment) getChildFragmentManager().findFragmentByTag(
				CompanyCategory.GEOGRAPHIC_MARKET.getFragmentTag());
			if (model.getHasGeographicMarkets() && geographicMarketFragment == null)
			{
				geographicMarketFragment = CategoriesFragment.newInstance(CompanyCategory.GEOGRAPHIC_MARKET.getHeader(), model.getGeographicMarkets());
				addCategoryFragment(ft, geographicMarketFragment, CompanyCategory.GEOGRAPHIC_MARKET.getFragmentTag());
			}

			ft.commit();
		}
	}

	private void addCategoryFragment(FragmentTransaction ft, Fragment fragment, String tag)
	{
		int containerId;
		if (getActivityListener() != null && getActivityListener().getIsLargeScreen())
		{
			// Want to go First -> Second -> First -> Second
			containerId = currentlyAddedFragments.size() % 2 == 0 ? R.id.companyCategoriesFirstContainer : R.id.companyCategoriesSecondContainer;
		}
		else
		{
			// Just one container for phones
			containerId = R.id.companyCategoriesFirstContainer;
		}

		ft.add(containerId, fragment, tag);
		this.currentlyAddedFragments.add(tag);
	}

	private void removeCurrentlyAddedFragments()
	{
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		for (String tag : this.currentlyAddedFragments)
		{
			Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
			if (fragment != null)
			{
				ft.remove(fragment);
			}
		}

		ft.commit();
		getChildFragmentManager().executePendingTransactions();
		this.currentlyAddedFragments.clear();
	}
}

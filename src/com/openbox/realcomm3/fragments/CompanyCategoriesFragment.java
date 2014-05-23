package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.List;
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

public class CompanyCategoriesFragment extends BaseProfileFragment
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

		CompanyModel model = getCompany();
		if (model != null)
		{
			CategoriesFragment mainCategoriesFragment = getCategoriesFragment(CompanyCategory.MAIN_CATEGORY);
			if (model.getHasMainCategories() && mainCategoriesFragment == null)
			{
				mainCategoriesFragment = CategoriesFragment.newInstance(CompanyCategory.MAIN_CATEGORY);
				addCategoryFragment(mainCategoriesFragment, CompanyCategory.MAIN_CATEGORY.getFragmentTag());
			}

			CategoriesFragment subCategoriesFragment = getCategoriesFragment(CompanyCategory.SUB_CATEGORY);
			if (model.getHasSubCategories() && subCategoriesFragment == null)
			{
				subCategoriesFragment = CategoriesFragment.newInstance(CompanyCategory.SUB_CATEGORY);
				addCategoryFragment(subCategoriesFragment, CompanyCategory.SUB_CATEGORY.getFragmentTag());
			}

			CategoriesFragment targetMarketFragment = getCategoriesFragment(CompanyCategory.TARGET_MARKET);
			if (model.getHasTargetMarkets() && targetMarketFragment == null)
			{
				targetMarketFragment = CategoriesFragment.newInstance(CompanyCategory.TARGET_MARKET);
				addCategoryFragment(targetMarketFragment, CompanyCategory.TARGET_MARKET.getFragmentTag());
			}

			CategoriesFragment geographicMarketFragment = getCategoriesFragment(CompanyCategory.GEOGRAPHIC_MARKET);
			if (model.getHasGeographicMarkets() && geographicMarketFragment == null)
			{
				geographicMarketFragment = CategoriesFragment.newInstance(CompanyCategory.GEOGRAPHIC_MARKET);
				addCategoryFragment(geographicMarketFragment, CompanyCategory.GEOGRAPHIC_MARKET.getFragmentTag());
			}

			getChildFragmentManager().executePendingTransactions();
			if (this.currentlyAddedFragments.size() > 0)
			{
				int lastFragmentIndex = this.currentlyAddedFragments.size() - 1;
				CategoriesFragment lastFragment = (CategoriesFragment) getChildFragmentManager().findFragmentByTag(
					this.currentlyAddedFragments.get(lastFragmentIndex));
				if (lastFragment != null)
				{
					lastFragment.updateIsLast();
				}
			}
		}
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

	private CategoriesFragment getCategoriesFragment(CompanyCategory companyCategory)
	{
		return (CategoriesFragment) getChildFragmentManager().findFragmentByTag(companyCategory.getFragmentTag());
	}

	private void addCategoryFragment(Fragment fragment, String tag)
	{
		int containerId;
		if (getActivityInterface() != null && getActivityInterface().getIsLargeScreen())
		{
			// Want to go First -> Second -> First -> Second
			containerId = currentlyAddedFragments.size() % 2 == 0 ? R.id.companyCategoriesFirstContainer : R.id.companyCategoriesSecondContainer;
		}
		else
		{
			// Just one container for phones
			containerId = R.id.companyCategoriesFirstContainer;
		}

		getChildFragmentManager().beginTransaction().add(containerId, fragment, tag).commit();
		this.currentlyAddedFragments.add(tag);
	}
}

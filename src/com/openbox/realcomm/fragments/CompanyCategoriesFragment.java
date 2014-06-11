package com.openbox.realcomm.fragments;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.base.BaseProfileFragment;
import com.openbox.realcomm.database.models.CompanyModel;
import com.openbox.realcomm.utilities.enums.CompanyCategory;
import com.openbox.realcomm.R;

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
			CategoriesFragment lastFragment = null;
			CompanyCategory[] categories;
			
			if (RealCommApplication.getIsLargeScreen())
			{
				categories = CompanyCategory.getTabletSortedCompanyCategories();
			}
			else
			{
				categories = CompanyCategory.getPhoneSortedCompanyCategories();
			}
			
			for (int i = 0; i < categories.length; i++)
			{
				CategoriesFragment fragment = getCategoriesFragment(categories[i]);
				if (model.getHasCategories(categories[i]) && fragment == null)
				{
					fragment = CategoriesFragment.newInstance(categories[i]);
					addCategoryFragment(fragment, categories[i].getFragmentTag());
					
					if (i == categories.length - 1)
					{
						lastFragment = fragment;
					}
				}
			}
			
			getChildFragmentManager().executePendingTransactions();
			if (lastFragment != null)
			{
				lastFragment.updateIsLast();
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
		if (getActivityInterface() != null && RealCommApplication.getIsLargeScreen())
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

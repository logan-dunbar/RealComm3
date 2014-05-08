package com.openbox.realcomm3.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.database.models.CompanyModel;
import com.openbox.realcomm3.database.models.SelectedBoothModel;
import com.openbox.realcomm3.database.objects.Booth;
import com.openbox.realcomm3.database.objects.Company;
import com.openbox.realcomm3.utilities.interfaces.ProfilePageInterface;
import com.openbox.realcomm3.utilities.loaders.CompanyModelLoader;

public class ProfilePageFragment extends BaseFragment implements ProfilePageInterface
{
	public static final String TAG = "profilePageFragment";
	private static final int COMPANY_LOADER_ID = 1;

	private static final int CATEGORIES_INDEX = 1;

	private static final int ADDRESS_INDEX = 0;
	private static final int CONTACTS_INDEX = 1;
	private static final int LINKS_INDEX = 2;
	private static final int SOCIAL_NETWORKS_INDEX = 3;

	private CompanyModel companyModel;

	private LinearLayout companyDetailsAndCategoriesLayout;
	private LinearLayout companyAddressContactsAndLinksLayout;

	public static ProfilePageFragment newInstance(SelectedBoothModel selectedBooth)
	{
		ProfilePageFragment fragment = new ProfilePageFragment();

		Bundle args = new Bundle();
		args.putInt(Booth.BOOTH_ID_COLUMN_NAME, selectedBooth.getBoothId());
		args.putInt(Company.COMPANY_ID_COLUMN_NAME, selectedBooth.getCompanyId());
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		initCompanyLoader();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

		this.companyDetailsAndCategoriesLayout = (LinearLayout) view.findViewById(R.id.companyDetailsAndCategoriesLayout);
		this.companyAddressContactsAndLinksLayout = (LinearLayout) view.findViewById(R.id.companyAddressContactsAndLinksLayout);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// TODO might move all
	}

	@Override
	public CompanyModel getCompany()
	{
		return this.companyModel;
	}

	private void initCompanyLoader()
	{
		getLoaderManager().initLoader(COMPANY_LOADER_ID, null, this.companyLoaderCallbacks);
	}

	private void finishCompanyLoad(CompanyModel results)
	{
		this.companyModel = results;

		if (this.companyModel != null)
		{
			createCompanyDetailsFragment();
			createCompanyCategoriesFragment();

			createCompanyAddressFragment();
			createCompanyContactsFragment();
			createCompanyLinksFragment();
			createCompanySocialNetworksFragment();

			addSectionBorders(this.companyDetailsAndCategoriesLayout);
			addSectionBorders(this.companyAddressContactsAndLinksLayout);
		}
	}

	private void createCompanyDetailsFragment()
	{
		CompanyDetailsFragment fragment = (CompanyDetailsFragment) getChildFragmentManager().findFragmentById(R.id.companyDetailsContainer);
		if (fragment == null)
		{
			fragment = CompanyDetailsFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.companyDetailsContainer, fragment).commit();
		}
	}

	private void createCompanyCategoriesFragment()
	{
		if (this.companyModel.getHasCategories())
		{
			CompanyCategoriesFragment fragment = (CompanyCategoriesFragment) getChildFragmentManager().findFragmentById(R.id.companyCategoriesContainer);
			if (fragment == null)
			{
				fragment = CompanyCategoriesFragment.newInstance();
				getChildFragmentManager().beginTransaction().add(R.id.companyCategoriesContainer, fragment).commit();
			}
		}
		else
		{
			this.companyDetailsAndCategoriesLayout.removeViewAt(CATEGORIES_INDEX);
		}
	}

	private void createCompanyAddressFragment()
	{
		// TODO check if has address

		if (true)
		{
			CompanyAddressFragment fragment = (CompanyAddressFragment) getChildFragmentManager().findFragmentById(R.id.companyAddressContainer);
			if (fragment == null)
			{
				fragment = CompanyAddressFragment.newInstance();
				getChildFragmentManager().beginTransaction().add(R.id.companyAddressContainer, fragment).commit();
			}
		}
		else
		{
			this.companyAddressContactsAndLinksLayout.removeViewAt(ADDRESS_INDEX);
		}
	}

	private void createCompanyContactsFragment()
	{
		if (true)
		{
			CompanyContactsFragment fragment = (CompanyContactsFragment) getChildFragmentManager().findFragmentById(R.id.companyContactsContainer);
			if (fragment == null)
			{
				fragment = CompanyContactsFragment.newInstance();
				getChildFragmentManager().beginTransaction().add(R.id.companyContactsContainer, fragment).commit();
			}
		}
		else
		{
			this.companyAddressContactsAndLinksLayout.removeViewAt(CONTACTS_INDEX);
		}
	}

	private void createCompanyLinksFragment()
	{
		// TODO check if links

		if (true)
		{
			CompanyLinksFragment fragment = (CompanyLinksFragment) getChildFragmentManager().findFragmentById(R.id.companyLinksContainer);
			if (fragment == null)
			{
				fragment = CompanyLinksFragment.newInstance();
				getChildFragmentManager().beginTransaction().add(R.id.companyLinksContainer, fragment).commit();
			}
		}
		else
		{
			this.companyAddressContactsAndLinksLayout.removeViewAt(LINKS_INDEX);
		}
	}

	private void createCompanySocialNetworksFragment()
	{
		if (this.companyModel.getHasSocialNetworks())
		{
			CompanySocialNetworksFragment fragment = (CompanySocialNetworksFragment) getChildFragmentManager()
				.findFragmentById(R.id.companySocialNetworksContainer);
			if (fragment == null)
			{
				fragment = CompanySocialNetworksFragment.newInstance();
				getChildFragmentManager().beginTransaction().add(R.id.companySocialNetworksContainer, fragment).commit();
			}
		}
		else
		{
			this.companyAddressContactsAndLinksLayout.removeViewAt(SOCIAL_NETWORKS_INDEX);
		}
	}

	private void addSectionBorders(LinearLayout section)
	{
		int childCount = section.getChildCount();
		int index = 1;
		for (int i = 0; i < childCount - 1; i++)
		{
			section.addView(getBorderView(), index);
			index += 2;
		}
	}

	private View getBorderView()
	{
		View view = new View(getActivity());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			(int) getResources().getDimension(R.dimen.borderWidth));
		view.setLayoutParams(params);
		view.setBackgroundColor(getResources().getColor(R.color.border_grey));

		return view;
	}

	private LoaderCallbacks<CompanyModel> companyLoaderCallbacks = new LoaderCallbacks<CompanyModel>()
	{
		@Override
		public Loader<CompanyModel> onCreateLoader(int loaderId, Bundle bundle)
		{
			int boothId = getArguments().getInt(Booth.BOOTH_ID_COLUMN_NAME);
			int companyId = getArguments().getInt(Company.COMPANY_ID_COLUMN_NAME);
			return new CompanyModelLoader(getActivity(), boothId, companyId);
		}

		@Override
		public void onLoadFinished(Loader<CompanyModel> loader, CompanyModel results)
		{
			finishCompanyLoad(results);
		}

		@Override
		public void onLoaderReset(Loader<CompanyModel> loader)
		{
		}
	};
}

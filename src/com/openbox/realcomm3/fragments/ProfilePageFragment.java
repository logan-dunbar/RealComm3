package com.openbox.realcomm3.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;
import com.openbox.realcomm3.database.models.SelectedBoothModel;
import com.openbox.realcomm3.database.objects.Booth;
import com.openbox.realcomm3.database.objects.Company;
import com.openbox.realcomm3.utilities.interfaces.ProfileDataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.ProfilePageInterface;
import com.openbox.realcomm3.utilities.loaders.CompanyModelLoader;

public class ProfilePageFragment extends BaseProfileFragment implements ProfilePageInterface
{
	public static final String TAG = "profilePageFragment";
	public static final String SELECTED_BOOTH_MODEL_KEY = "selectedBoothModelKey";
	private static final int COMPANY_LOADER_ID = 1;

	private SelectedBoothModel selectedBoothModel;
	private CompanyModel companyModel;

	private List<ProfileDataChangedCallbacks> profileDataChangedListeners = new ArrayList<>();

	private ScrollView profilePageScrollView;
	private FrameLayout companyCategoriesBorder;
	private FrameLayout companyAddressBorder;
	private FrameLayout companyContactsBorder;
	private FrameLayout companyLinksBorder;
	private FrameLayout companySocialNetworksBorder;

	public static ProfilePageFragment newInstance()
	{
		ProfilePageFragment fragment = new ProfilePageFragment();

		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && this.selectedBoothModel == null)
		{
			this.selectedBoothModel = (SelectedBoothModel) savedInstanceState.getSerializable(SELECTED_BOOTH_MODEL_KEY);
		}

		startCompanyLoader();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putSerializable(SELECTED_BOOTH_MODEL_KEY, this.selectedBoothModel);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

		this.profilePageScrollView = (ScrollView) view.findViewById(R.id.profilePageScrollView);
		this.companyCategoriesBorder = (FrameLayout) view.findViewById(R.id.companyCategoriesBorder);
		this.companyAddressBorder = (FrameLayout) view.findViewById(R.id.companyAddressBorder);
		this.companyContactsBorder = (FrameLayout) view.findViewById(R.id.companyContactsBorder);
		this.companyLinksBorder = (FrameLayout) view.findViewById(R.id.companyLinksBorder);
		this.companySocialNetworksBorder = (FrameLayout) view.findViewById(R.id.companySocialNetworksBorder);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		createCompanyDetailsFragment();
		createCompanyCategoriesFragment();

		createCompanyAddressFragment();
		createCompanyContactsFragment();
		createCompanyLinksFragment();
		createCompanySocialNetworksFragment();
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.profileDataChangedListeners.clear();
	}

	/**********************************************************************************************
	 * Profile Page Interface Implements
	 **********************************************************************************************/
	@Override
	public CompanyModel getCompany()
	{
		return this.companyModel;
	}

	/**********************************************************************************************
	 * Public Methods
	 **********************************************************************************************/
	public void updateProfilePage(SelectedBoothModel selectedBoothModel)
	{
		if (this.profilePageScrollView != null)
		{
			this.profilePageScrollView.scrollTo(0, 0);
		}

		this.selectedBoothModel = selectedBoothModel;
		startCompanyLoader();
	}

	/**********************************************************************************************
	 * Private Helper Methods
	 **********************************************************************************************/
	private void startCompanyLoader()
	{
		if (this.selectedBoothModel != null)
		{
			Bundle args = new Bundle();
			args.putInt(Booth.BOOTH_ID_COLUMN_NAME, this.selectedBoothModel.getBoothId());
			args.putInt(Company.COMPANY_ID_COLUMN_NAME, this.selectedBoothModel.getCompanyId());
			getLoaderManager().restartLoader(COMPANY_LOADER_ID, args, this.companyLoaderCallbacks);
		}
	}

	private void finishCompanyLoad(CompanyModel results)
	{
		this.companyModel = results;
		setFragmentVisibilities();

		for (ProfileDataChangedCallbacks listener : this.profileDataChangedListeners)
		{
			listener.onCompanyLoaded();
		}
	}

	private void setFragmentVisibilities()
	{
		if (this.companyModel != null)
		{
			FragmentTransaction ft = getChildFragmentManager().beginTransaction();

			CompanyCategoriesFragment categoriesFragment = (CompanyCategoriesFragment) getChildFragmentManager().findFragmentById(
				R.id.companyCategoriesContainer);
			CompanyAddressFragment addressFragment = (CompanyAddressFragment) getChildFragmentManager().findFragmentById(R.id.companyAddressContainer);
			CompanyContactsFragment contactsFragment = (CompanyContactsFragment) getChildFragmentManager().findFragmentById(R.id.companyContactsContainer);
			CompanyLinksFragment linksFragment = (CompanyLinksFragment) getChildFragmentManager().findFragmentById(R.id.companyLinksContainer);
			CompanySocialNetworksFragment socialNetworksFragment = (CompanySocialNetworksFragment) getChildFragmentManager()
				.findFragmentById(R.id.companySocialNetworksContainer);

			resetBorderVisibilities();

			boolean fragmentAboveIsVisible = false;

			if (categoriesFragment != null && this.companyModel.getHasCategories())
			{
				ft.show(categoriesFragment);
				this.companyCategoriesBorder.setVisibility(View.VISIBLE);
				if (getActivityInterface() != null && !RealCommApplication.getIsLargeScreen())
				{
					fragmentAboveIsVisible = true;
				}
			}
			else
			{
				ft.hide(categoriesFragment);
			}

			if (addressFragment != null && this.companyModel.getHasAddress())
			{
				ft.show(addressFragment);
				if (fragmentAboveIsVisible &&
					this.companyContactsBorder != null &&
					getActivityInterface() != null && !RealCommApplication.getIsLargeScreen())
				{
					this.companyAddressBorder.setVisibility(View.VISIBLE);
				}

				fragmentAboveIsVisible = true;
			}
			else
			{
				ft.hide(addressFragment);
			}

			if (contactsFragment != null && this.companyModel.getHasContacts())
			{
				ft.show(contactsFragment);
				if (fragmentAboveIsVisible)
				{
					this.companyContactsBorder.setVisibility(View.VISIBLE);
				}

				fragmentAboveIsVisible = true;
			}
			else
			{
				ft.hide(contactsFragment);
			}

			if (linksFragment != null && this.companyModel.getHasWebsite())
			{
				ft.show(linksFragment);
				if (fragmentAboveIsVisible)
				{
					this.companyLinksBorder.setVisibility(View.VISIBLE);
				}

				fragmentAboveIsVisible = true;
			}
			else
			{
				ft.hide(linksFragment);
			}

			if (socialNetworksFragment != null && this.companyModel.getHasSocialNetworks())
			{
				ft.show(socialNetworksFragment);
				if (fragmentAboveIsVisible)
				{
					this.companySocialNetworksBorder.setVisibility(View.VISIBLE);
				}
			}
			else
			{
				ft.hide(socialNetworksFragment);
			}

			ft.commit();
		}
	}

	private void resetBorderVisibilities()
	{
		this.companyCategoriesBorder.setVisibility(View.GONE);
		this.companyContactsBorder.setVisibility(View.GONE);
		this.companyLinksBorder.setVisibility(View.GONE);
		this.companySocialNetworksBorder.setVisibility(View.GONE);
		if (this.companyAddressBorder != null)
		{
			// Not present in the tablet version
			this.companyAddressBorder.setVisibility(View.GONE);
		}
	}

	private void createCompanyDetailsFragment()
	{
		CompanyDetailsFragment fragment = (CompanyDetailsFragment) getChildFragmentManager().findFragmentById(R.id.companyDetailsContainer);
		if (fragment == null)
		{
			fragment = CompanyDetailsFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.companyDetailsContainer, fragment).commit();
			getDataChangedListeners().add(fragment);
			this.profileDataChangedListeners.add(fragment);
		}
	}

	private void createCompanyCategoriesFragment()
	{
		CompanyCategoriesFragment fragment = (CompanyCategoriesFragment) getChildFragmentManager().findFragmentById(R.id.companyCategoriesContainer);
		if (fragment == null)
		{
			fragment = CompanyCategoriesFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.companyCategoriesContainer, fragment).hide(fragment).commit();
			this.profileDataChangedListeners.add(fragment);
		}
	}

	private void createCompanyAddressFragment()
	{
		CompanyAddressFragment fragment = (CompanyAddressFragment) getChildFragmentManager().findFragmentById(R.id.companyAddressContainer);
		if (fragment == null)
		{
			fragment = CompanyAddressFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.companyAddressContainer, fragment).hide(fragment).commit();
			this.profileDataChangedListeners.add(fragment);
		}
	}

	private void createCompanyContactsFragment()
	{
		CompanyContactsFragment fragment = (CompanyContactsFragment) getChildFragmentManager().findFragmentById(R.id.companyContactsContainer);
		if (fragment == null)
		{
			fragment = CompanyContactsFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.companyContactsContainer, fragment).hide(fragment).commit();
			this.profileDataChangedListeners.add(fragment);
		}
	}

	private void createCompanyLinksFragment()
	{
		CompanyLinksFragment fragment = (CompanyLinksFragment) getChildFragmentManager().findFragmentById(R.id.companyLinksContainer);
		if (fragment == null)
		{
			fragment = CompanyLinksFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.companyLinksContainer, fragment).hide(fragment).commit();
			this.profileDataChangedListeners.add(fragment);
		}
	}

	private void createCompanySocialNetworksFragment()
	{
		CompanySocialNetworksFragment fragment = (CompanySocialNetworksFragment) getChildFragmentManager()
			.findFragmentById(R.id.companySocialNetworksContainer);
		if (fragment == null)
		{
			fragment = CompanySocialNetworksFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.companySocialNetworksContainer, fragment).hide(fragment).commit();
			this.profileDataChangedListeners.add(fragment);
		}
	}

	private LoaderCallbacks<CompanyModel> companyLoaderCallbacks = new LoaderCallbacks<CompanyModel>()
	{
		@Override
		public Loader<CompanyModel> onCreateLoader(int loaderId, Bundle bundle)
		{
			int boothId = bundle.getInt(Booth.BOOTH_ID_COLUMN_NAME);
			int companyId = bundle.getInt(Company.COMPANY_ID_COLUMN_NAME);
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

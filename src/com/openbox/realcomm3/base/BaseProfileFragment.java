package com.openbox.realcomm3.base;

import com.openbox.realcomm3.database.models.CompanyModel;
import com.openbox.realcomm3.utilities.interfaces.ProfileDataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.ProfilePageInterface;

import android.os.Bundle;

public abstract class BaseProfileFragment extends BaseFragment implements ProfileDataChangedCallbacks, ProfilePageInterface
{
	private ProfilePageInterface profilePageInterface;

	public ProfilePageInterface getProfilePageInterface()
	{
		return profilePageInterface;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (getParentFragment() instanceof ProfilePageInterface)
		{
			this.profilePageInterface = (ProfilePageInterface) getParentFragment();
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.profilePageInterface = null;
	}

	@Override
	public CompanyModel getCompany()
	{
		if (this.profilePageInterface != null)
		{
			return this.profilePageInterface.getCompany();
		}

		return null;
	}

	@Override
	public void onCompanyLoaded()
	{
		// Should override to get notified when company finished loading.
	}

}

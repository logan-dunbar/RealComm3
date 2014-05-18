package com.openbox.realcomm3.base;

import com.openbox.realcomm3.database.models.CompanyModel;
import com.openbox.realcomm3.utilities.interfaces.ProfilePageInterface;

import android.os.Bundle;

public class BaseProfileFragment extends BaseFragment
{
	private ProfilePageInterface profilePageInterface;

	public ProfilePageInterface getProfilePageInterface()
	{
		return profilePageInterface;
	}

	public CompanyModel getCompanyModel()
	{
		if (profilePageInterface != null)
		{
			return profilePageInterface.getCompany();
		}

		return null;
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
}

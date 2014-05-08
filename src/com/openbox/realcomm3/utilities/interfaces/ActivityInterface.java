package com.openbox.realcomm3.utilities.interfaces;

import com.openbox.realcomm3.utilities.enums.RealcommPage;

public interface ActivityInterface
{
	// Page related
	void setSelectedBooth(int boothId, int companyId);

	void changePage(RealcommPage page);

	// Realcomm Page Manager related
	void showSplashScreenFragment();

	void hideSplashScreenFragmentAndShowListingPageFragment();

	void showListingPageFragmentAndRemoveProfileFragment();

	void addProfilePageAndHideListingPage();

	void onSplashScreenAnimationInComplete();

	void onSplashScreenAnimationOutComplete();
}

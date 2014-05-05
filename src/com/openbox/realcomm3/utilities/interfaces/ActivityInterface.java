package com.openbox.realcomm3.utilities.interfaces;

import com.openbox.realcomm3.utilities.enums.RealcommPage;

public interface ActivityInterface
{
	// Fragment related
	void onFragmentViewCreated();

	void changePage(RealcommPage page);

	// Realcomm Page Manager related
	void showSplashScreenFragment();
	
	void hideSplashScreenFragmentAndShowListingPageFragment();
	
	void showListingPageFragmentAndRemoveProfileFragment();
	
	void addProfilePageAndHideListingPage();

	void onSplashScreenAnimationInComplete();

	void onSplashScreenAnimationOutComplete();
}

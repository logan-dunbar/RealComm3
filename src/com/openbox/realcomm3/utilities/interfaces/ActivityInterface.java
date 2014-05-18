package com.openbox.realcomm3.utilities.interfaces;

import com.openbox.realcomm3.utilities.enums.RealcommPage;
import com.openbox.realcomm3.utilities.enums.RealcommPhonePage;

public interface ActivityInterface
{
	// Page related
	boolean getIsLargeScreen();
	
	void setSelectedBooth(int boothId, int companyId);

	void changePage(RealcommPage page);

	void changePage(RealcommPhonePage page);

	// General Page Manager related
	void showSplashScreenFragment();
	
	void initializeFragments();

	// Realcomm Page Manager related
	void showListingPageAndRemoveSplashScreen();

	void showListingPageAndHideProfilePage();

	void showProfilePageAndHideListingPage();

	// Realcomm Phone Page Manager related
	void showBoothExploreAndRemoveSplashScreen();

	void showBoothExploreAndHideBoothList();

	void showBoothExploreAndRemoveProfilePage();

	void showBoothExploreAndHideSchedulePage();

	void showBoothListAndHideBoothExplore();

	void showBoothListAndHideProfilePage();

	void showBoothListAndHideSchedulePage();

	void showProfilePageAndHideBoothExplore();

	void showProfilePageAndHideBoothList();

	void showSchedulePageAndHideBoothExplore();

	void showSchedulePageAndHideBoothList();
	
	void initNavigationDrawer();
	
	void showNavigationDrawer();
	
	void hideNavigationDrawer();
	
	void selectPageButton();

	// Splash Animation related
	void onSplashScreenAnimationInComplete();

	void onSplashScreenAnimationOutComplete();
}

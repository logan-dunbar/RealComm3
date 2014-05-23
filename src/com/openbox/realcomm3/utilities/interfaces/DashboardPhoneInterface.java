package com.openbox.realcomm3.utilities.interfaces;

import com.openbox.realcomm3.utilities.enums.RealcommPhonePage;

public interface DashboardPhoneInterface
{
	// Page related
	boolean onBackBressed();
	
	// Page related
	void changePage(RealcommPhonePage page);

	// Realcomm Phone Page Manager related
	void showBoothExploreFromInitializing();

	void showBoothExploreAndHideBoothList();

	void showBoothExploreAndHideSchedulePage();

	void showBoothListAndHideBoothExplore();

	void showBoothListAndHideSchedulePage();

	void showSchedulePageAndHideBoothExplore();

	void showSchedulePageAndHideBoothList();

	// void showNavigationDrawer();
	//
	// void hideNavigationDrawer();

	void selectPageButton();
}

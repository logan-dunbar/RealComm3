package com.openbox.realcomm.utilities.interfaces;

import com.openbox.realcomm.utilities.enums.RealcommPhonePage;

public interface DashboardPhoneInterface
{
	// Page related
	boolean onBackPressed();

	// Page related
	void changePage(RealcommPhonePage page);

	// Realcomm Phone Page Manager related
	void showBoothExploreFromInitializing();

	void showBoothExploreAndHideBoothList();

	void showBoothExploreAndHideSchedulePage();

	void showBoothExploreAndHideInfoPage();

	void showBoothListAndHideBoothExplore();

	void showBoothListAndHideSchedulePage();

	void showBoothListAndHideInfoPage();

	void showSchedulePageAndHideBoothExplore();

	void showSchedulePageAndHideBoothList();

	void showSchedulePageAndHideInfoPage();

	void showInfoPageAndHideBoothExplore();

	void showInfoPageAndHideBoothList();

	void showInfoPageAndHideSchedulePage();

	void selectPageButton();
}

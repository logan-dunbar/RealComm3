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

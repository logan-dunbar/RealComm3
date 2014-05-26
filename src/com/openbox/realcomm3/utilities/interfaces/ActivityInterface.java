package com.openbox.realcomm3.utilities.interfaces;

import com.openbox.realcomm3.utilities.enums.RealcommPage;

public interface ActivityInterface
{
	// Page related
	void setSelectedBooth(int boothId, int companyId);

	void changePage(RealcommPage page);

	// General Page Manager related
	void showSplashScreenFragment();

	void initializeFragments();

	// Realcomm Page Manager related
	void showDashboardAndRemoveSplashScreen();

	void showDashboardAndHideProfilePage();

	void showProfilePageAndHideDashboard();
}

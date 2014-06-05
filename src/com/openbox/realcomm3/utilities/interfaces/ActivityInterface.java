package com.openbox.realcomm3.utilities.interfaces;

import com.openbox.realcomm3.utilities.enums.RealcommPage;

public interface ActivityInterface
{
	// Page related
	void setSelectedBooth(int boothId, int companyId);

	void changePage(RealcommPage page);

	// General Page Manager related
	void showSplashScreen();

	void initializeFragments();
	
	void showSplashScreenFragment();

	// Realcomm Page Manager related
	void showDashboardAndRemoveSplashScreen();

	void showDashboardAndHideProfilePage();

	void showProfilePageAndHideDashboard();
	
	// App Mode Manager related
	void initBeaconManager();
	
	void bindBeaconManager();
	
	void unbindBeaconManager();
}

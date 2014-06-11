package com.openbox.realcomm.utilities.interfaces;

import com.openbox.realcomm.utilities.enums.RealcommPage;

public interface ActivityInterface
{
	// Page related
	void setSelectedBooth(int boothId, int companyId);

	void changePage(RealcommPage page);

	// Realcomm Page Manager related
	void setInfoMenuItemVisibility(boolean visible);
	
	void showSplashScreen();

	void initializeFragments();

	void showSplashScreenFragment();

	void showDashboardAndRemoveSplashScreen();

	void showDashboardAndHideProfilePage();

	void showProfilePageAndHideDashboard();

	// App Mode Manager related
	void initBeaconManager();

	void bindBeaconManager();

	void unbindBeaconManager();

	// Booth Flipper related
	void startViewTimer();
	
	void stopViewTimer();
}

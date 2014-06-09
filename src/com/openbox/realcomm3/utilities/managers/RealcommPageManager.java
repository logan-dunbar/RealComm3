package com.openbox.realcomm3.utilities.managers;

import com.openbox.realcomm3.utilities.enums.RealcommPage;
import com.openbox.realcomm3.utilities.interfaces.ActivityInterface;

public class RealcommPageManager
{
	private RealcommPage currentPage;
	private ActivityInterface activityListener;

	public RealcommPage getCurrentPage()
	{
		return this.currentPage;
	}

	public RealcommPageManager(RealcommPage currentPage, ActivityInterface activityListener)
	{
		this.currentPage = currentPage;
		this.activityListener = activityListener;
	}

	public void changePage(RealcommPage newPage)
	{
		switch (newPage)
		{
			case INITIALIZING:
				// Should not get here
				break;
			case SPLASH_SCREEN:
				changePageToSplashScreen(newPage);
				break;
			case DASHBOARD_PAGE:
				changePageToDashboard(newPage);
				break;
			case PROFILE_PAGE:
				changePageToProfilePage(newPage);
				break;
			default:
				break;
		}
	}

	private void changePageToSplashScreen(RealcommPage newPage)
	{
		switch (this.currentPage)
		{
			case INITIALIZING:
				changeInitializingToSplashScreen();
				changePage(newPage);
				break;
			case SPLASH_SCREEN:
				// Stay here
				break;
			case DASHBOARD_PAGE:
				// Shouldn't need this
				break;
			case PROFILE_PAGE:
				// Shouldn't need this
				break;
			default:
				break;
		}
	}

	private void changePageToDashboard(RealcommPage newPage)
	{
		switch (this.currentPage)
		{
			case INITIALIZING:
				// Shouldn't need this
				break;
			case SPLASH_SCREEN:
				changeSplashScreenToDashboard();
				changePage(newPage);
				break;
			case DASHBOARD_PAGE:
				// Stay here
				break;
			case PROFILE_PAGE:
				changeProfilePageToDashboard();
				changePage(newPage);
				break;
			default:
				break;
		}
	}

	private void changePageToProfilePage(RealcommPage newPage)
	{
		switch (this.currentPage)
		{
			case INITIALIZING:
				// Shouldn't need this
				break;
			case SPLASH_SCREEN:
				// Shouldn't need this
				break;
			case DASHBOARD_PAGE:
				changeDashboardToProfilePage();
				changePage(newPage);
				break;
			case PROFILE_PAGE:
				// Stay here
				break;
			default:
				break;
		}
	}

	private void changeInitializingToSplashScreen()
	{
		if (this.activityListener != null)
		{
			this.activityListener.showSplashScreenFragment();
			this.activityListener.initializeFragments();
		}

		this.currentPage = RealcommPage.SPLASH_SCREEN;
	}

	private void changeSplashScreenToDashboard()
	{
		if (this.activityListener != null)
		{
			this.activityListener.showDashboardAndRemoveSplashScreen();
		}

		this.currentPage = RealcommPage.DASHBOARD_PAGE;
	}

	private void changeProfilePageToDashboard()
	{
		if (this.activityListener != null)
		{
			this.activityListener.showDashboardAndHideProfilePage();
		}

		this.currentPage = RealcommPage.DASHBOARD_PAGE;
	}

	private void changeDashboardToProfilePage()
	{
		if (this.activityListener != null)
		{
			this.activityListener.setInfoMenuItemVisibility(false);
			this.activityListener.showProfilePageAndHideDashboard();
		}

		this.currentPage = RealcommPage.PROFILE_PAGE;
	}

	// private void template()
	// {
	// switch (this.currentPage)
	// {
	// case Initializing:
	// break;
	// case SplashScreen:
	// break;
	// case ListingPage:
	// break;
	// case ProfilePage:
	// break;
	// default:
	// break;
	// }
	// }
}

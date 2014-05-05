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

	public void backPressed()
	{
		if (this.currentPage == RealcommPage.ProfilePage)
		{
			// To handle popping the back stack
			this.currentPage = RealcommPage.ListingPage;
		}
	}

	public void changePage(RealcommPage newPage)
	{
		switch (newPage)
		{
			case Initializing:
				// Should not get here
				break;
			case SplashScreen:
				changePageToSplashScreen(newPage);
				break;
			case ListingPage:
				changePageToListingPage(newPage);
				break;
			case ProfilePage:
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
			case Initializing:
				changeInitializingToSplashScreen();
				changePage(newPage);
				break;
			case SplashScreen:
				// Stay here
				break;
			case ListingPage:
				// Shouldn't need this
				break;
			case ProfilePage:
				// Shouldn't need this
				break;
			default:
				break;
		}
	}

	private void changePageToListingPage(RealcommPage newPage)
	{
		switch (this.currentPage)
		{
			case Initializing:
				// Shouldn't need this
				break;
			case SplashScreen:
				changeSplashScreenToListingPage();
				changePage(newPage);
				break;
			case ListingPage:
				// Stay here
				break;
			case ProfilePage:
				changeProfilePageToListingPage();
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
			case Initializing:
				// Shouldn't need this
				break;
			case SplashScreen:
				// Shouldn't need this
				break;
			case ListingPage:
				changeListingPageToProfilePage();
				changePage(newPage);
				break;
			case ProfilePage:
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
		}

		this.currentPage = RealcommPage.SplashScreen;
	}

	private void changeSplashScreenToListingPage()
	{
		if (this.activityListener != null)
		{
			this.activityListener.hideSplashScreenFragmentAndShowListingPageFragment();
		}

		this.currentPage = RealcommPage.ListingPage;
	}

	private void changeProfilePageToListingPage()
	{
		if (this.activityListener != null)
		{
			this.activityListener.showListingPageFragmentAndRemoveProfileFragment();
		}

		this.currentPage = RealcommPage.ListingPage;
	}

	private void changeListingPageToProfilePage()
	{
		if (this.activityListener != null)
		{
			this.activityListener.addProfilePageAndHideListingPage();
		}

		this.currentPage = RealcommPage.ProfilePage;
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

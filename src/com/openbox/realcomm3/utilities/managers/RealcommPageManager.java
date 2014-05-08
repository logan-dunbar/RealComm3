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
		if (this.currentPage == RealcommPage.PROFILEPAGE)
		{
			// To handle popping the back stack
			this.currentPage = RealcommPage.LISTINGPAGE;
		}
	}

	public void changePage(RealcommPage newPage)
	{
		switch (newPage)
		{
			case INITIALIZING:
				// Should not get here
				break;
			case SPLASHSCREEN:
				changePageToSplashScreen(newPage);
				break;
			case LISTINGPAGE:
				changePageToListingPage(newPage);
				break;
			case PROFILEPAGE:
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
			case SPLASHSCREEN:
				// Stay here
				break;
			case LISTINGPAGE:
				// Shouldn't need this
				break;
			case PROFILEPAGE:
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
			case INITIALIZING:
				// Shouldn't need this
				break;
			case SPLASHSCREEN:
				changeSplashScreenToListingPage();
				changePage(newPage);
				break;
			case LISTINGPAGE:
				// Stay here
				break;
			case PROFILEPAGE:
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
			case INITIALIZING:
				// Shouldn't need this
				break;
			case SPLASHSCREEN:
				// Shouldn't need this
				break;
			case LISTINGPAGE:
				changeListingPageToProfilePage();
				changePage(newPage);
				break;
			case PROFILEPAGE:
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

		this.currentPage = RealcommPage.SPLASHSCREEN;
	}

	private void changeSplashScreenToListingPage()
	{
		if (this.activityListener != null)
		{
			this.activityListener.hideSplashScreenFragmentAndShowListingPageFragment();
		}

		this.currentPage = RealcommPage.LISTINGPAGE;
	}

	private void changeProfilePageToListingPage()
	{
		if (this.activityListener != null)
		{
			this.activityListener.showListingPageFragmentAndRemoveProfileFragment();
		}

		this.currentPage = RealcommPage.LISTINGPAGE;
	}

	private void changeListingPageToProfilePage()
	{
		if (this.activityListener != null)
		{
			this.activityListener.addProfilePageAndHideListingPage();
		}

		this.currentPage = RealcommPage.PROFILEPAGE;
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

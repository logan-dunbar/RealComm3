package com.openbox.realcomm3.utilities.managers;

import com.openbox.realcomm3.utilities.enums.RealcommPhonePage;
import com.openbox.realcomm3.utilities.interfaces.ActivityInterface;

public class RealcommPhonePageManager
{
	private RealcommPhonePage currentPage;
	private RealcommPhonePage previousPage;
	private ActivityInterface activityListener;

	public RealcommPhonePage getCurrentPage()
	{
		return currentPage;
	}

	public RealcommPhonePage getPreviousPage()
	{
		return previousPage;
	}

	public void setPreviousPage(RealcommPhonePage previousPage)
	{
		this.previousPage = previousPage;
	}

	public RealcommPhonePageManager(RealcommPhonePage currentPage, ActivityInterface activityListener)
	{
		this.currentPage = currentPage;
		this.activityListener = activityListener;
	}

	public void changePage(RealcommPhonePage newPage)
	{
		switch (newPage)
		{
			case INITIALIZING:
				// Should not get here
				break;
			case SPLASH_SCREEN:
				changePageToSplashScreen(newPage);
				break;
			case BOOTH_EXPLORE:
				changePageToBoothExplore(newPage);
				break;
			case BOOTH_LIST:
				changePageToBoothList(newPage);
				break;
			case PROFILE_PAGE:
				changePageToProfilePage(newPage);
				break;
			case SCHEDULE_PAGE:
				changePageToSchedulePage(newPage);
				break;
			default:
				break;
		}
	}

	private void changePageToSplashScreen(RealcommPhonePage newPage)
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
			case BOOTH_EXPLORE:
				// Shouldn't need this
				break;
			case BOOTH_LIST:
				// Shouldn't need this
				break;
			case PROFILE_PAGE:
				// Shouldn't need this
				break;
			case SCHEDULE_PAGE:
				// Shouldn't need this
				break;
			default:
				break;
		}
	}

	private void changePageToBoothExplore(RealcommPhonePage newPage)
	{
		switch (this.currentPage)
		{
			case INITIALIZING:
				// Shouldn't need this
				break;
			case SPLASH_SCREEN:
				changeSplashScreenToBoothExplore();
				changePage(newPage);
				break;
			case BOOTH_EXPLORE:
				// Stay here
				break;
			case BOOTH_LIST:
				changeBoothListToBoothExplore();
				changePage(newPage);
				break;
			case PROFILE_PAGE:
				changeProfilePageToBoothExplore();
				changePage(newPage);
				break;
			case SCHEDULE_PAGE:
				changeSchedulePageToBoothExplore();
				changePage(newPage);
				break;
			default:
				break;
		}
	}

	private void changePageToBoothList(RealcommPhonePage newPage)
	{
		switch (this.currentPage)
		{
			case INITIALIZING:
				// Shouldn't need this
				break;
			case SPLASH_SCREEN:
				// Shouldn't need this
				break;
			case BOOTH_EXPLORE:
				changeBoothExploreToBoothList();
				changePage(newPage);
				break;
			case BOOTH_LIST:
				// Stay here
				break;
			case PROFILE_PAGE:
				changeProfilePageToBoothList();
				changePage(newPage);
				break;
			case SCHEDULE_PAGE:
				changeSchedulePageToBoothList();
				changePage(newPage);
				break;
			default:
				break;
		}
	}

	private void changePageToProfilePage(RealcommPhonePage newPage)
	{
		switch (this.currentPage)
		{
			case INITIALIZING:
				// Shouldn't need this
				break;
			case SPLASH_SCREEN:
				// Shouldn't need this
				break;
			case BOOTH_EXPLORE:
				changeBoothExploreToProfilePage();
				changePage(newPage);
				break;
			case BOOTH_LIST:
				changeBoothListToProfilePage();
				changePage(newPage);
				break;
			case PROFILE_PAGE:
				// Stay here
				break;
			case SCHEDULE_PAGE:
				// Shouldn't need this
				break;
			default:
				break;
		}
	}

	private void changePageToSchedulePage(RealcommPhonePage newPage)
	{
		switch (this.currentPage)
		{
			case INITIALIZING:
				// Shouldn't need this
				break;
			case SPLASH_SCREEN:
				// Shouldn't need this
				break;
			case BOOTH_EXPLORE:
				changeBoothExploreToSchedulePage();
				changePage(newPage);
				break;
			case BOOTH_LIST:
				changeBoothListToSchedulePage();
				changePage(newPage);
				break;
			case PROFILE_PAGE:
				// Shouldn't need this
				break;
			case SCHEDULE_PAGE:
				// Stay here
				break;
			default:
				break;
		}
	}

	private void changeInitializingToSplashScreen()
	{
		this.currentPage = RealcommPhonePage.SPLASH_SCREEN;

		if (this.activityListener != null)
		{
			this.activityListener.showSplashScreenFragment();
			this.activityListener.initializeFragments();
		}
	}

	private void changeSplashScreenToBoothExplore()
	{
		this.currentPage = RealcommPhonePage.BOOTH_EXPLORE;

		if (this.activityListener != null)
		{
			this.activityListener.initNavigationDrawer();

			// TODO could potentially animate or do something here.
			// although the entire fragment will be animating... something to think about
			// wait for the animation to finish and then animate it in?
			this.activityListener.selectPageButton();
			this.activityListener.showNavigationDrawer();
			this.activityListener.showBoothExploreAndRemoveSplashScreen();
		}
	}

	private void changeBoothListToBoothExplore()
	{
		this.currentPage = RealcommPhonePage.BOOTH_EXPLORE;

		if (this.activityListener != null)
		{
			this.activityListener.selectPageButton();
			this.activityListener.showNavigationDrawer();
			this.activityListener.showBoothExploreAndHideBoothList();
		}
	}

	private void changeProfilePageToBoothExplore()
	{
		this.currentPage = RealcommPhonePage.BOOTH_EXPLORE;

		if (this.activityListener != null)
		{
			this.activityListener.selectPageButton();
			this.activityListener.showNavigationDrawer();
			this.activityListener.showBoothExploreAndRemoveProfilePage();
		}
	}

	private void changeSchedulePageToBoothExplore()
	{
		this.currentPage = RealcommPhonePage.BOOTH_EXPLORE;

		if (this.activityListener != null)
		{
			this.activityListener.selectPageButton();
			this.activityListener.showNavigationDrawer();
			this.activityListener.showBoothExploreAndHideSchedulePage();
		}
	}

	private void changeBoothExploreToBoothList()
	{
		this.currentPage = RealcommPhonePage.BOOTH_LIST;

		if (this.activityListener != null)
		{
			this.activityListener.selectPageButton();
			this.activityListener.showNavigationDrawer();
			this.activityListener.showBoothListAndHideBoothExplore();
		}
	}

	private void changeProfilePageToBoothList()
	{
		this.currentPage = RealcommPhonePage.BOOTH_LIST;

		if (this.activityListener != null)
		{
			this.activityListener.selectPageButton();
			this.activityListener.showNavigationDrawer();
			this.activityListener.showBoothListAndHideProfilePage();
		}
	}

	private void changeSchedulePageToBoothList()
	{
		this.currentPage = RealcommPhonePage.BOOTH_LIST;

		if (this.activityListener != null)
		{
			this.activityListener.selectPageButton();
			this.activityListener.showNavigationDrawer();
			this.activityListener.showBoothListAndHideSchedulePage();
		}
	}

	private void changeBoothExploreToProfilePage()
	{
		this.currentPage = RealcommPhonePage.PROFILE_PAGE;

		if (this.activityListener != null)
		{
			this.activityListener.hideNavigationDrawer();
			this.activityListener.showProfilePageAndHideBoothExplore();
		}
	}

	private void changeBoothListToProfilePage()
	{
		this.currentPage = RealcommPhonePage.PROFILE_PAGE;

		if (this.activityListener != null)
		{
			this.activityListener.hideNavigationDrawer();
			this.activityListener.showProfilePageAndHideBoothList();
		}
	}

	private void changeBoothExploreToSchedulePage()
	{
		this.currentPage = RealcommPhonePage.SCHEDULE_PAGE;

		if (this.activityListener != null)
		{
			this.activityListener.selectPageButton();
			this.activityListener.showNavigationDrawer();
			this.activityListener.showSchedulePageAndHideBoothExplore();
		}
	}

	private void changeBoothListToSchedulePage()
	{
		this.currentPage = RealcommPhonePage.SCHEDULE_PAGE;

		if (this.activityListener != null)
		{
			this.activityListener.selectPageButton();
			this.activityListener.showNavigationDrawer();
			this.activityListener.showSchedulePageAndHideBoothList();
		}
	}

	// private void template()
	// {
	// switch (this.currentPage)
	// {
	// case INITIALIZING:
	// break;
	// case SPLASH_SCREEN:
	// break;
	// case BOOTH_EXPLORE:
	// break;
	// case BOOTH_LIST:
	// break;
	// case PROFILE_PAGE:
	// break;
	// case SCHEDULE_PAGE:
	// break;
	// default:
	// break;
	// }
	// }
}

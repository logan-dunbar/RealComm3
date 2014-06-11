package com.openbox.realcomm.utilities.managers;

import com.openbox.realcomm.utilities.enums.RealcommPhonePage;
import com.openbox.realcomm.utilities.interfaces.DashboardPhoneInterface;

public class RealcommPhonePageManager
{
	private RealcommPhonePage currentPage;
	private RealcommPhonePage previousPage;
	private DashboardPhoneInterface dashboardPhoneListener;

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

	public RealcommPhonePageManager(RealcommPhonePage currentPage, DashboardPhoneInterface dashboardPhoneListener)
	{
		this.currentPage = currentPage;
		this.dashboardPhoneListener = dashboardPhoneListener;
	}

	public void changePage(RealcommPhonePage newPage)
	{
		switch (newPage)
		{
			case INITIALIZING:
				// Should not get here
				break;
			case BOOTH_EXPLORE:
				changePageToBoothExplore(newPage);
				break;
			case BOOTH_LIST:
				changePageToBoothList(newPage);
				break;
			case SCHEDULE_PAGE:
				changePageToSchedulePage(newPage);
				break;
			case INFO_PAGE:
				changePageToInfoPage(newPage);
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
				changeInitializingToBoothExplore();
				changePage(newPage);
				break;
			case BOOTH_EXPLORE:
				// Stay here
				break;
			case BOOTH_LIST:
				changeBoothListToBoothExplore();
				changePage(newPage);
				break;
			case SCHEDULE_PAGE:
				changeSchedulePageToBoothExplore();
				changePage(newPage);
				break;
			case INFO_PAGE:
				changeInfoPageToBoothExplore();
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
			case BOOTH_EXPLORE:
				changeBoothExploreToBoothList();
				changePage(newPage);
				break;
			case BOOTH_LIST:
				// Stay here
				break;
			case SCHEDULE_PAGE:
				changeSchedulePageToBoothList();
				changePage(newPage);
				break;
			case INFO_PAGE:
				changeInfoPageToBoothList();
				changePage(newPage);
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
			case BOOTH_EXPLORE:
				changeBoothExploreToSchedulePage();
				changePage(newPage);
				break;
			case BOOTH_LIST:
				changeBoothListToSchedulePage();
				changePage(newPage);
				break;
			case SCHEDULE_PAGE:
				// Stay here
				break;
			case INFO_PAGE:
				changeInfoPageToSchedulePage();
				changePage(newPage);
				break;
			default:
				break;
		}
	}

	private void changePageToInfoPage(RealcommPhonePage newPage)
	{
		switch (this.currentPage)
		{
			case INITIALIZING:
				// Shouldn't need this
				break;
			case BOOTH_EXPLORE:
				changeBoothExploreToInfoPage();
				changePage(newPage);
				break;
			case BOOTH_LIST:
				changeBoothListToInfoPage();
				changePage(newPage);
				break;
			case SCHEDULE_PAGE:
				changeSchedulePageToInfoPage();
				changePage(newPage);
				break;
			case INFO_PAGE:
				// Stay here
				break;
			default:
				break;
		}
	}

	private void changeInitializingToBoothExplore()
	{
		this.currentPage = RealcommPhonePage.BOOTH_EXPLORE;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showBoothExploreFromInitializing();
		}
	}

	private void changeBoothListToBoothExplore()
	{
		this.currentPage = RealcommPhonePage.BOOTH_EXPLORE;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showBoothExploreAndHideBoothList();
		}
	}

	private void changeSchedulePageToBoothExplore()
	{
		this.currentPage = RealcommPhonePage.BOOTH_EXPLORE;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showBoothExploreAndHideSchedulePage();
		}
	}

	private void changeInfoPageToBoothExplore()
	{
		this.currentPage = RealcommPhonePage.BOOTH_EXPLORE;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showBoothExploreAndHideInfoPage();
		}

	}

	private void changeBoothExploreToBoothList()
	{
		this.currentPage = RealcommPhonePage.BOOTH_LIST;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showBoothListAndHideBoothExplore();
		}
	}

	private void changeSchedulePageToBoothList()
	{
		this.currentPage = RealcommPhonePage.BOOTH_LIST;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showBoothListAndHideSchedulePage();
		}
	}

	private void changeInfoPageToBoothList()
	{
		this.currentPage = RealcommPhonePage.BOOTH_LIST;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showBoothListAndHideInfoPage();
		}

	}

	private void changeBoothExploreToSchedulePage()
	{
		this.currentPage = RealcommPhonePage.SCHEDULE_PAGE;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showSchedulePageAndHideBoothExplore();
		}
	}

	private void changeBoothListToSchedulePage()
	{
		this.currentPage = RealcommPhonePage.SCHEDULE_PAGE;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showSchedulePageAndHideBoothList();
		}
	}

	private void changeInfoPageToSchedulePage()
	{
		this.currentPage = RealcommPhonePage.SCHEDULE_PAGE;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showSchedulePageAndHideInfoPage();
		}
	}

	private void changeBoothExploreToInfoPage()
	{
		this.currentPage = RealcommPhonePage.INFO_PAGE;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showInfoPageAndHideBoothExplore();
		}
	}

	private void changeBoothListToInfoPage()
	{
		this.currentPage = RealcommPhonePage.INFO_PAGE;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showInfoPageAndHideBoothList();
		}
	}

	private void changeSchedulePageToInfoPage()
	{
		this.currentPage = RealcommPhonePage.INFO_PAGE;

		if (this.dashboardPhoneListener != null)
		{
			this.dashboardPhoneListener.selectPageButton();
			this.dashboardPhoneListener.showInfoPageAndHideSchedulePage();
		}
	}
}

package com.openbox.realcomm3.utilities.enums;

import java.util.Arrays;
import java.util.Comparator;

public enum CompanyCategory
{
	MAIN_CATEGORY("Main Categories", "mainCategoriesFragment", 1, 1),
	TARGET_MARKET("Target Markets", "targetMarketsFragment", 5, 3),
	SUB_CATEGORY("Sub Categories", "subCategoriesFragment", 3, 2),
	GEOGRAPHIC_MARKET("Geographic Markets", "geographicMarketsFragment", 2, 4),
	CLIENT_SAMPLING("Client Sampling", "clientSamplingFragment", 4, 5);

	private String header;
	private String fragmentTag;
	private int tabletOrder;
	private int phoneOrder;

	private CompanyCategory(String header, String fragmentTag, int tabletOrder, int phoneOrder)
	{
		this.header = header;
		this.fragmentTag = fragmentTag;
		this.tabletOrder = tabletOrder;
		this.phoneOrder = phoneOrder;
	}

	public String getHeader()
	{
		return header;
	}

	public String getFragmentTag()
	{
		return fragmentTag;
	}

	public int getTabletOrder()
	{
		return tabletOrder;
	}

	public int getPhoneOrder()
	{
		return phoneOrder;
	}

	public static CompanyCategory[] getPhoneSortedCompanyCategories()
	{
		CompanyCategory[] categories = CompanyCategory.values();
		Arrays.sort(categories, new Comparator<CompanyCategory>()
		{
			@Override
			public int compare(CompanyCategory lhs, CompanyCategory rhs)
			{
				return lhs.getPhoneOrder() - rhs.getPhoneOrder();
			}
		});

		return categories;
	}

	public static CompanyCategory[] getTabletSortedCompanyCategories()
	{
		CompanyCategory[] categories = CompanyCategory.values();
		Arrays.sort(categories, new Comparator<CompanyCategory>()
		{
			@Override
			public int compare(CompanyCategory lhs, CompanyCategory rhs)
			{
				return lhs.getTabletOrder() - rhs.getTabletOrder();
			}
		});

		return categories;
	}
}

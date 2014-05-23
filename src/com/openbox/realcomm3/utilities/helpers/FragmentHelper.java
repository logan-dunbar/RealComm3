package com.openbox.realcomm3.utilities.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class FragmentHelper
{
	public static void addFragment(FragmentManager fm, int containerId, Fragment fragment, String tag)
	{
		fm
			.beginTransaction()
			.add(containerId, fragment, tag)
			.commit();
	}

	public static void removeFragment(FragmentManager fm, Fragment fragment)
	{
		fm
			.beginTransaction()
			.remove(fragment)
			.commit();
	}

	public static void showFragment(FragmentManager fm, Fragment fragment)
	{
		fm
			.beginTransaction()
			.show(fragment)
			.commit();
	}

	public static void hideFragment(FragmentManager fm, Fragment fragment)
	{
		fm
			.beginTransaction()
			.hide(fragment)
			.commit();
	}

	public static void addAndHideFragment(FragmentManager fm, int containerId, Fragment fragment, String tag)
	{
		fm
			.beginTransaction()
			.add(containerId, fragment, tag)
			.hide(fragment)
			.commit();
	}

	public static void showAndHideFragments(FragmentManager fm, Fragment showFragment, Fragment hideFragment, int inAnimationId, int outAnimationId)
	{
		fm
			.beginTransaction()
			.setCustomAnimations(inAnimationId, outAnimationId)
			.hide(hideFragment)
			.show(showFragment)
			.commit();
	}

	public static void showAndRemoveFragments(FragmentManager fm, Fragment showFragment, Fragment removeFragment, int inAnimationId, int outAnimationId)
	{
		fm
			.beginTransaction()
			.setCustomAnimations(inAnimationId, outAnimationId)
			.remove(removeFragment)
			.show(showFragment)
			.commit();
	}
}

package com.openbox.realcomm.utilities.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class FragmentHelper
{
	// TODO the .commitAllowingStateLoss() is a hack to fix crashes whenever a fragment
	// transaction happens when there is a popover (like opening the google user from settings)
	public static void addFragment(FragmentManager fm, int containerId, Fragment fragment, String tag)
	{
		fm
			.beginTransaction()
			.add(containerId, fragment, tag)
			.commitAllowingStateLoss();
	}

	public static void removeFragment(FragmentManager fm, Fragment fragment)
	{
		fm
			.beginTransaction()
			.remove(fragment)
			.commitAllowingStateLoss();
	}

	public static void showFragment(FragmentManager fm, Fragment fragment)
	{
		fm
			.beginTransaction()
			.show(fragment)
			.commitAllowingStateLoss();
	}

	public static void hideFragment(FragmentManager fm, Fragment fragment)
	{
		fm
			.beginTransaction()
			.hide(fragment)
			.commitAllowingStateLoss();
	}

	public static void addAndHideFragment(FragmentManager fm, int containerId, Fragment fragment, String tag)
	{
		fm
			.beginTransaction()
			.add(containerId, fragment, tag)
			.hide(fragment)
			.commitAllowingStateLoss();
	}

	public static void showAndHideFragments(FragmentManager fm, Fragment showFragment, Fragment hideFragment, int inAnimationId, int outAnimationId)
	{
		fm
			.beginTransaction()
			.setCustomAnimations(inAnimationId, outAnimationId)
			.hide(hideFragment)
			.show(showFragment)
			.commitAllowingStateLoss();
	}

	public static void showAndRemoveFragments(FragmentManager fm, Fragment showFragment, Fragment removeFragment, int inAnimationId, int outAnimationId)
	{
		fm
			.beginTransaction()
			.setCustomAnimations(inAnimationId, outAnimationId)
			.remove(removeFragment)
			.show(showFragment)
			.commitAllowingStateLoss();
	}
}

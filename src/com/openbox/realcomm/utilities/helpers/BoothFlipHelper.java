package com.openbox.realcomm.utilities.helpers;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.openbox.realcomm.fragments.BoothFragment;
import com.openbox.realcomm.utilities.animations.FlipAnimation;
import com.openbox.realcomm.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm.R;

public class BoothFlipHelper
{
	public static List<DataChangedCallbacks> updateBoothViews(
		int numberOfBigBooths,
		List<Integer> boothIdsToDisplay,
		int duration,
		int betweenDelayDuration,
		FragmentManager childFragmentManager)
	{
		List<DataChangedCallbacks> boothDataChangedListeners = new ArrayList<>();
		int betweenDelay = 0;
		for (int i = 0; i < boothIdsToDisplay.size(); i++)
		{
			int newBoothId = boothIdsToDisplay.get(i);
			int containerId = BoothFlipHelper.getContainerId(i);

			// int duration = getResources().getInteger(R.integer.boothFragmentFlipDuration);

			FragmentTransaction ft = childFragmentManager.beginTransaction();
			ft.setCustomAnimations(R.id.flipInDownAnimation, R.id.flipOutDownAnimation);

			BoothFragment oldFragment = (BoothFragment) childFragmentManager.findFragmentById(containerId);
			Boolean switchFragment = true;
			if (oldFragment != null)
			{
				if (oldFragment.getBoothId() == newBoothId)
				{
					// TODO These should be through an interface
					oldFragment.updateBooth();
					switchFragment = false;
				}
				else
				{
					oldFragment.setOutAnimationDuration(duration);
					oldFragment.setOutAnimationInterpolator(AnimationInterpolator.ACCELERATE);
					oldFragment.setOutAnimationDelay(betweenDelay);
					oldFragment.setOutFlipStartDegrees(FlipAnimation.DEFAULT_OUT_UP_START_DEGREES);
					oldFragment.setOutFlipEndDegrees(FlipAnimation.DEFAULT_OUT_UP_END_DEGREES);

					// oldFragment.setOutFlipStartDegrees(FlipAnimation.DEFAULT_OUT_DOWN_START_DEGREES);
					// oldFragment.setOutFlipEndDegrees(FlipAnimation.DEFAULT_OUT_DOWN_END_DEGREES);
					ft.remove(oldFragment);
				}
			}

			// If first fragment, or changing booths, then add
			if (switchFragment)
			{
				Boolean isBig = i < numberOfBigBooths;
				BoothFragment newFragment = BoothFragment.newInstance(newBoothId, isBig);

				newFragment.setInAnimationDuration(duration);
				newFragment.setInAnimationInterpolator(AnimationInterpolator.DECELERATE);
				newFragment.setInFlipStartDegrees(FlipAnimation.DEFAULT_IN_UP_START_DEGREES);
				newFragment.setInFlipEndDegrees(FlipAnimation.DEFAULT_IN_UP_END_DEGREES);

				// newFragment.setInFlipStartDegrees(FlipAnimation.DEFAULT_IN_DOWN_START_DEGREES);
				// newFragment.setInFlipEndDegrees(FlipAnimation.DEFAULT_IN_DOWN_END_DEGREES);

				newFragment.setInAnimationDelay(betweenDelay + (oldFragment != null ? duration : 0));

				ft.add(containerId, newFragment);

				betweenDelay += betweenDelayDuration;

				boothDataChangedListeners.add(newFragment);
			}
			else
			{
				boothDataChangedListeners.add(oldFragment);
			}

			// So it can flip after onPause() (and therefore onSaveInstanceState())
			ft.commitAllowingStateLoss();
		}

		return boothDataChangedListeners;
	}

	private static int getContainerId(int index)
	{
		switch (index)
		{
			case 0:
				return R.id.firstClosestBoothContainer;
			case 1:
				return R.id.secondClosestBoothContainer;
			case 2:
				return R.id.thirdClosestBoothContainer;
			default:
				return -1;
		}
	}
}

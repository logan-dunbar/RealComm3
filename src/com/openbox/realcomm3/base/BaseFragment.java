package com.openbox.realcomm3.base;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.helpers.AnimationHelper;
import com.openbox.realcomm3.utilities.helpers.CustomAnimationListener;
import com.openbox.realcomm3.utilities.interfaces.ActivityInterface;
import com.openbox.realcomm3.utilities.interfaces.AppModeInterface;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

public class BaseFragment extends Fragment
{
	public static final int DEFAULT_ANIMATION_DURATION = 0;
	public static final int DEFAULT_DELAY = 0;

	private ActivityInterface activityListener;
	private DataInterface dataInterface;
	private AppModeInterface appModeInterface;

	private int inAnimationDuration = DEFAULT_ANIMATION_DURATION;
	private int outAnimationDuration = DEFAULT_ANIMATION_DURATION;
	private int inAnimationDelay = DEFAULT_DELAY;
	private int outAnimationDelay = DEFAULT_DELAY;
	private AnimationInterpolator inAnimationInterpolator = AnimationInterpolator.LINEAR;
	private AnimationInterpolator outAnimationInterpolator = AnimationInterpolator.LINEAR;
	private CustomAnimationListener inAnimationCompleteListener;
	private CustomAnimationListener outAnimationCompleteListener;
	private int inFlipStartDegrees;
	private int inFlipEndDegrees;
	private int outFlipStartDegrees;
	private int outFlipEndDegrees;

	public ActivityInterface getActivityListener()
	{
		return activityListener;
	}

	public DataInterface getDataInterface()
	{
		return dataInterface;
	}

	public AppModeInterface getAppModeInterface()
	{
		return appModeInterface;
	}

	public void setInAnimationDuration(int inAnimationDuration)
	{
		this.inAnimationDuration = inAnimationDuration;
	}

	public int getInAnimationDuration()
	{
		return inAnimationDuration;
	}

	public void setOutAnimationDuration(int outAnimationDuration)
	{
		this.outAnimationDuration = outAnimationDuration;
	}

	public int getOutAnimationDuration()
	{
		return outAnimationDuration;
	}

	public void setInAnimationDelay(int inAnimationDelay)
	{
		this.inAnimationDelay = inAnimationDelay;
	}

	public int getInAnimationDelay()
	{
		return inAnimationDelay;
	}

	public void setOutAnimationDelay(int outAnimationDelay)
	{
		this.outAnimationDelay = outAnimationDelay;
	}

	public int getOutAnimationDelay()
	{
		return outAnimationDelay;
	}

	public void setInAnimationInterpolator(AnimationInterpolator inAnimationInterpolator)
	{
		this.inAnimationInterpolator = inAnimationInterpolator;
	}

	public void setOutAnimationInterpolator(AnimationInterpolator outAnimationInterpolator)
	{
		this.outAnimationInterpolator = outAnimationInterpolator;
	}

	public void setInAnimationCompleteListener(Runnable inAnimationCompleteRunnable)
	{
		this.inAnimationCompleteListener = new CustomAnimationListener(inAnimationCompleteRunnable);
	}

	public void setOutAnimationCompleteListener(Runnable outAnimationCompleteRunnable)
	{
		this.outAnimationCompleteListener = new CustomAnimationListener(outAnimationCompleteRunnable);
	}

	public void setInFlipStartDegrees(int inFlipStartDegrees)
	{
		this.inFlipStartDegrees = inFlipStartDegrees;
	}

	public void setInFlipEndDegrees(int inFlipEndDegrees)
	{
		this.inFlipEndDegrees = inFlipEndDegrees;
	}

	public void setOutFlipStartDegrees(int outFlipStartDegrees)
	{
		this.outFlipStartDegrees = outFlipStartDegrees;
	}

	public void setOutFlipEndDegrees(int outFlipEndDegrees)
	{
		this.outFlipEndDegrees = outFlipEndDegrees;
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		if (activity instanceof ActivityInterface)
		{
			this.activityListener = (ActivityInterface) activity;
		}

		if (activity instanceof DataInterface)
		{
			this.dataInterface = (DataInterface) activity;
		}

		if (activity instanceof AppModeInterface)
		{
			this.appModeInterface = (AppModeInterface) activity;
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.activityListener = null;
		this.dataInterface = null;
		this.appModeInterface = null;
	}

	private static Animation createNoneAnimation(Fragment fragment, boolean enter)
	{
		Animation animation = null;
		Fragment nextParentFragment;

		// Get top most fragment (which has the animation)
		while ((nextParentFragment = fragment.getParentFragment()) != null)
		{
			fragment = nextParentFragment;
		}

		// Create the None animation (which is AlphaAnimation from 1.0 to 1.0)
		if (fragment instanceof BaseFragment && fragment.isRemoving())
		{
			BaseFragment baseFrag = (BaseFragment) fragment;

			int duration;
			int delay;
			if (enter)
			{
				duration = baseFrag.getInAnimationDuration();
				delay = baseFrag.getInAnimationDelay();
			}
			else
			{
				duration = baseFrag.getOutAnimationDuration();
				delay = baseFrag.getOutAnimationDelay();
			}

			animation = AnimationHelper.getNoneAnimation(AnimationInterpolator.LINEAR, duration, delay, null);
		}

		return animation;
	}

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim)
	{
		// Hack to prevent child fragments from disappearing when the parent is removed (for the profile page).
		if (isVisible() && getParentFragment() != null && this instanceof BaseProfileFragment)
		{
			return createNoneAnimation(getParentFragment(), enter);
		}

		Animation animation;
		// TODO going to need to check this, how to get values/parameters in here, check what transit is?
		switch (nextAnim)
		{
			case R.id.fadeInAnimation:
				animation = AnimationHelper.getFadeInAnimation(
					this.inAnimationInterpolator,
					this.inAnimationDuration,
					this.inAnimationDelay,
					this.inAnimationCompleteListener);
				break;
			case R.id.fadeOutAnimation:
				animation = AnimationHelper.getFadeOutAnimation(
					this.outAnimationInterpolator,
					this.outAnimationDuration,
					this.outAnimationDelay,
					this.outAnimationCompleteListener);
				break;
			case R.id.slideInRightAnimation:
				animation = AnimationHelper.getSlideInRightAnimation(
					this.inAnimationInterpolator,
					this.inAnimationDuration,
					this.inAnimationDelay,
					this.inAnimationCompleteListener);
				break;
			case R.id.slideOutRightAnimation:
				animation = AnimationHelper.getSlideOutRightAnimation(
					this.outAnimationInterpolator,
					this.outAnimationDuration,
					this.outAnimationDelay,
					this.outAnimationCompleteListener);
				break;
			case R.id.slideInLeftAnimation:
				animation = AnimationHelper.getSlideInLeftAnimation(
					this.inAnimationInterpolator,
					this.inAnimationDuration,
					this.inAnimationDelay,
					this.inAnimationCompleteListener);
				break;
			case R.id.slideOutLeftAnimation:
				animation = AnimationHelper.getSlideOutLeftAnimation(
					this.outAnimationInterpolator,
					this.outAnimationDuration,
					this.outAnimationDelay,
					this.outAnimationCompleteListener);
				break;
			case R.id.slideUpInAnimation:
				animation = AnimationHelper.getSlideUpInAnimation(
					this.inAnimationInterpolator,
					this.inAnimationDuration,
					this.inAnimationDelay,
					this.inAnimationCompleteListener);
				break;
			case R.id.slideUpOutAnimation:
				animation = AnimationHelper.getSlideUpOutAnimation(
					this.outAnimationInterpolator,
					this.outAnimationDuration,
					this.outAnimationDelay,
					this.outAnimationCompleteListener);
				break;
			case R.id.flipInDownAnimation:
				animation = AnimationHelper.getFlipInUpDownAnimation(
					this.inAnimationInterpolator,
					this.inAnimationDuration,
					this.inAnimationDelay,
					this.inAnimationCompleteListener,
					this.inFlipStartDegrees,
					this.inFlipEndDegrees);
				break;
			case R.id.flipOutDownAnimation:
				animation = AnimationHelper.getFlipOutUpDownAnimation(
					this.outAnimationInterpolator,
					this.outAnimationDuration,
					this.outAnimationDelay,
					this.outAnimationCompleteListener,
					this.outFlipStartDegrees,
					this.outFlipEndDegrees);
				break;
			default:
				return super.onCreateAnimation(transit, enter, nextAnim);
		}

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(animation);

		return set;
	}

}

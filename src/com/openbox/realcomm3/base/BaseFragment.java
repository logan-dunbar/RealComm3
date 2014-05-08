package com.openbox.realcomm3.base;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.helpers.AnimationHelper;
import com.openbox.realcomm3.utilities.helpers.CustomAnimationListener;
import com.openbox.realcomm3.utilities.interfaces.ActivityInterface;
import com.openbox.realcomm3.utilities.interfaces.AppModeInterface;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
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

	public void setOutAnimationDuration(int outAnimationDuration)
	{
		this.outAnimationDuration = outAnimationDuration;
	}

	public void setInAnimationDelay(int inAnimationDelay)
	{
		this.inAnimationDelay = inAnimationDelay;
	}

	public void setOutAnimationDelay(int outAnimationDelay)
	{
		this.outAnimationDelay = outAnimationDelay;
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

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// So the activity can animate knowing the view is all there
		if (this.activityListener != null)
		{
			// TODO see if this is required or not
			// this.activityListener.onFragmentViewCreated();
		}
	}

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim)
	{
		// TODO going to need to check this, how to get values/parameters in here, check what transit is?
		Animation animation;
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
				return null;
		}

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(animation);

		return set;
	}

}

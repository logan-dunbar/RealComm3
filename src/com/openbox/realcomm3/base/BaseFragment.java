package com.openbox.realcomm3.base;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.helpers.AnimationHelper;
import com.openbox.realcomm3.utilities.helpers.CustomAnimationListener;
import com.openbox.realcomm3.utilities.interfaces.ActivityInterface;

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

	private int inAnimationDuration = DEFAULT_ANIMATION_DURATION;
	private int outAnimationDuration = DEFAULT_ANIMATION_DURATION;
	private int inAnimationDelay = DEFAULT_DELAY;
	private int outAnimationDelay = DEFAULT_DELAY;
	private AnimationInterpolator inAnimationInterpolator = AnimationInterpolator.LinearInterpolator;
	private AnimationInterpolator outAnimationInterpolator = AnimationInterpolator.LinearInterpolator;
	private CustomAnimationListener inAnimationCompleteListener;
	private CustomAnimationListener outAnimationCompleteListener;

	public ActivityInterface getActivityListener()
	{
		return activityListener;
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

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		if (activity instanceof ActivityInterface)
		{
			this.activityListener = (ActivityInterface) activity;
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		this.activityListener = null;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// So the activity can animate knowing the view is all there
		if (this.activityListener != null)
		{
			this.activityListener.onFragmentViewCreated();
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
			default:
				return null;
		}

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(animation);

		return set;
	}

}

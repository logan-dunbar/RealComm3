package com.openbox.realcomm3.utilities.helpers;

import com.openbox.realcomm3.utilities.animations.FlipAnimation;
import com.openbox.realcomm3.utilities.animations.FlipAnimation.FlipDirection;
import com.openbox.realcomm3.utilities.animations.FlipAnimation.ScaleUpDownEnum;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

public class AnimationHelper
{
	public static Animation getFadeInAnimation(AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener)
	{
		Animation animation = new AlphaAnimation(0, 1);
		setAnimationValues(animation, interpolator, duration, delay, listener);
		return animation;
	}

	public static Animation getFadeOutAnimation(AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener)
	{
		Animation animation = new AlphaAnimation(1, 0);
		setAnimationValues(animation, interpolator, duration, delay, listener);
		return animation;
	}

	public static Animation getSlideUpInAnimation(AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener)
	{
		Animation animation = new TranslateAnimation(
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 1.0f,
			Animation.RELATIVE_TO_SELF, 0.0f);
		setAnimationValues(animation, interpolator, duration, delay, listener);
		return animation;
	}

	public static Animation getSlideUpOutAnimation(AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener)
	{
		Animation animation = new TranslateAnimation(
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, -1.0f);
		setAnimationValues(animation, interpolator, duration, delay, listener);
		return animation;
	}

	public static Animation getSlideInRightAnimation(AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener)
	{
		Animation animation = new TranslateAnimation(
			Animation.RELATIVE_TO_SELF, 1.0f,
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 0.0f);
		setAnimationValues(animation, interpolator, duration, delay, listener);
		return animation;
	}

	public static Animation getSlideOutRightAnimation(AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener)
	{
		Animation animation = new TranslateAnimation(
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 1.0f,
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 0.0f);
		setAnimationValues(animation, interpolator, duration, delay, listener);
		return animation;
	}

	public static Animation getSlideInLeftAnimation(AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener)
	{
		Animation animation = new TranslateAnimation(
			Animation.RELATIVE_TO_SELF, -1.0f,
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 0.0f);
		setAnimationValues(animation, interpolator, duration, delay, listener);
		return animation;
	}

	public static Animation getSlideOutLeftAnimation(AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener)
	{
		Animation animation = new TranslateAnimation(
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, -1.0f,
			Animation.RELATIVE_TO_SELF, 0.0f,
			Animation.RELATIVE_TO_SELF, 0.0f);
		setAnimationValues(animation, interpolator, duration, delay, listener);
		return animation;
	}

	// TODO come up with better names...
	public static Animation getFlipInUpDownAnimation(AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener,
		int startDegrees, int endDegrees)
	{
		FlipAnimation animation = new FlipAnimation(startDegrees, endDegrees, FlipAnimation.SCALE_DEFAULT, FlipAnimation.ScaleUpDownEnum.SCALE_UP,
			FlipAnimation.FlipDirection.UpDown);
		setAnimationValues(animation, interpolator, duration, delay, listener);
		return animation;
	}

	public static Animation getFlipOutUpDownAnimation(AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener,
		int startDegrees, int endDegrees)
	{
		FlipAnimation animation = new FlipAnimation(startDegrees, endDegrees, FlipAnimation.SCALE_DEFAULT, FlipAnimation.ScaleUpDownEnum.SCALE_DOWN,
			FlipAnimation.FlipDirection.UpDown);
		setAnimationValues(animation, interpolator, duration, delay, listener);
		return animation;
	}

	private static void setAnimationValues(Animation animation, AnimationInterpolator interpolator, int duration, int delay, CustomAnimationListener listener)
	{
		animation.setInterpolator(getAnimationInterpolator(interpolator));
		animation.setStartOffset(delay);
		animation.setDuration(duration);
		animation.setAnimationListener(listener);
	}

	private static Interpolator getAnimationInterpolator(AnimationInterpolator interpolatorEnum)
	{
		switch (interpolatorEnum)
		{
			case ACCELERATEDECELERATE:
				return new AccelerateDecelerateInterpolator();
			case ACCELERATE:
				return new AccelerateInterpolator();
			case DECELERATE:
				return new DecelerateInterpolator();
			case LINEAR:
				return new LinearInterpolator();
			default:
				return new LinearInterpolator();
		}
	}
}

package com.openbox.realcomm3.base;

import java.util.ArrayList;
import java.util.List;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.helpers.AnimationHelper;
import com.openbox.realcomm3.utilities.helpers.CustomAnimationListener;
import com.openbox.realcomm3.utilities.interfaces.ActivityInterface;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.AppModeInterface;
import com.openbox.realcomm3.utilities.interfaces.ClearFocusInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

public class BaseFragment extends Fragment implements
	DataChangedCallbacks,
	AppModeChangedCallbacks,
	ClearFocusInterface
{
	public static final int DEFAULT_ANIMATION_DURATION = 0;
	public static final int DEFAULT_DELAY = 0;

	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	// Listeners/Interfaces
	private ActivityInterface activityInterface;
	private DataInterface dataInterface;
	private AppModeInterface appModeInterface;

	private final List<DataChangedCallbacks> dataChangedListeners = new ArrayList<>();
	private final List<AppModeChangedCallbacks> appModeChangedListeners = new ArrayList<>();
	private final List<ClearFocusInterface> clearFocusListeners = new ArrayList<>();

	// TODO Wrap these into an AnimationValues object. This is just gross
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

	/**********************************************************************************************
	 * Getters/Setters
	 **********************************************************************************************/
	public ActivityInterface getActivityInterface()
	{
		return activityInterface;
	}

	public DataInterface getDataInterface()
	{
		return dataInterface;
	}

	public AppModeInterface getAppModeInterface()
	{
		return appModeInterface;
	}

	public List<DataChangedCallbacks> getDataChangedListeners()
	{
		return dataChangedListeners;
	}

	public List<AppModeChangedCallbacks> getAppModeChangedListeners()
	{
		return appModeChangedListeners;
	}

	public List<ClearFocusInterface> getClearFocusListeners()
	{
		return clearFocusListeners;
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

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		if (activity instanceof ActivityInterface)
		{
			this.activityInterface = (ActivityInterface) activity;
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
		this.activityInterface = null;
		this.dataInterface = null;
		this.appModeInterface = null;

		this.dataChangedListeners.clear();
		this.appModeChangedListeners.clear();
		this.clearFocusListeners.clear();
	}

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim)
	{
		// Hack to prevent child fragments from disappearing when the parent is removed (for the profile page).
		// if (isVisible() && getParentFragment() != null && this instanceof BaseProfileFragment)
		// {
		// return createNoneAnimation(getParentFragment(), enter);
		// }

		Animation animation;
		// TODO going to need to check this, how to get values/parameters in here, check what transit is?
		switch (nextAnim)
		{
			case R.id.noneInAnimation:
				animation = AnimationHelper.getNoneAnimation(
					this.inAnimationInterpolator,
					this.inAnimationDuration,
					this.inAnimationDelay,
					this.inAnimationCompleteListener);
				break;
			case R.id.noneOutAnimation:
				animation = AnimationHelper.getNoneAnimation(
					this.outAnimationInterpolator,
					this.outAnimationDuration,
					this.outAnimationDelay,
					this.outAnimationCompleteListener);
				break;
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

	/**********************************************************************************************
	 * Data Changed Callbacks Implements
	 **********************************************************************************************/
	@Override
	public void onDataChanged()
	{
		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onDataChanged();
		}
	}

	@Override
	public void onDataLoaded()
	{
		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onDataLoaded();
		}
	}

	@Override
	public void onBeaconsUpdated()
	{
		for (DataChangedCallbacks listener : this.dataChangedListeners)
		{
			listener.onBeaconsUpdated();
		}
	}

	/**********************************************************************************************
	 * App Mode Changed Callbacks Implements
	 **********************************************************************************************/
	@Override
	public void onAppModeChanged(AppMode newAppMode, AppMode previousAppMode)
	{
		for (AppModeChangedCallbacks listener : this.appModeChangedListeners)
		{
			listener.onAppModeChanged(newAppMode, previousAppMode);
		}
	}

	/**********************************************************************************************
	 * Clear Focus Interface Implements
	 **********************************************************************************************/
	@Override
	public List<View> getViewsToClearFocus()
	{
		List<View> views = new ArrayList<>();
		for (ClearFocusInterface listener : this.clearFocusListeners)
		{
			views.addAll(listener.getViewsToClearFocus());
		}

		return views;
	}
}

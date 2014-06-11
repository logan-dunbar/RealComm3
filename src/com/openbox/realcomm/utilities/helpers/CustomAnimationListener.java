package com.openbox.realcomm.utilities.helpers;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class CustomAnimationListener implements AnimationListener
{
	Runnable callback;

	public CustomAnimationListener(Runnable callback)
	{
		this.callback = callback;
	}

	@Override
	public void onAnimationStart(Animation animation)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onAnimationEnd(Animation animation)
	{
		// TODO Auto-generated method stub
		if (this.callback != null)
		{
			this.callback.run();
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation)
	{
		// TODO Auto-generated method stub
	}
}
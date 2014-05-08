package com.openbox.realcomm3.controls;

import java.util.ArrayList;
import java.util.List;

import com.openbox.realcomm3.utilities.animations.FlipAnimation;
import com.openbox.realcomm3.utilities.interfaces.BoothFrameLayoutCallbacks;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;

public class BoothFrameLayout extends FrameLayout
{
	private BoothFrameLayoutCallbacks boothFrameLayoutListener;

	public void setBoothFrameLayoutListener(BoothFrameLayoutCallbacks boothFrameLayoutListener)
	{
		this.boothFrameLayoutListener = boothFrameLayoutListener;
	}

	public BoothFrameLayout(Context context)
	{
		super(context);
	}

	public BoothFrameLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public BoothFrameLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// Listener to let know when measured, then go through the animations... (or whenever one is added) boom!
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		if (this.boothFrameLayoutListener != null)
		{
			this.boothFrameLayoutListener.onMeasureCalled(width, height);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}

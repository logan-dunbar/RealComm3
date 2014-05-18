package com.openbox.realcomm3.controls;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class VariableHeightViewPager extends ViewPager
{
	private boolean isSettingHeight = false;

	public VariableHeightViewPager(Context context)
	{
		super(context);
	}

	public VariableHeightViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void setVariableHeight()
	{
		// super.measure() calls finishUpdate() in adapter, so need this to stop infinite loop
		if (!this.isSettingHeight)
		{
			this.isSettingHeight = true;

			int maxChildHeight = 0;
			int widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
			for (int i = 0; i < getChildCount(); i++)
			{
				View child = getChildAt(i);
				child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED));
				maxChildHeight = child.getMeasuredHeight() > maxChildHeight ? child.getMeasuredHeight() : maxChildHeight;
			}

			int height = maxChildHeight + getPaddingTop() + getPaddingBottom();
			int heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

			super.measure(widthMeasureSpec, heightMeasureSpec);
			requestLayout();

			this.isSettingHeight = false;
		}
	}
}

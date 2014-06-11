package com.openbox.realcomm.controls;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FixedRatioImageView extends ImageView
{
	public FixedRatioImageView(Context context)
	{
		super(context);
	}

	public FixedRatioImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public FixedRatioImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
		int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

		int finalWidth = originalWidth;
		int finalHeight = originalHeight;

		Drawable drawable = getDrawable();
		if (drawable != null)
		{
			int bgWidth = drawable.getIntrinsicWidth();
			int bgHeight = drawable.getIntrinsicHeight();

			double aspectRatio = (double) bgWidth / (double) bgHeight;

			int calculatedHeight = (int) (finalWidth / aspectRatio);

			if (calculatedHeight > originalHeight)
			{
				// If calculated height is bigger, we need to scale width down
				finalWidth = (int) (originalHeight * aspectRatio);
			}
			else
			{
				// else scale height down
				finalHeight = calculatedHeight;
			}
		}

		super.onMeasure(
			MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
			MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
	}
}

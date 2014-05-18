package com.openbox.realcomm3.utilities.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class BitmapHelper
{
	public static Bitmap getRoundedBitmap(Bitmap src, int width, int height, float radius)
	{
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		Canvas canvas = new Canvas(result);
		canvas.drawARGB(0, 0, 0, 0);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);

		// TODO: play around with FIT vs. CROP, or make sure the images are all in the correct aspect ratio
		final Rect srcRect = calculateSrcRect(src.getWidth(), src.getHeight(), width, height, ScalingLogic.CROP);
		final Rect dstRect = calculateDstRect(src.getWidth(), src.getHeight(), width, height, ScalingLogic.CROP);

		final RectF dstRectF = new RectF(dstRect);

		canvas.drawRoundRect(dstRectF, radius, radius, paint);

		paint.setFilterBitmap(true);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		canvas.drawBitmap(src, srcRect, dstRect, paint);

		return result;
	}

	public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic)
	{
		if (scalingLogic == ScalingLogic.CROP)
		{
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;

			if (srcAspect > dstAspect)
			{
				final int srcRectWidth = (int) (srcHeight * dstAspect);
				final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
				return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
			}
			else
			{
				final int srcRectHeight = (int) (srcWidth / dstAspect);
				final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
				return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
			}
		}
		else
		{
			return new Rect(0, 0, srcWidth, srcHeight);
		}
	}

	public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic)
	{
		if (scalingLogic == ScalingLogic.FIT)
		{
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;

			if (srcAspect > dstAspect)
			{
				return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
			}
			else
			{
				return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
			}
		}
		else
		{
			return new Rect(0, 0, dstWidth, dstHeight);
		}
	}

	public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic)
	{
		if (scalingLogic == ScalingLogic.FIT)
		{
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;

			if (srcAspect > dstAspect)
			{
				return srcWidth / dstWidth;
			}
			else
			{
				return srcHeight / dstHeight;
			}
		}
		else
		{
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;

			if (srcAspect > dstAspect)
			{
				return srcHeight / dstHeight;
			}
			else
			{
				return srcWidth / dstWidth;
			}
		}
	}

	public static enum ScalingLogic
	{
		CROP,
		FIT
	}
}

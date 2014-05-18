package com.openbox.realcomm3.controls;

import com.openbox.realcomm3.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ProfileBorder extends View
{
	public ProfileBorder(Context context)
	{
		super(context);
	}

	public ProfileBorder(Context context, boolean isHorizontal)
	{
		super(context);

		init(isHorizontal);
	}

	private void init(boolean isHorizontal)
	{
		LayoutParams params;
		if (isHorizontal)
		{
			params = new LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.profileBorderWidth));
		}
		else
		{
			params = new LayoutParams((int) getResources().getDimension(R.dimen.profileBorderWidth), LayoutParams.MATCH_PARENT);
		}

		setLayoutParams(params);
		setBackgroundColor(getResources().getColor(R.color.border_grey));
	}
}

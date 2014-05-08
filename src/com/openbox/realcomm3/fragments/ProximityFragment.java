package com.openbox.realcomm3.fragments;

import java.util.Map;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.enums.ProximityRegion;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.AppModeInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;

public class ProximityFragment extends BaseFragment implements DataChangedCallbacks, AppModeChangedCallbacks
{
	private TextView immediateTextView;
	private TextView nearTextView;
	private TextView farTextView;

	public static ProximityFragment newInstance()
	{
		ProximityFragment fragment = new ProximityFragment();
		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_proximity, container, false);

		this.immediateTextView = (TextView) view.findViewById(R.id.immediateTextView);
		this.nearTextView = (TextView) view.findViewById(R.id.nearTextView);
		this.farTextView = (TextView) view.findViewById(R.id.FarTextView);

		updateView();

		return view;
	}

	/**********************************************************************************************
	 * Data Changed Callbacks
	 **********************************************************************************************/
	@Override
	public void onDataLoaded()
	{
		// TODO check how this looks as its starts
		updateView();
	}

	@Override
	public void onDataChanged()
	{
		// Stub. Not needed
	}

	@Override
	public void onBeaconsUpdated()
	{
		updateView();
	}

	/**********************************************************************************************
	 * App Mode Changed Callbacks
	 **********************************************************************************************/
	@Override
	public void onAppModeChanged()
	{
		updateView();
	}

	@Override
	public void onOnlineModeToOfflineMode()
	{
		// Stub. Not needed
	}

	/**********************************************************************************************
	 * Private Helper Methods
	 **********************************************************************************************/
	private void updateView()
	{
		if (getDataInterface() != null && getAppModeInterface() != null)
		{
			if (getAppModeInterface().getCurrentAppMode() == AppMode.OFFLINE)
			{
				updateTextView(this.immediateTextView, ProximityRegion.OUTOFRANGE, 0);
				updateTextView(this.nearTextView, ProximityRegion.OUTOFRANGE, 0);
				updateTextView(this.farTextView, ProximityRegion.OUTOFRANGE, 0);
			}
			else
			{
				Map<ProximityRegion, Integer> proximityCounts = getDataInterface().getBoothProximityCounts();
				if (proximityCounts != null)
				{
					updateTextView(this.immediateTextView, ProximityRegion.IMMEDIATE, proximityCounts.get(ProximityRegion.IMMEDIATE));
					updateTextView(this.nearTextView, ProximityRegion.NEAR, proximityCounts.get(ProximityRegion.NEAR));
					updateTextView(this.farTextView, ProximityRegion.FAR, proximityCounts.get(ProximityRegion.FAR));
				}
			}
		}
	}

	private void updateTextView(TextView textView, ProximityRegion proximityRegion, int count)
	{
		textView.setText(String.valueOf(count));
		GradientDrawable bg = (GradientDrawable) textView.getBackground();
		bg.setColor(getResources().getColor(proximityRegion.getColorId()));
	}
}

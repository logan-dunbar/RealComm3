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
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.enums.ProximityRegion;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.AppModeInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;

public class ProximityFragment extends BaseFragment implements DataChangedCallbacks, AppModeChangedCallbacks
{
	private static final String BOOTHS_NEAR_ME_SUFFIX = " BOOTHS NEAR ME";
	private static final String NO_BOOTHS_NEAR_ME = "NO BOOTHS NEAR ME";

	private TextView boothsNearMeTextView;
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

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		// Active
		this.boothsNearMeTextView = (TextView) view.findViewById(R.id.boothsNearMeTextView);
		this.immediateTextView = (TextView) view.findViewById(R.id.proximityImmediateTextView);
		this.nearTextView = (TextView) view.findViewById(R.id.proximityNearTextView);
		this.farTextView = (TextView) view.findViewById(R.id.proximityFarTextView);

		this.boothsNearMeTextView.setTypeface(application.getExo2FontBold());
		this.immediateTextView.setTypeface(application.getExo2FontBold());
		this.nearTextView.setTypeface(application.getExo2FontBold());
		this.farTextView.setTypeface(application.getExo2FontBold());

		// Static
		TextView immediateLabel = (TextView) view.findViewById(R.id.proximityImmediateLabelTextView);
		TextView nearLabel = (TextView) view.findViewById(R.id.proximityNearLabelTextView);
		TextView farLabel = (TextView) view.findViewById(R.id.proximityFarLabelTextView);

		immediateLabel.setText(ProximityRegion.IMMEDIATE.getLabel());
		nearLabel.setText(ProximityRegion.NEAR.getLabel());
		farLabel.setText(ProximityRegion.FAR.getLabel());

		immediateLabel.setTypeface(application.getExo2Font());
		nearLabel.setTypeface(application.getExo2Font());
		farLabel.setTypeface(application.getExo2Font());

		TextView exhibitionAreaTextView = (TextView) view.findViewById(R.id.exhibitionAreaTextView);
		if (exhibitionAreaTextView != null)
		{
			// Not present in phone version of xml
			exhibitionAreaTextView.setTypeface(application.getExo2Font());
		}

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
			if (getAppModeInterface().getCurrentAppMode() == AppMode.OFFLINE ||
				(getAppModeInterface().getCurrentAppMode() == AppMode.PAUSED && getAppModeInterface().getPreviousAppMode() == AppMode.OFFLINE))
			{
				this.boothsNearMeTextView.setText(NO_BOOTHS_NEAR_ME);

				// TODO Potentially remove the circles/counts
				updateTextView(this.immediateTextView, ProximityRegion.OUTOFRANGE, 0);
				updateTextView(this.nearTextView, ProximityRegion.OUTOFRANGE, 0);
				updateTextView(this.farTextView, ProximityRegion.OUTOFRANGE, 0);
			}
			else
			{
				Map<ProximityRegion, Integer> proximityCounts = getDataInterface().getBoothProximityCounts();
				if (proximityCounts != null)
				{
					int totalCount = proximityCounts.get(ProximityRegion.IMMEDIATE) + proximityCounts.get(ProximityRegion.NEAR)
						+ proximityCounts.get(ProximityRegion.FAR);
					this.boothsNearMeTextView.setText(String.format("%d" + BOOTHS_NEAR_ME_SUFFIX, totalCount));

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

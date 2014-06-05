package com.openbox.realcomm3.fragments;

import java.util.Map;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.enums.ProximityRegion;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;

public class ProximityFragment extends BaseFragment
{
	private static final String OFFLINE_HEADER = "OFFLINE";
	private static final String EXHIBITION_AREA_HEADER = "EXHIBITION AREA";
	private static final String OUT_OF_RANGE = "OUT OF RANGE";
	private static final String BOOTHS_NEAR_ME_SUFFIX = " BOOTHS NEAR ME";
	private static final String NO_BOOTHS_NEAR_ME = "NO BOOTHS NEAR ME";
	private static final String DEVICE_UNSUPPORTED = "DEVICE DOES NOT SUPPORT ONLINE MODE";
	private static final String PROXIMITY_FEATURES_REQUIRE_BLUETOOTH = "PROXIMITY FEATURES REQUIRE BLUETOOTH TO BE ON";

	private FrameLayout proximityControlPictureLayout;
	private TextView exhibitionAreaTextView;
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
		this.proximityControlPictureLayout = (FrameLayout) view.findViewById(R.id.proximityControlPictureLayout);
		this.exhibitionAreaTextView = (TextView) view.findViewById(R.id.exhibitionAreaTextView);
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

		// Not present in phone version of xml
		if (this.exhibitionAreaTextView != null)
		{
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
	public void onBeaconsUpdated()
	{
		updateView();
	}

	/**********************************************************************************************
	 * App Mode Changed Callbacks
	 **********************************************************************************************/
	@Override
	public void onAppModeChanged(AppMode newAppMode, AppMode previousAppMode)
	{
		updateView();
	}

	/**********************************************************************************************
	 * Private Helper Methods
	 **********************************************************************************************/
	private void updateView()
	{
		// TODO possibly re-do this
		if (getDataInterface() != null && getAppModeInterface() != null)
		{
			AppMode currentMode = getAppModeInterface().getCurrentAppMode();

			// Reset view to visible
			this.proximityControlPictureLayout.setVisibility(View.VISIBLE);

			switch (currentMode)
			{
				case OFFLINE:
					updateViewOffline();
					break;
				case ONLINE:
					updateViewOnline(getDataInterface());
					break;
				case OUTOFRANGE:
					updateViewOutOfRange();
					break;
				default:
					break;
			}
		}
	}

	private void updateViewOutOfRange()
	{
		if (this.exhibitionAreaTextView != null)
		{
			this.exhibitionAreaTextView.setText(OUT_OF_RANGE);
		}

		this.boothsNearMeTextView.setText(NO_BOOTHS_NEAR_ME);

		this.immediateTextView.setVisibility(View.GONE);
		this.nearTextView.setVisibility(View.GONE);
		this.farTextView.setVisibility(View.GONE);
	}

	private void updateViewOnline(DataInterface dataInterface)
	{
		if (this.exhibitionAreaTextView != null)
		{
			this.exhibitionAreaTextView.setText(EXHIBITION_AREA_HEADER);
		}

		Map<ProximityRegion, Integer> proximityCounts = dataInterface.getBoothProximityCounts();
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

	private void updateViewOffline()
	{
		if (this.exhibitionAreaTextView != null)
		{
			this.exhibitionAreaTextView.setText(OFFLINE_HEADER);
		}

		if (RealCommApplication.getHasBluetoothLe())
		{
			this.boothsNearMeTextView.setText(PROXIMITY_FEATURES_REQUIRE_BLUETOOTH);
		}
		else
		{
			this.boothsNearMeTextView.setText(DEVICE_UNSUPPORTED);
		}

		this.proximityControlPictureLayout.setVisibility(View.GONE);
	}

	private void updateTextView(TextView textView, ProximityRegion proximityRegion, int count)
	{
		textView.setVisibility(View.VISIBLE);
		textView.setText(String.valueOf(count));
		GradientDrawable bg = (GradientDrawable) textView.getBackground();
		bg.setColor(getResources().getColor(proximityRegion.getColorId()));
	}
}

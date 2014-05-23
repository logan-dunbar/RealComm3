package com.openbox.realcomm3.fragments;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseFragment;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SplashScreenFragment extends BaseFragment
{
	public static final String TAG = "splashScreenFragment";

	public static SplashScreenFragment newInstance()
	{
		SplashScreenFragment fragment = new SplashScreenFragment();

		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_splash_screen, container, false);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();
		Resources resources = getActivity().getResources();

		TextView header = (TextView) view.findViewById(R.id.splashScreenHeader);
		header.setTypeface(application.getExo2Font());
		TextView subHeader = (TextView) view.findViewById(R.id.splashScreenSubHeader);
		subHeader.setTypeface(application.getNunitoFontBold());

		FrameLayout redCircle = (FrameLayout) view.findViewById(R.id.splashCircleRed);
		GradientDrawable redBg = (GradientDrawable) redCircle.getBackground();
		redBg.setColor(resources.getColor(R.color.opaque_red));
		FrameLayout orangeCircle = (FrameLayout) view.findViewById(R.id.splashCircleOrange);
		GradientDrawable orangeBg = (GradientDrawable) orangeCircle.getBackground();
		orangeBg.setColor(resources.getColor(R.color.opaque_orange));
		FrameLayout blueCircle = (FrameLayout) view.findViewById(R.id.splashCircleBlue);
		GradientDrawable blueBg = (GradientDrawable) blueCircle.getBackground();
		blueBg.setColor(resources.getColor(R.color.opaque_blue));

		TextView exploreTitle = (TextView) view.findViewById(R.id.splashScreenExploreTitleTextView);
		exploreTitle.setTypeface(application.getExo2Font());
		TextView exploreDesc = (TextView) view.findViewById(R.id.splashScreenExploreDescTextView);
		exploreDesc.setTypeface(application.getExo2Font());

		TextView findTitle = (TextView) view.findViewById(R.id.splashScreenFindTitleTextView);
		findTitle.setTypeface(application.getExo2Font());
		TextView findDesc = (TextView) view.findViewById(R.id.splashScreenFindDescTextView);
		findDesc.setTypeface(application.getExo2Font());

		TextView attendTitle = (TextView) view.findViewById(R.id.splashScreenAttendTitleTextView);
		attendTitle.setTypeface(application.getExo2Font());
		TextView attendDesc = (TextView) view.findViewById(R.id.splashScreenAttendDescTextView);
		attendDesc.setTypeface(application.getExo2Font());

		return view;
	}
}

package com.openbox.realcomm.fragments;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.application.SharedPreferencesManager;
import com.openbox.realcomm.base.BaseFragment;
import com.openbox.realcomm.database.models.BoothModel;
import com.openbox.realcomm.utilities.enums.RealcommPage;
import com.openbox.realcomm.R;

public class InfoFragment extends BaseFragment implements OnClickListener
{
	public static final String TAG = "infoFragmentTag";

	private static final String VERSION_PREFIX = "Version: ";
	private static final String LAST_UPDATE_DATE_PREFIX = "Last updated: ";
	private static final String LAST_UPDATE_NOT_UPDATED = "-";
	private static final String AUTHOR_HYPERLINK = "<a href=\"http://earthincolors.wordpress.com/\">Moyan Brenn</a>";

	private TextView lastUpdatedTextView;

	public static InfoFragment newInstance()
	{
		InfoFragment fragment = new InfoFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_info, container, false);

		Button viewProfileButton = (Button) view.findViewById(R.id.infoViewProfileButton);
		viewProfileButton.setOnClickListener(this);

		TextView developedByTextView = (TextView) view.findViewById(R.id.infoDevelopedByTextView);
		TextView versionTextView = (TextView) view.findViewById(R.id.infoVersionTextView);
		this.lastUpdatedTextView = (TextView) view.findViewById(R.id.infoLastUpdatedTextView);
		TextView courtesyOfTextView = (TextView) view.findViewById(R.id.infoCourtesyOfTextView);
		TextView authorLink = (TextView) view.findViewById(R.id.infoAuthorLink);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();
		developedByTextView.setTypeface(application.getExo2Font());
		versionTextView.setTypeface(application.getExo2Font());
		lastUpdatedTextView.setTypeface(application.getExo2Font());
		courtesyOfTextView.setTypeface(application.getExo2Font());
		authorLink.setTypeface(application.getExo2Font());

		populateVersionText(versionTextView);
		populateLastUpdatedText();
		populateAuthorLinkText(authorLink);

		return view;
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.infoViewProfileButton)
		{
			goToOpenBoxProfile();
		}
	}

	@Override
	public void onDataChanged()
	{
		super.onDataChanged();

		populateLastUpdatedText();
	}

	private void populateLastUpdatedText()
	{
		Long lastUpdatedDateLong = SharedPreferencesManager.getLastUpdateDate(getActivity());
		if (lastUpdatedDateLong != SharedPreferencesManager.DEFAULT_LONG_VALUE)
		{
			DateFormat dateFormat = DateFormat.getDateInstance();
			SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
			this.lastUpdatedTextView.setText(LAST_UPDATE_DATE_PREFIX + dateFormat.format(lastUpdatedDateLong) + " " + timeFormat.format(lastUpdatedDateLong));
		}
		else
		{
			this.lastUpdatedTextView.setText(LAST_UPDATE_DATE_PREFIX + LAST_UPDATE_NOT_UPDATED);
		}
	}

	private void populateAuthorLinkText(TextView authorLink)
	{
		authorLink.setText(Html.fromHtml(AUTHOR_HYPERLINK));
		authorLink.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void populateVersionText(TextView versionTextView)
	{
		try
		{
			PackageInfo pInfo;
			pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			versionTextView.setText(VERSION_PREFIX + pInfo.versionName);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private void goToOpenBoxProfile()
	{
		if (getActivityInterface() != null && getDataInterface() != null)
		{
			BoothModel openBoxModel = getDataInterface().getBoothModelForCompanyName(getResources().getString(R.string.openBoxCompanyName));
			if (openBoxModel != null)
			{
				getActivityInterface().setSelectedBooth(openBoxModel.getBoothId(), openBoxModel.getCompanyId());
				getActivityInterface().changePage(RealcommPage.PROFILE_PAGE);

				if (getParentFragment() != null && getParentFragment() instanceof DialogFragment)
				{
					DialogFragment dialogFragment = (DialogFragment) getParentFragment();
					dialogFragment.dismiss();
				}
			}
		}
	}
}

package com.openbox.realcomm.dialogfragments;

import com.openbox.realcomm.fragments.InfoFragment;
import com.openbox.realcomm.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm.R;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InfoDialogFragment extends DialogFragment implements DataChangedCallbacks
{
	public static final String TAG = "infoDialogFragmentTag";

	private DataChangedCallbacks infoListener;

	public static InfoDialogFragment newInstance()
	{
		InfoDialogFragment fragment = new InfoDialogFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, getTheme());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.dialog_fragment_info, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		createInfoFragment();
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.infoListener = null;
	}

	@Override
	public void onDataLoaded()
	{
		// Stub
	}

	@Override
	public void onDataChanged()
	{
		if (this.infoListener != null)
		{
			this.infoListener.onDataChanged();
		}
	}

	@Override
	public void onBeaconsUpdated()
	{
		// Stub
	}

	private void createInfoFragment()
	{
		InfoFragment infoFragment = (InfoFragment) getChildFragmentManager().findFragmentByTag(InfoFragment.TAG);
		if (infoFragment == null)
		{
			infoFragment = InfoFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.infoFragmentContainer, infoFragment, InfoFragment.TAG).commitAllowingStateLoss();

			this.infoListener = infoFragment;
		}
	}
}

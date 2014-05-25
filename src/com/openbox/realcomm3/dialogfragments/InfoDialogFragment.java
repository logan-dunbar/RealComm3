package com.openbox.realcomm3.dialogfragments;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.fragments.InfoFragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InfoDialogFragment extends DialogFragment
{
	public static final String TAG = "infoDialogFragmentTag";

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

	private void createInfoFragment()
	{
		InfoFragment infoFragment = (InfoFragment) getChildFragmentManager().findFragmentByTag(InfoFragment.TAG);
		if (infoFragment == null)
		{
			infoFragment = InfoFragment.newInstance();
			getChildFragmentManager().beginTransaction().add(R.id.infoFragmentContainer, infoFragment, InfoFragment.TAG).commit();
		}
	}
}

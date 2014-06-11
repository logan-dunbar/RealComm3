package com.openbox.realcomm.fragments;

import java.util.Arrays;
import java.util.List;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.base.BaseFragment;
import com.openbox.realcomm.controls.ClearableEditText;
import com.openbox.realcomm.database.models.BoothModel;
import com.openbox.realcomm.utilities.adapters.BoothListAdapter;
import com.openbox.realcomm.utilities.enums.AppMode;
import com.openbox.realcomm.utilities.enums.BoothSortMode;
import com.openbox.realcomm.utilities.enums.RealcommPage;
import com.openbox.realcomm.utilities.helpers.ClearFocusTouchListener;
import com.openbox.realcomm.R;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class BoothListFragment extends BaseFragment implements
	TextWatcher,
	OnItemClickListener,
	OnClickListener
{
	public static final String TAG = "boothListFragment";

	private static final String BOOTH_SORT_MODE_KEY = "boothSortModeKey";

	private LinearLayout boothListSortModeContainer;
	private Button aToZButton;
	private Button nearMeButton;

	private ClearableEditText boothFilter;
	private ListView boothListView;
	private BoothListAdapter boothAdapter;

	private BoothSortMode currentSortMode;

	public static BoothListFragment newInstance()
	{
		BoothListFragment fragment = new BoothListFragment();
		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_booth_list, container, false);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		this.boothListSortModeContainer = (LinearLayout) view.findViewById(R.id.boothListSortModeContainer);
		this.aToZButton = (Button) view.findViewById(R.id.boothListAtoZButton);
		this.nearMeButton = (Button) view.findViewById(R.id.boothListNearMeButton);
		this.aToZButton.setText(BoothSortMode.A_TO_Z.getDisplayName());
		this.nearMeButton.setText(BoothSortMode.NEAR_ME.getDisplayName());
		this.aToZButton.setOnClickListener(this);
		this.nearMeButton.setOnClickListener(this);
		this.aToZButton.setTypeface(application.getExo2Font());
		this.nearMeButton.setTypeface(application.getExo2Font());

		this.boothFilter = (ClearableEditText) view.findViewById(R.id.boothFilter);
		this.boothFilter.addTextChangedListener(this);
		this.boothFilter.setTypeface(application.getExo2Font());

		this.boothListView = (ListView) view.findViewById(R.id.boothListView);
		this.boothListView.setOnItemClickListener(this);

		this.boothListView.setOnTouchListener(new ClearFocusTouchListener(getActivity(), this));

		this.boothAdapter = new BoothListAdapter(getActivity(), (RealCommApplication) getActivity().getApplication());
		this.boothListView.setAdapter(this.boothAdapter);

		this.currentSortMode = BoothSortMode.NEAR_ME;
		if (savedInstanceState != null)
		{
			this.currentSortMode = (BoothSortMode) savedInstanceState.getSerializable(BOOTH_SORT_MODE_KEY);
		}

		toggleSortContainerVisibility();
		updateButtons();
		updateView();

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putSerializable(BOOTH_SORT_MODE_KEY, this.currentSortMode);
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);

		if (this.boothFilter != null)
		{
			this.boothFilter.setEnabled(!hidden);
		}
	}

	/**********************************************************************************************
	 * Data Changed Callbacks Implements
	 **********************************************************************************************/
	@Override
	public void onDataLoaded()
	{
		updateView();
	}

	@Override
	public void onDataChanged()
	{
		updateView();
	}

	@Override
	public void onBeaconsUpdated()
	{
		updateView();
	}

	/**********************************************************************************************
	 * Click Implements
	 **********************************************************************************************/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		BoothModel booth = this.boothAdapter.getItem(position);
		if (getActivityInterface() != null && booth != null)
		{
			getActivityInterface().setSelectedBooth(booth.getBoothId(), booth.getCompanyId());
			getActivityInterface().changePage(RealcommPage.PROFILE_PAGE);
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == BoothSortMode.A_TO_Z.getButtonId() && this.currentSortMode != BoothSortMode.A_TO_Z)
		{
			this.currentSortMode = BoothSortMode.A_TO_Z;
			updateButtons();
			updateView();
		}
		else if (v.getId() == BoothSortMode.NEAR_ME.getButtonId() && this.currentSortMode != BoothSortMode.NEAR_ME)
		{
			this.currentSortMode = BoothSortMode.NEAR_ME;
			updateButtons();
			updateView();
		}
	}

	/**********************************************************************************************
	 * Text Watcher Implements
	 **********************************************************************************************/
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
		// Not needed
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		this.boothAdapter.filterItems(s.toString());
	}

	@Override
	public void afterTextChanged(Editable s)
	{
		// Not needed
	}

	/**********************************************************************************************
	 * Clear Focus Interface Implements
	 **********************************************************************************************/
	@Override
	public List<View> getViewsToClearFocus()
	{
		return Arrays.asList((View) this.boothFilter);
	}

	/**********************************************************************************************
	 * App Mode Changed Callbacks Implements
	 **********************************************************************************************/
	@Override
	public void onAppModeChanged(AppMode newAppMode, AppMode previousAppMode)
	{
		toggleSortContainerVisibility();

		if (newAppMode == AppMode.ONLINE)
		{
			this.currentSortMode = BoothSortMode.NEAR_ME;
		}
		else
		{
			this.currentSortMode = BoothSortMode.A_TO_Z;
		}

		updateButtons();
		updateView();
	}

	/**********************************************************************************************
	 * Private Helper Methods
	 **********************************************************************************************/
	private void updateButtons()
	{
		if (this.currentSortMode == BoothSortMode.A_TO_Z)
		{
			this.aToZButton.setSelected(true);
			this.nearMeButton.setSelected(false);
		}
		else if (this.currentSortMode == BoothSortMode.NEAR_ME)
		{
			this.aToZButton.setSelected(false);
			this.nearMeButton.setSelected(true);
		}
	}

	private void updateView()
	{
		if (getDataInterface() != null && this.boothAdapter != null)
		{
			this.boothAdapter.setItems(getDataInterface().getBoothModelList(this.currentSortMode));
		}
	}

	private void toggleSortContainerVisibility()
	{
		if (getAppModeInterface() != null)
		{
			if (getAppModeInterface().getCurrentAppMode() == AppMode.ONLINE)
			{
				this.boothListSortModeContainer.setVisibility(View.VISIBLE);
			}
			else
			{
				this.boothListSortModeContainer.setVisibility(View.GONE);
			}
		}
		else
		{
			this.boothListSortModeContainer.setVisibility(View.GONE);
		}
	}
}

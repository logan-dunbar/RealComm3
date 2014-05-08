package com.openbox.realcomm3.fragments;

import java.util.Arrays;
import java.util.List;

import com.openbox.realcomm3.utilities.enums.BoothSortMode;
import com.openbox.realcomm3.utilities.enums.RealcommPage;
import com.openbox.realcomm3.utilities.helpers.ClearFocusTouchListener;
import com.openbox.realcomm3.utilities.interfaces.BoothListInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;
import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.controls.ClearableEditText;
import com.openbox.realcomm3.database.models.BoothModel;
import com.openbox.realcomm3.utilities.adapters.BoothListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BoothListFragment extends BaseFragment implements
	TextWatcher,
	OnItemClickListener,
	BoothListInterface,
	DataChangedCallbacks
{
	private static final String BOOTH_SORT_MODE_KEY = "boothSortModeKey";

	private ClearableEditText boothFilter;
	private ListView boothListView;
	private BoothListAdapter boothAdapter;

	private BoothSortMode currentSortMode;

	private DataInterface dataInterface;

	public static BoothListFragment newInstance()
	{
		BoothListFragment fragment = new BoothListFragment();
		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		if (activity instanceof DataInterface)
		{
			this.dataInterface = (DataInterface) activity;
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.dataInterface = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_booth_list, container, false);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		this.boothFilter = (ClearableEditText) view.findViewById(R.id.boothFilter);
		this.boothFilter.addTextChangedListener(this);
		this.boothFilter.setTypeface(application.getExo2Font());

		this.boothListView = (ListView) view.findViewById(R.id.boothListView);
		this.boothListView.setOnItemClickListener(this);

		// TODO check this doesn't block going to profile page when selecting
		this.boothListView.setOnTouchListener(new ClearFocusTouchListener(getActivity(), this));

		this.boothAdapter = new BoothListAdapter(getActivity(), (RealCommApplication) getActivity().getApplication());
		this.boothListView.setAdapter(this.boothAdapter);

		// TODO: check this default
		this.currentSortMode = BoothSortMode.NAME;
		if (savedInstanceState != null)
		{
			this.currentSortMode = (BoothSortMode) savedInstanceState.getSerializable(BOOTH_SORT_MODE_KEY);
		}

		// TODO maybe check how many times this is being run, so as not to be wasting processing power
		updateList();

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putSerializable(BOOTH_SORT_MODE_KEY, this.currentSortMode);
	}

	/**********************************************************************************************
	 * Booth List Interface Implements
	 **********************************************************************************************/
	@Override
	public void updateList()
	{
		// TODO maybe check how many times this is being run, so as not to be wasting processing power
		if (this.dataInterface != null && this.boothAdapter != null)
		{
			this.boothAdapter.setItems(this.dataInterface.getBoothModelList(this.currentSortMode));
		}
	}

	@Override
	public void toggleSortMode()
	{
		this.currentSortMode = this.currentSortMode == BoothSortMode.ACCURACY ? BoothSortMode.NAME : BoothSortMode.ACCURACY;
		updateList();
	}

	/**********************************************************************************************
	 * Data Changed Callbacks
	 **********************************************************************************************/
	@Override
	public void onDataLoaded()
	{
		updateList();
	}

	@Override
	public void onDataChanged()
	{
		updateList();
	}

	@Override
	public void onBeaconsUpdated()
	{
		updateList();
	}

	/**********************************************************************************************
	 * OnItemClick Implements
	 **********************************************************************************************/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		BoothModel booth = this.boothAdapter.getItem(position);
		if (getActivityListener() != null)
		{
			getActivityListener().setSelectedCompanyId(booth.getCompanyId());
			getActivityListener().changePage(RealcommPage.PROFILEPAGE);
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
}

package com.openbox.realcomm3.fragments;

import com.openbox.realcomm3.utilities.enums.BoothSortMode;
import com.openbox.realcomm3.utilities.interfaces.BoothListInterface;
import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.controls.ClearableEditText;
import com.openbox.realcomm3.utilities.adapters.BoothListAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BoothListFragment extends Fragment implements TextWatcher, OnItemClickListener, BoothListInterface
{
	private static final String BOOTH_SORT_MODE_KEY = "boothSortModeKey";

	private ClearableEditText boothFilter;
	private ListView boothListView;
	private BoothListAdapter boothAdapter;

	private BoothSortMode currentSortMode;

	public static BoothListFragment newInstance()
	{
		BoothListFragment fragment = new BoothListFragment();
		return fragment;
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

		this.boothAdapter = new BoothListAdapter(getActivity());
		this.boothListView.setAdapter(this.boothAdapter);

		// TODO: check this default
		this.currentSortMode = BoothSortMode.Name;
		if (savedInstanceState != null)
		{
			this.currentSortMode = (BoothSortMode) savedInstanceState.getSerializable(BOOTH_SORT_MODE_KEY);
		}

		// TODO do something here
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putSerializable(BOOTH_SORT_MODE_KEY, this.currentSortMode);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		this.boothAdapter.filterItems(s.toString());
	}

	@Override
	public void afterTextChanged(Editable s)
	{
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{

	}

	@Override
	public View getViewToClearFocus()
	{
		// TODO Auto-generated method stub
		return this.boothFilter;
	}

	@Override
	public void updateList()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void toggleSortMode()
	{
		// TODO Auto-generated method stub
	}
}

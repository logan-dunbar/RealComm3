package com.openbox.realcomm3.utilities.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.database.models.BoothDistanceModel;
import com.openbox.realcomm3.database.models.BoothListModel;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BoothListAdapter extends ArrayAdapter<BoothListModel>
{
	private LayoutInflater layoutInflater;
	private Resources resources;

	private List<BoothListModel> fullBoothList;
	private String filter;

	public BoothListAdapter(Context context)
	{
		super(context, 0);
		this.resources = context.getResources();
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setItems(List<BoothListModel> data)
	{
		if (data != null)
		{
			this.fullBoothList = data;
		}

		doFiltering();
	}

	public void filterItems(String filter)
	{
		this.filter = filter;
		doFiltering();
	}

	private void doFiltering()
	{
		clear();

		if (this.filter == null || this.filter.length() == 0)
		{
			addAll(this.fullBoothList);
		}
		else
		{
			List<BoothListModel> filteredList = new ArrayList<>();
			for (BoothListModel model : this.fullBoothList)
			{
				if (model.getCompanyName().toUpperCase(Locale.ENGLISH).contains(this.filter.toUpperCase(Locale.ENGLISH)) ||
					String.valueOf(model.getBoothNumber()).contains(this.filter))
				{
					filteredList.add(model);
				}
			}

			addAll(filteredList);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row;
		ViewHolder holder;

		if (convertView == null)
		{
			row = this.layoutInflater.inflate(R.layout.list_item_booth, null);
			holder = new ViewHolder();
			holder.text = (TextView) row.findViewById(R.id.companyNameTextView);
			row.setTag(holder);
		}
		else
		{
			row = convertView;
			holder = (ViewHolder) row.getTag();
		}

		BoothListModel model = getItem(position);
		holder.text.setText(model.getCompanyName());

		// TODO booth number

		GradientDrawable circle = (GradientDrawable) holder.text.getCompoundDrawables()[0];
		int colorId = BoothDistanceModel.getProximityRegion(model.getAccuracy()).getColorId();
		circle.setColor(this.resources.getColor(colorId));
		
		return row;
	}

	static class ViewHolder
	{
		TextView text;
	}
}

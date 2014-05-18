package com.openbox.realcomm3.utilities.adapters;

import java.util.List;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.database.models.TalkModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TalkListAdapter extends ArrayAdapter<TalkModel>
{
	private LayoutInflater layoutInflater;
	private RealCommApplication application;

	public TalkListAdapter(Context context, RealCommApplication application)
	{
		super(context, 0);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.application = application;
	}

	public void setItems(List<TalkModel> talkList)
	{
		clear();
		if (talkList != null)
		{
			addAll(talkList);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		View row;
		if (null == convertView)
		{
			row = this.layoutInflater.inflate(R.layout.list_item_talk, null);
			holder = new ViewHolder();

			holder.talkTime = (TextView) row.findViewById(R.id.talkTime);
			holder.talkName = (TextView) row.findViewById(R.id.talkName);

			holder.talkTime.setTypeface(this.application.getExo2FontBold());
			holder.talkName.setTypeface(this.application.getExo2Font());

			row.setTag(holder);
		}
		else
		{
			row = convertView;
			holder = (ViewHolder) row.getTag();
		}

		TalkModel talk = getItem(position);

		holder.talkTime.setText(talk.getTalkTimeString());
		holder.talkName.setText(talk.getName());

		return row;
	}

	@Override
	public boolean isEnabled(int position)
	{
		return false;
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return false;
	}

	class ViewHolder
	{
		TextView talkTime;
		TextView talkName;
	}
}

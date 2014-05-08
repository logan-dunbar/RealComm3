package com.openbox.realcomm3.utilities.adapters;

import java.util.List;

import com.openbox.realcomm3.R;
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

	public TalkListAdapter(Context context)
	{
		super(context, 0);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			holder.talkName = (TextView) row.findViewById(R.id.talkName);
			row.setTag(holder);
		}
		else
		{
			row = convertView;
			holder = (ViewHolder) row.getTag();
		}

		TalkModel talk = getItem(position);

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
		TextView talkName;
	}
}

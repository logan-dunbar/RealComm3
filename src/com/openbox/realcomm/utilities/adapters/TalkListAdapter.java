package com.openbox.realcomm.utilities.adapters;

import java.util.List;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.database.models.TalkModel;
import com.openbox.realcomm.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TalkListAdapter extends ArrayAdapter<TalkModel>
{
	private static final String TALK_TRACK_PREFIX = "Track: ";

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
			holder.talkTrack = (TextView) row.findViewById(R.id.talkTrack);
			holder.talkName = (TextView) row.findViewById(R.id.talkName);

			holder.talkTime.setTypeface(this.application.getExo2FontBold());
			holder.talkTrack.setTypeface(this.application.getExo2FontBold());
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
		holder.talkTrack.setText(TALK_TRACK_PREFIX + talk.getTalkTrack());
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
		TextView talkTrack;
		TextView talkName;
	}
}

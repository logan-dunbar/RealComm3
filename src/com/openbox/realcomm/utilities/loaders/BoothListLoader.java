package com.openbox.realcomm.utilities.loaders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.openbox.realcomm.database.DatabaseManager;
import com.openbox.realcomm.database.models.BoothListModel;
import com.openbox.realcomm.database.objects.Booth;
import com.openbox.realcomm.database.objects.BoothContact;
import com.openbox.realcomm.database.objects.Company;
import com.openbox.realcomm.database.objects.Contact;

public class BoothListLoader extends AsyncTaskLoader<List<BoothListModel>>
{
	private List<BoothListModel> boothList;

	public BoothListLoader(Context context)
	{
		super(context);
	}

	@Override
	protected void onStartLoading()
	{
		if (this.boothList == null)
		{
			forceLoad();
		}
		else
		{
			deliverResult(this.boothList);
		}
	}

	@Override
	protected void onStopLoading()
	{
		cancelLoad();
	}

	@Override
	public void onContentChanged()
	{
		forceLoad();
	}

	@Override
	public void reset()
	{
		super.reset();

		onStopLoading();

		this.boothList = null;
	}

	@Override
	public List<BoothListModel> loadInBackground()
	{
		List<BoothListModel> list = new ArrayList<>();

		try
		{
			List<Booth> booths = DatabaseManager.getInstance().getAll(Booth.class);

			for (Booth booth : booths)
			{
				Company company = null;

				List<BoothContact> boothContacts = DatabaseManager.getInstance().getForWhereEquals(BoothContact.class, Booth.BOOTH_ID_COLUMN_NAME,
					booth.getBoothId());

				List<Integer> contactIds = new ArrayList<Integer>();
				for (BoothContact boothContact : boothContacts)
				{
					contactIds.add(boothContact.getContactId());
				}

				List<Contact> contacts = DatabaseManager.getInstance().getForWhereIn(Contact.class, Contact.CONTACT_ID_COLUMN_NAME, contactIds);

				List<Integer> companyIds = new ArrayList<Integer>();
				for (Contact contact : contacts)
				{
					companyIds.add(contact.getCompanyId());
				}

				// This does a distinct
				companyIds = new ArrayList<Integer>(new HashSet<Integer>(companyIds));

				// Should only be one company, might need to cater for multiple, but ultimate edge case
				if (companyIds.size() > 0)
				{
					int companyId = companyIds.get(0);

					// Get the company
					company = DatabaseManager.getInstance().getForId(Company.class, companyId);
				}

				list.add(new BoothListModel(booth, company));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return list;
	}

	@Override
	public void deliverResult(List<BoothListModel> data)
	{
		this.boothList = data;

		if (isStarted())
		{
			super.deliverResult(this.boothList);
		}
	}
}

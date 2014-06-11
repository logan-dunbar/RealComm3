package com.openbox.realcomm.utilities.loaders;

import java.sql.SQLException;
import java.util.List;

import com.openbox.realcomm.database.DatabaseManager;
import com.openbox.realcomm.database.models.ContactModel;
import com.openbox.realcomm.database.objects.Contact;
import com.openbox.realcomm.database.objects.ContactImage;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class ContactLoader extends AsyncTaskLoader<ContactModel>
{
	private ContactModel contactModel;

	private int contactId;

	public ContactLoader(Context context, int contactId)
	{
		super(context);
		
		this.contactId = contactId;
	}

	@Override
	protected void onStartLoading()
	{
		if (this.contactModel == null)
		{
			forceLoad();
		}
		else
		{
			deliverResult(this.contactModel);
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

		this.contactModel = null;
	}

	@Override
	public ContactModel loadInBackground()
	{
		// TODO
		ContactModel contactModel = null;

		try
		{
			ContactImage contactImage = null;

			Contact contact = DatabaseManager.getInstance().getForId(Contact.class, this.contactId);
			List<ContactImage> contactImages = DatabaseManager.getInstance().getForWhereEquals(ContactImage.class, Contact.CONTACT_ID_COLUMN_NAME,
				this.contactId);

			// Should only ever be 1... Should...
			if (contactImages.size() > 0)
			{
				contactImage = contactImages.get(0);
			}

			contactModel = new ContactModel(contact, contactImage);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return contactModel;
	}

	@Override
	public void deliverResult(ContactModel data)
	{
		this.contactModel = data;

		if (isStarted())
		{
			super.deliverResult(this.contactModel);
		}
	}
}

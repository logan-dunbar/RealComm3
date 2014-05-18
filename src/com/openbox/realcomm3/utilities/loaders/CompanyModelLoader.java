package com.openbox.realcomm3.utilities.loaders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.openbox.realcomm3.database.DatabaseManager;
import com.openbox.realcomm3.database.models.CompanyModel;
import com.openbox.realcomm3.database.models.ContactModel;
import com.openbox.realcomm3.database.objects.Booth;
import com.openbox.realcomm3.database.objects.Company;
import com.openbox.realcomm3.database.objects.CompanyLogo;
import com.openbox.realcomm3.database.objects.Contact;
import com.openbox.realcomm3.database.objects.ContactImage;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class CompanyModelLoader extends AsyncTaskLoader<CompanyModel>
{
	private CompanyModel companyModel;

	private int boothId;
	private int companyId;

	public CompanyModelLoader(Context context, int boothId, int companyId)
	{
		super(context);
		this.boothId = boothId;
		this.companyId = companyId;
	}

	@Override
	protected void onStartLoading()
	{
		if (this.companyModel == null)
		{
			forceLoad();
		}
		else
		{
			deliverResult(this.companyModel);
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

		this.companyModel = null;
	}

	@Override
	public CompanyModel loadInBackground()
	{
		CompanyModel model = null;
		try
		{
			// Company Info
			CompanyLogo companyLogo = null;

			Booth booth = DatabaseManager.getInstance().getForId(Booth.class, this.boothId);
			Company company = DatabaseManager.getInstance().getForId(Company.class, this.companyId);
			List<CompanyLogo> logoList = DatabaseManager.getInstance().getForWhereEquals(CompanyLogo.class, Company.COMPANY_ID_COLUMN_NAME, this.companyId);

			// Should only ever be one... Should...
			if (logoList.size() > 0)
			{
				companyLogo = logoList.get(0);
			}

			// Contacts
			List<ContactModel> contactModelList = new ArrayList<>();
			List<Contact> contacts = DatabaseManager.getInstance().getForWhereEquals(Contact.class, Company.COMPANY_ID_COLUMN_NAME, this.companyId);
			for (Contact contact : contacts)
			{
				ContactImage contactImage = null;
				List<ContactImage> contactImages = DatabaseManager.getInstance().getForWhereEquals(ContactImage.class, Contact.CONTACT_ID_COLUMN_NAME,
					contact.getContactId());

				// Should only ever be one... Should...
				if (contactImages.size() > 0)
				{
					contactImage = contactImages.get(0);
				}

				contactModelList.add(new ContactModel(contact, contactImage));
			}

			model = new CompanyModel(booth, company, companyLogo, contactModelList);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return model;
	}

	@Override
	public void deliverResult(CompanyModel data)
	{
		this.companyModel = data;

		if (isStarted())
		{
			super.deliverResult(this.companyModel);
		}
	}
}

package com.openbox.realcomm3.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.CompanyModel;
import com.openbox.realcomm3.database.models.ContactModel;
import com.openbox.realcomm3.database.objects.Contact;
import com.openbox.realcomm3.utilities.helpers.BitmapHelper;
import com.openbox.realcomm3.utilities.loaders.ContactLoader;

public class ContactFragment extends BaseProfileFragment
{
	// private static final int CONTACT_LOADER_ID = 1;

	// private ContactModel contactModel;

	private ImageView contactImage;
	private TextView contactName;
	private TextView contactDetails;

	public static ContactFragment newInstance(int contactId)
	{
		ContactFragment fragment = new ContactFragment();

		Bundle args = new Bundle();
		args.putInt(Contact.CONTACT_ID_COLUMN_NAME, contactId);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// initContactLoader();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_contact, container, false);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		this.contactImage = (ImageView) view.findViewById(R.id.contactImageView);
		this.contactName = (TextView) view.findViewById(R.id.contactNameTextView);
		this.contactDetails = (TextView) view.findViewById(R.id.contactDetailsTextView);

		this.contactName.setTypeface(application.getExo2FontBold());
		this.contactDetails.setTypeface(application.getExo2Font());

		updateView();

		return view;
	}

	// private void initContactLoader()
	// {
	// getLoaderManager().initLoader(CONTACT_LOADER_ID, null, this.contactLoader);
	// }

	private void updateView()
	{
		CompanyModel companyModel = getCompanyModel();
		if (companyModel != null)
		{
			ContactModel contactModel = companyModel.getContact(getArguments().getInt(Contact.CONTACT_ID_COLUMN_NAME));

			if (contactModel != null)
			{
				// TODO Do sizing stuff?
				float radius = getResources().getDimension(R.dimen.defaultCornerRadius);
				int width = (int) getResources().getDimension(R.dimen.contactImageSize);
				int height = (int) getResources().getDimension(R.dimen.contactImageSize);

				// TODO need better here...
				if (contactModel.getContactImage() != null)
				{

					Bitmap contactImage = BitmapHelper.getRoundedBitmap(contactModel.getContactImage(), width, height, radius);

					this.contactImage.setImageBitmap(contactImage);
				}

				this.contactName.setText(contactModel.getDisplayName());
				this.contactDetails.setText(contactModel.getDetails());
			}
		}
	}

	// private void finishContactLoad(ContactModel results)
	// {
	// this.contactModel = results;
	// updateView();
	// }

	// private LoaderCallbacks<ContactModel> contactLoader = new LoaderCallbacks<ContactModel>()
	// {
	//
	// @Override
	// public Loader<ContactModel> onCreateLoader(int loaderId, Bundle bundle)
	// {
	// return new ContactLoader(getActivity(), getArguments().getInt(Contact.CONTACT_ID_COLUMN_NAME));
	// }
	//
	// @Override
	// public void onLoadFinished(Loader<ContactModel> loader, ContactModel results)
	// {
	// finishContactLoad(results);
	// }
	//
	// @Override
	// public void onLoaderReset(Loader<ContactModel> loader)
	// {
	// }
	// };
}

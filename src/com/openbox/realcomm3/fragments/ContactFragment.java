package com.openbox.realcomm3.fragments;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Intents;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseProfileFragment;
import com.openbox.realcomm3.database.models.ContactModel;
import com.openbox.realcomm3.database.objects.Contact;
import com.openbox.realcomm3.utilities.helpers.BitmapHelper;
import com.openbox.realcomm3.utilities.helpers.LogHelper;
import com.openbox.realcomm3.utilities.helpers.StringHelper;
import com.openbox.realcomm3.utilities.helpers.ToastHelper;

public class ContactFragment extends BaseProfileFragment
{
	private ImageView contactImage;
	private TextView contactName;
	private TextView contactDetails;
	private ImageView addContactImageView;

	private ContactModel contactModel;

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_contact, container, false);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		this.contactImage = (ImageView) view.findViewById(R.id.contactImageView);
		this.contactName = (TextView) view.findViewById(R.id.contactNameTextView);
		this.contactDetails = (TextView) view.findViewById(R.id.contactDetailsTextView);
		this.addContactImageView = (ImageView) view.findViewById(R.id.addContactImageView);

		this.contactName.setTypeface(application.getExo2FontBold());
		this.contactDetails.setTypeface(application.getExo2Font());

		updateView();

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		LogHelper.Log("onViewCreated() has state = " + String.valueOf(savedInstanceState != null));
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState)
	{
		LogHelper.Log("onViewStateRestored() has state = " + String.valueOf(savedInstanceState != null));
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		checkContactExists();
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent data)
	// {
	// LogHelper.Log("onActivityResult() in ContactFragment");
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// switch (requestCode)
	// {
	// case SAVE_CONTACT_REQUEST:
	// if (resultCode == FragmentActivity.RESULT_OK)
	// {
	// ToastHelper.showLongMessage(getActivity(), "Contact saved successfully");
	// checkContactExists();
	// }
	// else
	// {
	// LogHelper.Log("About to delete contact");
	// DeleteContactTask deleteTask = new DeleteContactTask();
	// deleteTask.execute();
	// }
	// break;
	// default:
	// break;
	// }
	// }

	private ContactModel getContactModel()
	{
		if (getCompany() == null)
		{
			return null;
		}

		if (this.contactModel == null)
		{
			this.contactModel = getCompany().getContact(getArguments().getInt(Contact.CONTACT_ID_COLUMN_NAME));
		}

		return this.contactModel;
	}

	private void updateView()
	{
		if (getContactModel() != null)
		{
			if (getContactModel().getContactImage() != null)
			{
				float radius = getResources().getDimension(R.dimen.defaultCornerRadius);
				int height = (int) getResources().getDimension(R.dimen.contactImageSize);

				double aspectRatio = ((double) getContactModel().getContactImage().getWidth()) / getContactModel().getContactImage().getHeight();
				int width = (int) Math.round(height * aspectRatio);

				Bitmap contactImage = BitmapHelper.getRoundedBitmap(getContactModel().getContactImage(), width, height, radius);
				this.contactImage.setImageBitmap(contactImage);
			}

			this.contactName.setText(getContactModel().getDisplayName());
			this.contactDetails.setText(getContactModel().getDetails());

			checkContactExists();
		}
	}

	private void checkContactExists()
	{
		if (getCompany() != null && getContactModel() != null)
		{
			CheckContactExistsTask task = new CheckContactExistsTask(
				getContactModel().getFirstName(),
				getContactModel().getLastName(),
				getCompany().getName());
			task.execute();
		}
	}

	private void insertContact()
	{
		if (getCompany() != null && getContactModel() != null)
		{
			Intent intent = new Intent(Intents.Insert.ACTION);
			intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

			ArrayList<ContentValues> data = new ArrayList<ContentValues>();

			intent.putExtra(Intents.Insert.NAME, getContactModel().getDisplayName());
			intent.putExtra(Intents.Insert.COMPANY, getCompany().getName());

			if (!StringHelper.isNullOrEmpty(getContactModel().getContactNumber()))
			{
				intent.putExtra(Intents.Insert.PHONE, getContactModel().getContactNumber());
				intent.putExtra(Intents.Insert.PHONE_TYPE, Phone.TYPE_WORK);
			}

			if (!StringHelper.isNullOrEmpty(getContactModel().getEmail()))
			{
				intent.putExtra(Intents.Insert.EMAIL, getContactModel().getEmail());
				intent.putExtra(Intents.Insert.EMAIL_TYPE, Email.TYPE_WORK);
			}

			if (!StringHelper.isNullOrEmpty(getContactModel().getJobPosition()))
			{
				intent.putExtra(Intents.Insert.JOB_TITLE, getContactModel().getJobPosition());
			}

			Bitmap contactImage = getContactModel().getContactImage();
			if (contactImage != null)
			{
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				contactImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);

				ContentValues photoRow = new ContentValues();
				photoRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
				photoRow.put(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray());
				data.add(photoRow);
			}

			intent.putParcelableArrayListExtra(Intents.Insert.DATA, data);

			// Fix for Android 4.0+ going back to contact listing on save
			intent.putExtra("finishActivityOnSaveCompleted", true);

			startActivity(intent);
		}
	}

	class CheckContactExistsTask extends AsyncTask<Void, Void, Boolean>
	{
		private String firstName;
		private String lastName;
		private String companyName;

		public CheckContactExistsTask(String firstName, String lastName, String companyName)
		{
			this.firstName = firstName;
			this.lastName = lastName;
			this.companyName = companyName;
		}

		@Override
		protected Boolean doInBackground(Void... params)
		{
			boolean exists = false;
			Cursor nameCursor = null;
			Cursor companyCursor = null;

			String[] nameProjection =
			{ ContactsContract.Data.CONTACT_ID };

			String nameSelectionClause =
				ContactsContract.Data.MIMETYPE + " = ? AND " +
					ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME + " = ? AND " +
					ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME + " = ?";

			String[] nameSelectionArgs =
			{ ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, firstName, lastName };

			nameCursor = getActivity().getContentResolver().query(
				ContactsContract.Data.CONTENT_URI,
				nameProjection,
				nameSelectionClause,
				nameSelectionArgs,
				null);

			if (nameCursor != null && nameCursor.getCount() > 0)
			{
				String[] companyProjection =
				{ ContactsContract.Data.CONTACT_ID };

				while (nameCursor.moveToNext())
				{
					int contactId = nameCursor.getInt(nameCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));

					String companySelectionClause =
						ContactsContract.Data.CONTACT_ID + " = " + String.valueOf(contactId) + " AND " +
							ContactsContract.Data.MIMETYPE + " = ? AND " +
							ContactsContract.CommonDataKinds.Organization.COMPANY + " = ?";

					String[] companySelectionArgs =
					{
						ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
						companyName
					};

					companyCursor = getActivity().getContentResolver().query(
						ContactsContract.Data.CONTENT_URI,
						companyProjection,
						companySelectionClause,
						companySelectionArgs,
						null);

					if (companyCursor != null && companyCursor.getCount() > 0)
					{
						exists = true;
						break;
					}
				}
			}

			if (nameCursor != null)
			{
				nameCursor.close();
			}

			if (companyCursor != null)
			{
				companyCursor.close();
			}

			return exists;
		}

		@Override
		protected void onPostExecute(Boolean contactExists)
		{
			// Back on UIThread
			if (contactExists)
			{
				addContactImageView.setImageResource(R.drawable.icon_added_contact);
				addContactImageView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						ToastHelper.showLongMessage(getActivity(), "Contact already exists");
					}
				});
			}
			else
			{
				addContactImageView.setImageResource(R.drawable.icon_add_contact);
				addContactImageView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// Prevent multiple adds
						addContactImageView.setEnabled(false);

						insertContact();
					}
				});
			}

			addContactImageView.setVisibility(View.VISIBLE);
			addContactImageView.setEnabled(true);
		}
	}
}

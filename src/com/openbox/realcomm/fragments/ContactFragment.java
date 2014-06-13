package com.openbox.realcomm.fragments;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Intents;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.base.BaseProfileFragment;
import com.openbox.realcomm.database.models.ContactModel;
import com.openbox.realcomm.database.objects.Contact;
import com.openbox.realcomm.utilities.helpers.BitmapHelper;
import com.openbox.realcomm.utilities.helpers.StringHelper;
import com.openbox.realcomm.utilities.helpers.ToastHelper;
import com.openbox.realcomm.R;

public class ContactFragment extends BaseProfileFragment
{
	private ImageView contactImage;
	private TextView contactName;
	private ImageView addContactImageView;
	// private TextView contactDetails;

	private TextView contactJobTitle;
	private TextView contactPhoneNumber;
	private TextView contactEmail;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_contact, container, false);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		this.contactImage = (ImageView) view.findViewById(R.id.contactImageView);
		this.contactName = (TextView) view.findViewById(R.id.contactNameTextView);
		// this.contactDetails = (TextView) view.findViewById(R.id.contactDetailsTextView);
		this.addContactImageView = (ImageView) view.findViewById(R.id.addContactImageView);

		this.contactJobTitle = (TextView) view.findViewById(R.id.contactJobTitleTextView);
		this.contactPhoneNumber = (TextView) view.findViewById(R.id.contactPhoneNumberTextView);
		this.contactEmail = (TextView) view.findViewById(R.id.contactEmailTextView);

		this.contactName.setTypeface(application.getExo2FontBold());
		// this.contactDetails.setTypeface(application.getExo2Font());
		this.contactJobTitle.setTypeface(application.getExo2Font());
		this.contactPhoneNumber.setTypeface(application.getExo2Font());
		this.contactEmail.setTypeface(application.getExo2Font());

		updateView();

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		checkContactExists();
	}

	private ContactModel getContactModel()
	{
		// TODO check if this is causing the contact to get 'stuck'. Don't think so though,
		// as the adapter's list should be recreated each time data changes in CompanyContactsFragment
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

			if (!StringHelper.isNullOrEmpty(getContactModel().getJobPosition()))
			{
				this.contactJobTitle.setText(getContactModel().getJobPosition());
			}
			else
			{
				this.contactJobTitle.setVisibility(View.GONE);
			}

			if (!StringHelper.isNullOrEmpty(getContactModel().getContactNumber()))
			{
				SpannableStringBuilder ssb = new SpannableStringBuilder();
				ssb.append(getContactModel().getContactNumber());
				ssb.setSpan(new URLSpan("#"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				this.contactPhoneNumber.setText(ssb, TextView.BufferType.SPANNABLE);

				this.contactPhoneNumber.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", getContactModel().getContactNumber(), null));
						startActivity(callIntent);
					}
				});
			}
			else
			{
				this.contactPhoneNumber.setVisibility(View.GONE);
			}

			// Could not use Linkify, as it is broken and would somehow retain it's last value when coming back
			// from attempting to send an email. It could be to do with the ViewPager and Adapter as well.
			if (!StringHelper.isNullOrEmpty(getContactModel().getEmail()))
			{
				SpannableStringBuilder ssb = new SpannableStringBuilder();
				ssb.append(getContactModel().getEmail());
				ssb.setSpan(new URLSpan("#"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				this.contactEmail.setText(ssb, TextView.BufferType.SPANNABLE);

				this.contactEmail.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getContactModel().getEmail(), null));
						startActivity(Intent.createChooser(emailIntent, "Send email via"));
					}
				});
			}
			else
			{
				this.contactEmail.setVisibility(View.GONE);
			}

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

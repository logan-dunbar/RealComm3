package com.openbox.realcomm3.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
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

public class ContactFragment extends BaseProfileFragment
{
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

	private void updateView()
	{
		CompanyModel companyModel = getCompany();
		if (companyModel != null)
		{
			ContactModel contactModel = companyModel.getContact(getArguments().getInt(Contact.CONTACT_ID_COLUMN_NAME));

			if (contactModel != null)
			{
				if (contactModel.getContactImage() != null)
				{
					this.contactImage.setVisibility(View.VISIBLE);

					float radius = getResources().getDimension(R.dimen.defaultCornerRadius);
					int height = (int) getResources().getDimension(R.dimen.contactImageSize);

					double aspectRatio = ((double) contactModel.getContactImage().getWidth()) / contactModel.getContactImage().getHeight();
					int width = (int) Math.round(height * aspectRatio);

					Bitmap contactImage = BitmapHelper.getRoundedBitmap(contactModel.getContactImage(), width, height, radius);
					this.contactImage.setImageBitmap(contactImage);
				}
				else
				{
					this.contactImage.setVisibility(View.GONE);
				}

				this.contactName.setText(contactModel.getDisplayName());
				this.contactDetails.setText(contactModel.getDetails());
			}
		}
	}
}

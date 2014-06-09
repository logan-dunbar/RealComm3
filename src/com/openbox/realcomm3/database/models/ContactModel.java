package com.openbox.realcomm3.database.models;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.openbox.realcomm3.database.objects.Contact;
import com.openbox.realcomm3.database.objects.ContactImage;
import com.openbox.realcomm3.utilities.helpers.StringHelper;

public class ContactModel
{
	private int contactId;
	private String firstName;
	private String lastName;
	private String jobPosition;
	private String email;
	private String contactNumber;

	private Bitmap contactImage;

	public ContactModel(Contact contact, ContactImage contactImage)
	{
		if (contact != null)
		{
			this.contactId = contact.getContactId();
			this.firstName = contact.getFirstName();
			this.lastName = contact.getLastName();
			this.jobPosition = contact.getJobPosition();
			this.email = contact.getEmail();
			this.contactNumber = contact.getContactNumber();
		}

		if (contactImage != null)
		{
			// TODO do sample size decoding
			byte[] imageBytes = contactImage.getImageBytes();
			this.contactImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		}
	}

	public String getDisplayName()
	{
		// TODO First and Last name are non-nullable, but maybe null checks in case that changes
		return getFirstName() + " " + getLastName();
	}

	public String getDetails()
	{
		StringBuilder sb = new StringBuilder();

		List<String> detailsList = new ArrayList<>();
		detailsList.add(getJobPosition());
		detailsList.add(getContactNumber());
		detailsList.add(getEmail());

		String newline = System.getProperty("line.separator");
		String detailsString = StringHelper.join(detailsList, newline);
		sb.append(detailsString);

		return sb.toString();
	}

	public int getContactId()
	{
		return contactId;
	}

	public void setContactId(int contactId)
	{
		this.contactId = contactId;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getJobPosition()
	{
		return jobPosition;
	}

	public void setJobPosition(String jobPosition)
	{
		this.jobPosition = jobPosition;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getContactNumber()
	{
		return contactNumber;
	}

	public void setContactNumber(String contactNumber)
	{
		this.contactNumber = contactNumber;
	}

	public Bitmap getContactImage()
	{
		return contactImage;
	}

	public void setContactImage(Bitmap contactImage)
	{
		this.contactImage = contactImage;
	}

}

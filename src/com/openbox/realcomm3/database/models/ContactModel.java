package com.openbox.realcomm3.database.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.openbox.realcomm3.database.objects.Contact;
import com.openbox.realcomm3.database.objects.ContactImage;

public class ContactModel
{
	private int contactId;
	private String firstName;
	private String lastName;
	private String jobDescription;
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
			this.jobDescription = contact.getJobDescription();
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
		// TODO do null checks etc
		return getFirstName() + " " + getLastName();
	}

	public String getDetails()
	{
		// TODO do null checks etc
		StringBuilder sb = new StringBuilder();

		sb.append(getJobDescription());
		sb.append("\n");

		sb.append(getContactNumber());
		sb.append("\n");

		sb.append(getEmail());

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

	public String getJobDescription()
	{
		return jobDescription;
	}

	public void setJobDescription(String jobDescription)
	{
		this.jobDescription = jobDescription;
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

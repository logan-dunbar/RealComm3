package com.openbox.realcomm3.utilities.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.application.SharedPreferencesManager;
import com.openbox.realcomm3.database.DatabaseManager;
import com.openbox.realcomm3.database.objects.Booth;
import com.openbox.realcomm3.database.objects.BoothContact;
import com.openbox.realcomm3.database.objects.Company;
import com.openbox.realcomm3.database.objects.CompanyLogo;
import com.openbox.realcomm3.database.objects.Contact;
import com.openbox.realcomm3.database.objects.ContactImage;
import com.openbox.realcomm3.database.objects.RealCommDatabase;
import com.openbox.realcomm3.database.objects.Talk;
import com.openbox.realcomm3.database.objects.TalkTrack;
import com.openbox.realcomm3.database.objects.Venue;
import com.openbox.realcomm3.services.WebService;

public class DownloadDatabaseHelper
{
	public static final String FETCH_MOST_RECENT_UPDATE_DATE_API_URL = WebService.ROOT_API_URL + "FetchMostRecentUpdateDate";
	public static final String FETCH_DATABASE_API_URL = WebService.ROOT_API_URL + "FetchRealCommDatabase";

	/**********************************************************************************************
	 * Members
	 **********************************************************************************************/
	private static String serverMostRecentUpdateDate;

	private Context context;
	private Gson gson;
	private JsonParser jsonParser;

	private Boolean updateNeeded = false;

	public Boolean getUpdateNeeded()
	{
		return this.updateNeeded;
	}

	private RealCommDatabase realCommDatabase;

	/**********************************************************************************************
	 * Constructor
	 **********************************************************************************************/
	public DownloadDatabaseHelper(Context context)
	{
		this.context = context;
		this.gson = new GsonBuilder().setDateFormat(RealCommApplication.DOWNLOAD_DATE_FORMAT).create();
		this.jsonParser = new JsonParser();
	}

	/**********************************************************************************************
	 * Public Methods
	 **********************************************************************************************/
	public boolean checkUpdateNeeded()
	{
		try
		{
			// Set up the connection
			URL url = new URL(FETCH_MOST_RECENT_UPDATE_DATE_API_URL);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			// Specify request properties asking for JSON
			urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

			// Get the input stream
			InputStream stream = urlConnection.getInputStream();

			// Get the reader
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

			// TODO maybe save date in DB so we can ship with a date to prevent download on first start
			// Read and convert mostRecentUpdateDate
			this.updateNeeded = readMostRecentUpdateDateAndCompareToSaved(reader);

			if (this.updateNeeded)
			{
				LogHelper.Log("Update required...");
			}

			return true;
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// UnknownHostException comes in here
			e.printStackTrace();
		}

		return false;
	}

	public boolean downloadDatabase()
	{
		// TODO LD - NB Change - com.openbox.realcomm.database is downloaded and completely loaded into memory,
		// and then written, might need to be incremental based on DB size
		try
		{
			LogHelper.Log("Starting database download...");

			// Set up the connection
			URL url = new URL(FETCH_DATABASE_API_URL);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			// Specify request properties asking for JSON
			urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			urlConnection.setRequestProperty("Connection", "Keep-Alive");

			// Get the input stream
			InputStream stream = urlConnection.getInputStream();

			// Get the reader
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

			// Read and convert database
			readRealCommDatabaseJson(reader);

			LogHelper.Log("Download database completed successfully...");
			return true;
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// UnknownHostException comes in here
			e.printStackTrace();
		}

		return false;
	}

	public boolean writeDatabase()
	{
		DatabaseManager databaseManager = DatabaseManager.getInstance();

		try
		{
			databaseManager.deleteAll(Booth.class);
			databaseManager.deleteAll(BoothContact.class);
			databaseManager.deleteAll(Contact.class);
			databaseManager.deleteAll(ContactImage.class);
			databaseManager.deleteAll(Company.class);
			databaseManager.deleteAll(CompanyLogo.class);
			databaseManager.deleteAll(Talk.class);
			databaseManager.deleteAll(TalkTrack.class);
			databaseManager.deleteAll(Venue.class);

			databaseManager.createList(realCommDatabase.getBoothList(), Booth.class, Booth.ID_CLASS);
			databaseManager.createList(realCommDatabase.getBoothContactList(), BoothContact.class, BoothContact.ID_CLASS);
			databaseManager.createList(realCommDatabase.getContactList(), Contact.class, Contact.ID_CLASS);
			databaseManager.createList(realCommDatabase.getContactImageList(), ContactImage.class, ContactImage.ID_CLASS);
			databaseManager.createList(realCommDatabase.getCompanyList(), Company.class, Company.ID_CLASS);
			databaseManager.createList(realCommDatabase.getCompanyLogoList(), CompanyLogo.class, CompanyLogo.ID_CLASS);
			databaseManager.createList(realCommDatabase.getTalkList(), Talk.class, Talk.ID_CLASS);
			databaseManager.createList(realCommDatabase.getTalkTrackList(), TalkTrack.class, TalkTrack.ID_CLASS);
			databaseManager.createList(realCommDatabase.getVenueList(), Venue.class, Venue.ID_CLASS);

			// Wait until data has been deleted/inserted before writing the mostRecentUpdateDate
			SharedPreferencesManager.setMostRecentUpdateDate(this.context, serverMostRecentUpdateDate);
			SharedPreferencesManager.setLastUpdateDate(this.context, new Date().getTime());

			return true;
		}
		catch (Exception e)
		{
			// Something went wrong with the insert
			e.printStackTrace();
		}

		return false;
	}

	/**********************************************************************************************
	 * Json Parsing Methods
	 **********************************************************************************************/
	private Boolean readMostRecentUpdateDateAndCompareToSaved(BufferedReader reader)
	{
		try
		{
			// Get the two dates
			JsonObject mostRecentUpdateDateObject = this.jsonParser.parse(reader).getAsJsonObject();
			serverMostRecentUpdateDate = mostRecentUpdateDateObject.get(SharedPreferencesManager.MOST_RECENT_UPDATE_DATE_MEMBER_NAME).getAsString();

			String localMostRecentUpdateDate = SharedPreferencesManager.getMostRecentUpdateDate(this.context);

			return !serverMostRecentUpdateDate.equals(localMostRecentUpdateDate);
		}
		catch (JsonIOException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	private void readRealCommDatabaseJson(BufferedReader reader)
	{
		try
		{
			JsonObject realCommDatabaseJsonObject = this.jsonParser.parse(reader).getAsJsonObject();

			realCommDatabase = this.gson.fromJson(realCommDatabaseJsonObject, RealCommDatabase.class);
		}
		catch (JsonIOException e)
		{
			LogHelper.Log("Too many beacons, not enough CPU!!!");
			e.printStackTrace();
		}
	}
}

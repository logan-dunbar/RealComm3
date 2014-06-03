package com.openbox.realcomm3.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.openbox.realcomm3.database.objects.Beacon;
import com.openbox.realcomm3.database.objects.Booth;
import com.openbox.realcomm3.database.objects.BoothContact;
import com.openbox.realcomm3.database.objects.Company;
import com.openbox.realcomm3.database.objects.CompanyLogo;
import com.openbox.realcomm3.database.objects.Contact;
import com.openbox.realcomm3.database.objects.ContactImage;
import com.openbox.realcomm3.database.objects.Talk;
import com.openbox.realcomm3.database.objects.TalkTrack;
import com.openbox.realcomm3.database.objects.Venue;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
	/**********************************************************************************************
	 * Database Configuration
	 **********************************************************************************************/
	private static final String DATABASE_NAME = "RealCommDB.sqlite";
	private static final String DATABASE_ASSET_NAME = "RealCommDB.zip";
	private static final int DATABASE_VERSION = 1;

	/**********************************************************************************************
	 * Constructor
	 **********************************************************************************************/
	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		copyFromAssets(context);
	}

	/**********************************************************************************************
	 * onCreate and onUpgrade Events
	 **********************************************************************************************/
	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource)
	{
		try
		{
			TableUtils.createTable(connectionSource, Booth.class);
			TableUtils.createTable(connectionSource, BoothContact.class);
			TableUtils.createTable(connectionSource, Contact.class);
			TableUtils.createTable(connectionSource, ContactImage.class);
			TableUtils.createTable(connectionSource, Company.class);
			TableUtils.createTable(connectionSource, CompanyLogo.class);
			TableUtils.createTable(connectionSource, Talk.class);
			TableUtils.createTable(connectionSource, TalkTrack.class);
			TableUtils.createTable(connectionSource, Venue.class);

			TableUtils.createTable(connectionSource, Beacon.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)
	{
		try
		{
			TableUtils.dropTable(connectionSource, Booth.class, true);
			TableUtils.dropTable(connectionSource, BoothContact.class, true);
			TableUtils.dropTable(connectionSource, Contact.class, true);
			TableUtils.dropTable(connectionSource, ContactImage.class, true);
			TableUtils.dropTable(connectionSource, Company.class, true);
			TableUtils.dropTable(connectionSource, CompanyLogo.class, true);
			TableUtils.dropTable(connectionSource, Talk.class, true);
			TableUtils.dropTable(connectionSource, TalkTrack.class, true);
			TableUtils.dropTable(connectionSource, Venue.class, true);

			TableUtils.dropTable(connectionSource, Beacon.class, true);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		onCreate(database, connectionSource);
	}

	private void copyFromAssets(Context context)
	{
		File dbFile = context.getDatabasePath(DATABASE_NAME);
		String dbPath = dbFile.getPath();
		File dbfile = new File(dbPath);
		boolean exists = dbfile.exists();

		if (!exists)
		{
			// Make sure folders exists
			dbFile.getParentFile().mkdirs();

			InputStream is;
			try
			{
				is = context.getAssets().open(DATABASE_ASSET_NAME);
				ZipInputStream zis = getFileFromZip(is);
				if (zis == null)
				{
					throw new FileNotFoundException("Realcomm DB not in asset folder");
				}

				writeExtractedFileToDisk(zis, new FileOutputStream(dbPath));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static ZipInputStream getFileFromZip(InputStream zipFileStream) throws IOException
	{
		ZipInputStream zis = new ZipInputStream(zipFileStream);
		while (zis.getNextEntry() != null)
		{
			return zis;
		}

		return null;
	}

	private static void writeExtractedFileToDisk(InputStream in, OutputStream outs) throws IOException
	{
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) > 0)
		{
			outs.write(buffer, 0, length);
		}
		outs.flush();
		outs.close();
		in.close();
	}
}

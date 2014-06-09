package com.openbox.realcomm3.application;

import com.openbox.realcomm3.database.DatabaseManager;
import com.openbox.realcomm3.utilities.helpers.LogHelper;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.Settings.Secure;

public class RealCommApplication extends Application
{
	// ONLY STATIC VARIABLES/METHODS IN HERE. THIS IS BOUND TO THE CONTEXT
	// Note: If the app goes to background and is closed by the OS, this object is not persisted. Use at own risk
	public static final String DOWNLOAD_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String UPLOAD_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private static boolean hasBluetoothLE = false;
	private static boolean isLargeScreen = false;

	private static String deviceId;

	private Typeface exo2Font;
	private Typeface exo2FontBold;
	private Typeface nunitoFont;
	private Typeface nunitoFontBold;

	public static boolean getHasBluetoothLe()
	{
		return hasBluetoothLE;
	}

	public static Boolean isBluetoothEnabled()
	{
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		return bluetoothAdapter == null ? false : bluetoothAdapter.isEnabled();
	}

	public static String getDeviceId()
	{
		return deviceId;
	}

	public static boolean getIsLargeScreen()
	{
		return isLargeScreen;
	}

	public Typeface getExo2Font()
	{
		return this.exo2Font;
	}

	public Typeface getExo2FontBold()
	{
		return exo2FontBold;
	}

	public Typeface getNunitoFont()
	{
		return this.nunitoFont;
	}

	public Typeface getNunitoFontBold()
	{
		return nunitoFontBold;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

		setIsLargeScreen();

		this.exo2Font = Typeface.createFromAsset(getAssets(), "fonts/Exo_2/Exo2-Regular.ttf");
		this.exo2FontBold = Typeface.createFromAsset(getAssets(), "fonts/Exo_2/Exo2-Bold.ttf");

		this.nunitoFont = Typeface.createFromAsset(getAssets(), "fonts/Nunito/Nunito-Regular.ttf");
		this.nunitoFontBold = Typeface.createFromAsset(getAssets(), "fonts/Nunito/Nunito-Bold.ttf");

		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
		{
			hasBluetoothLE = true;
		}

		// Initialize the DBManager
		DatabaseManager.init(getApplicationContext());
	}

	private void setIsLargeScreen()
	{
		// Lookup Configuration#screenLayout for more details
		isLargeScreen =
			(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}
}

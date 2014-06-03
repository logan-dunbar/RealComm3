package com.openbox.realcomm3.base;

import java.util.Dictionary;
import java.util.Hashtable;
import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.dialogfragments.InfoDialogFragment;
import com.openbox.realcomm3.services.WebService;
import com.openbox.realcomm3.utilities.helpers.ToastHelper;
import com.openbox.realcomm3.utilities.interfaces.AsyncTaskInterface;
import com.openbox.realcomm3.utilities.interfaces.DatabaseSyncReceiverInterface;
import com.openbox.realcomm3.utilities.interfaces.WebServiceConnectionInterface;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.AsyncTask.Status;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends FragmentActivity
{
	/**********************************************************************************************
	 * Fields
	 **********************************************************************************************/
	private ActionBar actionBar;

	/**********************************************************************************************
	 * Lifecycle Implements
	 **********************************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.actionBar = getActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Careful when this is called in the Lifecycle
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/**********************************************************************************************
	 * Click Implements
	 **********************************************************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.infoMenuItem:
				showInfoDialogFragment();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showInfoDialogFragment()
	{
		InfoDialogFragment infoDialogFragment = new InfoDialogFragment();
		infoDialogFragment.show(getSupportFragmentManager(), InfoDialogFragment.TAG);
	}
}

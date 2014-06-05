package com.openbox.realcomm3.base;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.dialogfragments.InfoDialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends FragmentActivity
{
	/**********************************************************************************************
	 * Lifecycle Implements
	 **********************************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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

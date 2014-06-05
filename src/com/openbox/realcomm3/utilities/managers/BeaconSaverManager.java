package com.openbox.realcomm3.utilities.managers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseArray;

import com.openbox.realcomm3.database.DatabaseManager;
import com.openbox.realcomm3.database.models.BeaconModel;
import com.openbox.realcomm3.database.models.BoothModel;
import com.openbox.realcomm3.database.objects.Beacon;
import com.openbox.realcomm3.services.WebService;
import com.openbox.realcomm3.utilities.enums.AppMode;
import com.openbox.realcomm3.utilities.helpers.LogHelper;
import com.openbox.realcomm3.utilities.interfaces.AppModeChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.AppModeInterface;
import com.openbox.realcomm3.utilities.interfaces.DataChangedCallbacks;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;

public class BeaconSaverManager implements DataChangedCallbacks, AppModeChangedCallbacks
{
	private static final long SAVE_INTERVAL = 1000;
	private static final long UPLOAD_INTERVAL = 60 * 1000;

	private Handler updateBeaconsHandler;
	private Handler saveHandler;
	private Handler uploadHandler;

	private Context context;
	private DataInterface dataInterface;
	private AppModeInterface appModeInterface;

	private boolean isDataLoaded;
	private boolean shouldContinue = true;

	private volatile SparseArray<BeaconModel> beaconArray = new SparseArray<>();

	public BeaconSaverManager(Context context, DataInterface dataInterface, AppModeInterface appModeInterface)
	{
		this.context = context;
		this.dataInterface = dataInterface;
		this.appModeInterface = appModeInterface;

		HandlerThread updateBeaconsThread = new HandlerThread("UpdateBeaconsThread");
		updateBeaconsThread.start();
		updateBeaconsHandler = new Handler(updateBeaconsThread.getLooper());

		HandlerThread saveThread = new HandlerThread("SaveThread");
		saveThread.start();
		saveHandler = new Handler(saveThread.getLooper());

		HandlerThread uploadThread = new HandlerThread("UploadThread");
		uploadThread.start();
		uploadHandler = new Handler(uploadThread.getLooper());
	}

	@Override
	public void onDataLoaded()
	{
		this.isDataLoaded = true;
	}

	@Override
	public void onDataChanged()
	{
		if (this.isDataLoaded)
		{
			beaconArray.clear();
		}
	}

	@Override
	public void onBeaconsUpdated()
	{
		if (this.isDataLoaded)
		{
			updateBeaconsHandler.post(updateBeaconsRunnable);
		}
	}

	@Override
	public void onAppModeChanged(AppMode newAppMode, AppMode previousAppMode)
	{
		LogHelper.Log("onAppModeChanged: " + newAppMode.getDisplayName());
		if (this.appModeInterface != null)
		{
			if (newAppMode == AppMode.ONLINE)
			{
				this.shouldContinue = true;
				this.saveHandler.post(saveBeaconsRunnable);
				this.uploadHandler.post(uploadBeaconsRunnable);
			}
			else
			{
				this.shouldContinue = false;
			}
		}
	}

	public void stop()
	{
		this.shouldContinue = false;
	}

	private Runnable updateBeaconsRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			if (dataInterface != null)
			{
				List<BoothModel> boothList = dataInterface.getBoothModelList();
				for (BoothModel boothModel : boothList)
				{
					// TODO Do we only keep beacons that have 'real' values?
					if (boothModel.getAccuracy() < BoothModel.DEFAULT_ACCURACY)
					{
						if (beaconArray.indexOfKey(boothModel.getBoothId()) > -1)
						{
							beaconArray.get(boothModel.getBoothId()).updateBeacon(boothModel);
						}
						else
						{
							beaconArray.put(boothModel.getBoothId(), new BeaconModel(boothModel));
						}
					}
				}
			}
		}
	};

	private Runnable uploadBeaconsRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			Intent serviceIntent = new Intent(context, WebService.class);
			serviceIntent.setAction(WebService.UPLOAD_BEACONS_ACTION);
			context.startService(serviceIntent);

			if (shouldContinue)
			{
				uploadHandler.postDelayed(uploadBeaconsRunnable, UPLOAD_INTERVAL);
			}
		}
	};

	private Runnable saveBeaconsRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			List<Beacon> saveList = new ArrayList<>();

			for (int i = 0; i < beaconArray.size(); i++)
			{
				saveList.add(new Beacon(beaconArray.valueAt(i)));
			}

			if (saveList.size() > 0)
			{
				try
				{
					DatabaseManager.getInstance().createList(saveList, Beacon.class, Beacon.ID_CLASS);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (shouldContinue)
			{
				saveHandler.postDelayed(saveBeaconsRunnable, SAVE_INTERVAL);
			}
		}
	};
}

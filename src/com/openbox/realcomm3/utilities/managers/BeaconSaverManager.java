package com.openbox.realcomm3.utilities.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
	private Context context;
	private DataInterface dataInterface;
	private AppModeInterface appModeInterface;
	private boolean isDataLoaded;

	private SaveBeaconThread saveBeaconThread;
	private UploadBeaconThread uploadBeaconThread;

	private SparseArray<BeaconModel> beaconArray = new SparseArray<>();

	public BeaconSaverManager(Context context, DataInterface dataInterface, AppModeInterface appModeInterface)
	{
		this.context = context;
		this.dataInterface = dataInterface;
		this.appModeInterface = appModeInterface;
	}

	@Override
	public void onDataLoaded()
	{
		this.isDataLoaded = true;
	}

	@Override
	public void onDataChanged()
	{
		// TODO Clear any saved beacons?
	}

	@Override
	public void onBeaconsUpdated()
	{
		if (this.isDataLoaded)
		{
			updateBeacons();
		}
	}

	@Override
	public void onAppModeChanged()
	{
		if (this.appModeInterface != null)
		{
			if (this.appModeInterface.getCurrentAppMode() == AppMode.ONLINE)
			{
				this.saveBeaconThread = new SaveBeaconThread();
				this.saveBeaconThread.setPriority(Thread.MIN_PRIORITY + 1);
				this.saveBeaconThread.start();

				this.uploadBeaconThread = new UploadBeaconThread();
				this.uploadBeaconThread.setPriority(Thread.MIN_PRIORITY + 1);
				this.uploadBeaconThread.start();
			}
			else
			{
				if (this.saveBeaconThread != null)
				{
					this.saveBeaconThread.setShouldContinue(false);
				}

				if (this.uploadBeaconThread != null)
				{
					this.uploadBeaconThread.setShouldContinue(false);
				}
			}
		}
	}

	@Override
	public void onOnlineModeToOfflineMode()
	{
		// Stub
	}

	private void updateBeacons()
	{
		if (this.dataInterface != null)
		{
			List<BoothModel> boothList = this.dataInterface.getBoothModelList();
			for (BoothModel boothModel : boothList)
			{
				// TODO Do we only keep beacons that have 'real' values?
				if (boothModel.getAccuracy() < BoothModel.DEFAULT_ACCURACY)
				{
					if (this.beaconArray.indexOfKey(boothModel.getBoothId()) > -1)
					{
						this.beaconArray.get(boothModel.getBoothId()).updateBeacon(boothModel);
					}
					else
					{
						this.beaconArray.put(boothModel.getBoothId(), new BeaconModel(boothModel));
					}
				}
			}
		}
	}

	class UploadBeaconThread extends Thread
	{
		private static final long UPLOAD_INTERVAL = 10 * 1000;
		private Handler uploadHandler;
		private boolean shouldContinue = true;

		public UploadBeaconThread()
		{
			this.uploadHandler = new Handler();
			this.uploadHandler.post(this.uploadBeaconsRunnable);
		}

		public void setShouldContinue(boolean shouldContinue)
		{
			this.shouldContinue = shouldContinue;
		}

		private Runnable uploadBeaconsRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				LogHelper.Log("Starting service to upload beacons...");
				Intent serviceIntent = new Intent(context, WebService.class);
				serviceIntent.setAction(WebService.UPLOAD_BEACONS_ACTION);
				context.startService(serviceIntent);

				if (shouldContinue)
				{
					uploadHandler.postDelayed(uploadBeaconsRunnable, UPLOAD_INTERVAL);
				}
			}
		};
	}

	class SaveBeaconThread extends Thread
	{
		private static final long SAVE_INTERVAL = 1000;
		private Handler saveHandler;
		private boolean shouldContinue = true;

		public SaveBeaconThread()
		{
			this.saveHandler = new Handler();
			this.saveHandler.post(this.saveBeaconsRunnable);
		}

		public void setShouldContinue(boolean shouldContinue)
		{
			this.shouldContinue = shouldContinue;
		}

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
						LogHelper.Log("Wrote " + saveList.size() + " beacons to DB");
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
}

package com.openbox.realcomm3.utilities.managers;

import java.util.Date;
import java.util.List;

import com.openbox.realcomm3.database.models.BoothModel;
import com.openbox.realcomm3.utilities.interfaces.DataInterface;

public class BeaconSaverManager
{
	private static final int SAVE_INTERVAL = 1000;
	private DataInterface dataInterface;
	private Date lastSaveTime = new Date();

	public BeaconSaverManager(DataInterface dataInterface)
	{
		this.dataInterface = dataInterface;
	}
	
	public void saveBeacons()
	{
		if (this.dataInterface != null)
		{
			Date currentTime = new Date();
			if (currentTime.getTime() - this.lastSaveTime.getTime() > SAVE_INTERVAL)
			{
				List<BoothModel> boothList = this.dataInterface.getBoothModelList();
				
			}
		}
	}
}

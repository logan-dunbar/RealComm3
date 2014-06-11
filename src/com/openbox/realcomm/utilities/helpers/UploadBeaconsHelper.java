package com.openbox.realcomm.utilities.helpers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.openbox.realcomm.application.RealCommApplication;
import com.openbox.realcomm.database.DatabaseManager;
import com.openbox.realcomm.database.models.BeaconModel;
import com.openbox.realcomm.database.objects.Beacon;
import com.openbox.realcomm.services.WebService;

public class UploadBeaconsHelper
{
	public static final String POST_BEACON_DATA_API_URL = WebService.ROOT_API_URL + "PostBeaconRangingData";

	private Gson gson;

	public UploadBeaconsHelper()
	{
		this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat(RealCommApplication.UPLOAD_DATE_FORMAT).create();
	}

	public boolean uploadBeaconRangingData()
	{
		List<Beacon> beaconList = null;
		try
		{
			beaconList = DatabaseManager.getInstance().getAll(Beacon.class);
			beaconList = aggregateBeacons(beaconList);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		if (beaconList != null && beaconList.size() > 0)
		{
			String beaconJson = this.gson.toJson(new BeaconJsonWrapper(beaconList));

			try
			{
				// Set up the connection
				URL url = new URL(POST_BEACON_DATA_API_URL);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

				// Specify request properties
				urlConnection.setRequestProperty("Accept", "application/json;charset=utf-8");
				urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("POST");

				// Write data to stream
				// OutputStream stream = urlConnection.getOutputStream();
				BufferedOutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
				os.write(beaconJson.getBytes("UTF-8"));
				os.close();

				// Get the result
				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK)
				{
					try
					{
						// Posted successfully, clear the table
						DatabaseManager.getInstance().deleteAll(Beacon.class);
						return true;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					// There was a problem, it will try again in a minute
					// TODO potentially handle better/log error?
				}
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

		// Nothing done, so signal everything OK
		return true;
	}

	private List<Beacon> aggregateBeacons(List<Beacon> beaconList)
	{
		SparseArray<List<BeaconModel>> aggregatedBeaconModelArray = new SparseArray<>();
		for (Beacon beacon : beaconList)
		{
			if (aggregatedBeaconModelArray.indexOfKey(beacon.getBoothId()) < 0)
			{
				List<BeaconModel> tempBeaconModelList = new ArrayList<BeaconModel>();
				tempBeaconModelList.add(new BeaconModel(beacon));
				aggregatedBeaconModelArray.put(beacon.getBoothId(), tempBeaconModelList);
			}
			else
			{
				List<BeaconModel> tempBeaconList = aggregatedBeaconModelArray.get(beacon.getBoothId());
				tempBeaconList.add(new BeaconModel(beacon));
			}
		}

		List<Beacon> aggregatedBeaconList = new ArrayList<>();
		for (int i = 0; i < aggregatedBeaconModelArray.size(); i++)
		{
			List<BeaconModel> tempBeaconList = aggregatedBeaconModelArray.valueAt(i);
			BeaconModel tempBeacon = null;
			if (tempBeaconList.size() > 0)
			{
				tempBeacon = tempBeaconList.get(0);
			}
			else
			{
				break;
			}

			for (int j = 1; j < tempBeaconList.size(); j++)
			{
				tempBeacon.updateBeacon(tempBeaconList.get(j));
			}

			if (tempBeacon != null)
			{
				Beacon beacon = new Beacon(tempBeacon);

				// TODO check if this shouldn't be averaged as well?
				beacon.setRangedDate(new Date());

				aggregatedBeaconList.add(beacon);
			}
		}

		return aggregatedBeaconList;
	}

	class BeaconJsonWrapper
	{
		@SerializedName("data")
		@Expose
		List<Beacon> data = new ArrayList<>();

		public BeaconJsonWrapper(List<Beacon> beaconData)
		{
			this.data = beaconData;
		}

		public List<Beacon> getData()
		{
			return data;
		}

		public void setData(List<Beacon> data)
		{
			this.data = data;
		}
	}
}

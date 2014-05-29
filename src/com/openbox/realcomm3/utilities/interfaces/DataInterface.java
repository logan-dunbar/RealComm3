package com.openbox.realcomm3.utilities.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.openbox.realcomm3.database.models.BoothModel;
import com.openbox.realcomm3.utilities.enums.BoothSortMode;
import com.openbox.realcomm3.utilities.enums.ProximityRegion;
import com.radiusnetworks.ibeacon.IBeacon;

public interface DataInterface
{
	BoothModel getBoothModelForBoothId(int boothId);
	
	BoothModel getBoothModelForCompanyName(String companyName);

	List<Integer> getClosestBoothIds(int numberOfDisplayBooths);

	List<Integer> getRandomBoothIds(int numberOfDisplayBooths);

	List<BoothModel> getBoothModelList(BoothSortMode sortMode);

	List<BoothModel> getBoothModelList();

	Map<ProximityRegion, Integer> getBoothProximityCounts();

	void updateAccuracy(Collection<IBeacon> beaconList);

	void resetAccuracy();
}

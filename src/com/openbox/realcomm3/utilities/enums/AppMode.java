package com.openbox.realcomm3.utilities.enums;

public enum AppMode
{
	INITIALIZING("Initializing"),
	OFFLINE("Offline"),
	ONLINE("Online"),
	OUTOFRANGE("Out of Range"),
	PAUSED("Paused");
	
	private final String displayName;
	
	private AppMode(String displayName)
	{
		this.displayName = displayName;
	}

	public String getDisplayName()
	{
		return displayName;
	}
}

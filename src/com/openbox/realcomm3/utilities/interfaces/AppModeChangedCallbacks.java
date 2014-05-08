package com.openbox.realcomm3.utilities.interfaces;

public interface AppModeChangedCallbacks
{
	// Could probably just pass the current mode here, but the whole app uses
	// Data fragments, so going to keep it standard and make the listener "fetch" the status
	void onAppModeChanged();
	
	void onOnlineModeToOfflineMode();
}

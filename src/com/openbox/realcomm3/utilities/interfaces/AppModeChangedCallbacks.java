package com.openbox.realcomm3.utilities.interfaces;

import com.openbox.realcomm3.utilities.enums.AppMode;

public interface AppModeChangedCallbacks
{
	void onAppModeChanged(AppMode newAppMode, AppMode previousAppMode);
}

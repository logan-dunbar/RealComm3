package com.openbox.realcomm.utilities.interfaces;

import com.openbox.realcomm.utilities.enums.AppMode;

public interface AppModeChangedCallbacks
{
	void onAppModeChanged(AppMode newAppMode, AppMode previousAppMode);
}

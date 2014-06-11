package com.openbox.realcomm.utilities.interfaces;

import com.openbox.realcomm.utilities.enums.AppMode;

public interface AppModeInterface
{
	void changeAppMode(AppMode newAppMode);
	
	AppMode getCurrentAppMode();
}

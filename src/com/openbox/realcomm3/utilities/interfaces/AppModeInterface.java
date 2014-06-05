package com.openbox.realcomm3.utilities.interfaces;

import com.openbox.realcomm3.utilities.enums.AppMode;

public interface AppModeInterface
{
	void changeAppMode(AppMode newAppMode);
	
	AppMode getCurrentAppMode();
}

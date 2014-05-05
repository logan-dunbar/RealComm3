package com.openbox.realcomm3.utilities.interfaces;

import android.content.ComponentName;

public interface WebServiceConnectionInterface
{
	void onServiceConnected(ComponentName name);
	void onServiceDisconnected(ComponentName name);
}

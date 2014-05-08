package com.openbox.realcomm3.utilities.helpers;

import java.util.TimerTask;

import com.openbox.realcomm3.utilities.interfaces.TimerTaskCallbacks;

public class ViewTimerTask extends TimerTask
{
	private TimerTaskCallbacks listener;

	public ViewTimerTask(TimerTaskCallbacks listener)
	{
		this.listener = listener;
	}

	@Override
	public void run()
	{
		// This is async, so must use runOnUiThread in listener
		listener.onTimerTick();
	}
}

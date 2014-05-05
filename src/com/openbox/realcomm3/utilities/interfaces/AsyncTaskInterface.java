package com.openbox.realcomm3.utilities.interfaces;

import com.openbox.realcomm3.base.BaseAsyncTask;

public interface AsyncTaskInterface
{
	void onTaskComplete(BaseAsyncTask task);
	void onTaskCancelled(BaseAsyncTask task);
	void finishAsyncTask(String taskName);
}

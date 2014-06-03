package com.openbox.realcomm3.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public abstract class CustomIntentService extends Service
{
	private volatile Looper serviceLooper;
	private volatile ServiceHandler serviceHandler;
	private String name;
	private boolean redelivery;
	private int threadPriority;

	private final class ServiceHandler extends Handler
	{
		public ServiceHandler(Looper looper)
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg)
		{
			onHandleIntent((Intent) msg.obj);
			stopSelf(msg.arg1);
		}
	}

	public CustomIntentService(String name)
	{
		super();
		this.name = name;
		this.threadPriority = Thread.NORM_PRIORITY;
	}

	public CustomIntentService(String name, int threadPriority)
	{
		super();
		this.name = name;
		this.threadPriority = threadPriority;
	}

	public void setIntentRedelivery(boolean enabled)
	{
		this.redelivery = enabled;
	}

	@Override
	public void onCreate()
	{
		// TODO: It would be nice to have an option to hold a partial wakelock
		// during processing, and to have a static startService(Context, Intent)
		// method that would launch the service & hand off a wakelock.

		super.onCreate();
		HandlerThread thread = new HandlerThread("IntentService[" + name + "]");
		thread.setPriority(this.threadPriority);
		thread.start();

		this.serviceLooper = thread.getLooper();
		this.serviceHandler = new ServiceHandler(serviceLooper);
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		Message msg = this.serviceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.obj = intent;
		serviceHandler.sendMessage(msg);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		onStart(intent, startId);
		return redelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
	}

	@Override
	public void onDestroy()
	{
		serviceLooper.quit();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	protected abstract void onHandleIntent(Intent intent);
}
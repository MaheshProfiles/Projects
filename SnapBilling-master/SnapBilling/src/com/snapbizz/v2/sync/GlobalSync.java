package com.snapbizz.v2.sync;

import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

public class GlobalSync extends Service {
	// TODO: Use https before rollout
	public static String sync_language = "en";

	private static final String TAG = "[GlobalSync]";
	private static final int MIN_SLEEP_TIME = 5*60*1000; // 5min
	private static Thread instance = null;
	private static String THREAD_NAME = "[GlobalSyncMainThread]";
	
	public static interface GlobalSyncService {
		// Actual sync code, returns null on failure
		public Long startSync(long currentTime, long lastSyncTime, GlobalDB gdb, SnapbizzDB sdb);
		public long getSyncInterval();				// In seconds
	}
	
	private Runnable mainThread = new Runnable() {
		@Override
		public void run() {
			GlobalDB gdb = GlobalDB.getInstance(GlobalSync.this, false);
			SnapbizzDB sdb = SnapbizzDB.getInstance(getApplicationContext(), false);

			GlobalSyncService allSyncServices[] = new GlobalSyncService[] {
														new SyncGDB(GlobalSync.this),
														new UpgradeVersion(GlobalSync.this),
									                    new UploadSyncService(GlobalSync.this)
												  };

			boolean bFirstRun = true;
			long allSyncIntervals[] = new long[allSyncServices.length];
			long allSyncTimestamps[] = new long[allSyncServices.length];
			
			for (int i = 0; i < allSyncServices.length; i++) {
				allSyncIntervals[i] = allSyncServices[i].getSyncInterval();
				if (gdb != null)
					allSyncTimestamps[i] = gdb.getLastSyncTime(allSyncServices[i].getClass().getName());
				else
					allSyncTimestamps[i] = 0L;
			}
			Log.i(TAG, "Sync Thread started");
			try {
				while (instance != null && !instance.isInterrupted()) {
					if (bFirstRun)
						bFirstRun = false;
					else
						Thread.sleep(MIN_SLEEP_TIME);
					
					if (!isInternet())
						continue;
					
					long currentTime = System.currentTimeMillis() / 1000L;
					
					for (int i = 0; i < allSyncServices.length; i++) {
						if ((allSyncTimestamps[i] + allSyncIntervals[i]) <= currentTime) {
							String syncClass = allSyncServices[i].getClass().getName();
							Log.i(TAG, "Starting Sync: " + i + ":" + syncClass);
							Long syncTime = allSyncServices[i].startSync(currentTime,
																		 gdb.getLastSyncTime(syncClass), gdb, sdb);
							if (syncTime != null) {
								// Save the last synched time as allSyncTimestamps[i]
								gdb.setLastSyncTime(syncClass, syncTime);
								allSyncTimestamps[i] = currentTime;
								Log.i(TAG, "Sync Completed: "+i);
							} else {
								Log.e(TAG, "Sync Failed: "+i);
							}
						}
					}
				}
			} catch(InterruptedException e) {
				Log.e(TAG, "Thread Interrupted");
			} catch(Exception e) {
				Log.e(TAG, "Thread exception unknown");
				e.printStackTrace();
			}
			GlobalSync.this.stopSelf();
		}
	};
	
	private boolean isInternet() {
		ConnectivityManager connectivityManager = 
					(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

    public static boolean isInstanceCreated(){
        return instance != null;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    // Not called on newer Android 2+
    @Override     
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId); 
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
    	Log.i(TAG, "ServiceStart Called:"+instance);
		if (instance == null)
			instance = new Thread(mainThread, THREAD_NAME);
    	if (!instance.isAlive())
    		instance.start();
    	return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override     
    public void onDestroy() {
    	instance = null;
    }
    
    @Override
    public boolean stopService(Intent intent) {
    	Log.i(TAG, "stopService Called");
    	if (instance != null)
    		instance.interrupt();
    	return super.stopService(intent);
    }
}

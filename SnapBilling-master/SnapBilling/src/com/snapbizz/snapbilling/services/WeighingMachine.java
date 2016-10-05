package com.snapbizz.snapbilling.services;

import java.io.IOException;
import java.io.InputStream;

import java.util.Set;

import com.snapbizz.snapbilling.activity.BillingActivity;
import com.snapbizz.snapbilling.utils.BluetoothHelper;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WeighingMachine extends Service {
	private final static String TAG = "[WeighingMachineService]";
	//private final static String deviceAddress = "00:04:3E:91:28:1A";
	private final static int RETRY_SLEEP = 10000;	// 10 sec...
	private final static int READ_SLEEP = 500;

	private final IBinder mBinder = new WeighingMachineBinder();
	private BillingActivity parent = null;
	private volatile BluetoothSocket btSocket = null;
	private volatile String deviceAddress = null;

	public class WeighingMachineBinder extends Binder {
		public WeighingMachine getService() {
            return WeighingMachine.this;
        }
    }

	void createBluetoothConnection() {
		btSocket = null;
		Log.d(TAG, "Trying to connect to: "+(deviceAddress != null ? deviceAddress : "Null"));
		if(deviceAddress == null)
			return;
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!btAdapter.isEnabled())
            btAdapter.enable();
		BluetoothDevice device = BluetoothHelper.getWeighingMachine(btAdapter, deviceAddress);
		if(device == null) {
			Log.d(TAG, "Device is not paired");
			return;
		}
		try {
			if(device.getUuids().length > 0) {
				btSocket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
				if(btSocket != null) {
					btSocket.connect();
					if(!btSocket.isConnected())  {
						Log.d(TAG, "Connect failed: "+deviceAddress);
						btSocket = null;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			btSocket = null;
		}
	}
	
	void startReading() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean restart = false;
				try {
					InputStream input = btSocket.getInputStream();
					byte[] buffer = new byte[100];
					float lastWeight = 0.0f;
					boolean start = false;
					String val = new String();
					while(true) {
						if(btSocket == null) {
							restart = true;
							break;
						}
						if(input.read(buffer) > 0) {
							val = val + new String(buffer);
							Log.d(TAG, "Read:"+val);
							if(!start && val.contains("\n")) {
								try {
									val = val.substring(val.indexOf("\n") + 1);
								} catch(Exception e) {
									val = new String();
									continue;
								}
								start = true;
							}
							if(!start && val.contains("+")) {
								val = val.substring(val.indexOf("+"));
								start = true;
							}
							if(!start && val.contains("-")) {
								val = val.substring(val.indexOf("-"));
								start = true;
							}
							if(start && val.contains("\n")) {
								boolean kg = false;
								val = val.substring(0, val.indexOf("\n"));
								val.trim();
								
								
								if(val.contains("Kg")) {
									kg = true;
									val = val.substring(0, val.indexOf("Kg"));
									val.trim();
								}
								
								try {
									float weight = Float.parseFloat(val);
									if(parent != null && weight != lastWeight) {
										Log.d(TAG, "Change weight to:"+weight);
										lastWeight = weight;
										parent.setLastItemWeight(weight, kg);
									}
								} catch(NumberFormatException e) { }
								
								start = false;
								val = new String();
							}
						}
						Thread.sleep(READ_SLEEP);
					}
				} catch(IOException e) {
					e.printStackTrace();
					restart = true;
					
				} catch(Exception e) {
					e.printStackTrace();
				}
				reinitialize();
				if(restart)
					restartConnection();
			}
		}).start();
	}
	
	void updateStatusIcon() {
		if(parent != null && !parent.isDestroyed() && !parent.isFinishing())
			parent.setWeighingMachineStatus(btSocket != null && !btSocket.isConnected());
	}
	
	void restartConnection() {
		updateStatusIcon();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(btSocket == null) {
						createBluetoothConnection();
						Thread.sleep(RETRY_SLEEP);
					}
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				if(btSocket != null) {
					updateStatusIcon();
					startReading();
				}
			}
		}).start();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		restartConnection();
	}


	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		this.parent = null;
		return false;
	}

	public void setParent(BillingActivity parent) {
		this.parent = parent;
	}
	
	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress; 
	}
	
	public void reinitialize() {
		try {
			if(btSocket != null && btSocket.isConnected())
				btSocket.close();
		} catch(Exception e) { }
		btSocket = null;
	}
}

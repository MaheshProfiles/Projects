package com.snapbizz.snapbilling.services;

import java.lang.reflect.Method;
import java.util.Set;

import com.snapbizz.plugin.PrinterPlugin.BTPrinterPlugin;
import com.snapbizz.plugin.printers.AEMPrinterPlugin;
import com.snapbizz.snapbilling.activity.BillingActivity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class PrinterService extends Service {	
	private final static String TAG = "[PrinterService]";
	private final static int RETRY_SLEEP = 10000;	// 30000	// 30 sec
	private final IBinder mBinder = new PrinterBinder();
	
	private static volatile BluetoothSocket btSocket = null;
	private static volatile BTPrinterPlugin printer = null;
	private volatile String deviceName = null;
	private BillingActivity parent = null;
	private BTPrinterPlugin[] printerPlugins;
	
	public class PrinterBinder extends Binder {
		public PrinterService getService() {
			return PrinterService.this;
		}
	}
	
	public void setParent(BillingActivity activity) {
		this.parent = activity;
	}
	
	public void attachPrinterDriver(BTPrinterPlugin printer) {
		this.printer = printer;
		if(btSocket != null && btSocket.isConnected())
			printer.reInitBtSocket(btSocket);
	}
	
	private void initializePrinter(BluetoothDevice device) {
		if(printerPlugins == null)
			return;
		for(BTPrinterPlugin plugin : printerPlugins) {
			if(plugin.checkPluginSupport(device)) {
				printer = plugin;
				return;
			}
		}
	}

	void createBluetoothConnection() {
		btSocket = null;
		updateStatusIcon();
		Log.d(TAG, "Trying to connect to: " + (deviceName != null ? deviceName : "Null"));
		BluetoothDevice device = getPrinterByName(deviceName);	
		if (device == null) {
			Log.d(TAG, "Device is not paired");
			return;
		}
		initializePrinter(device);
		try {
			if (device.getUuids().length > 0) {
				String deviceVersion = Build.VERSION.RELEASE;

		        if (deviceVersion.contains("4.1.1") || deviceVersion.contains("4.2")) {
		        	btSocket = device.createInsecureRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
		        } else {
		            try {
		                Method m = device.getClass().getMethod("createInsecureRfcommSocket",
		                                				new Class[] { int.class });
		                btSocket = (BluetoothSocket) m.invoke(device, 1);
		            } catch (Exception e) {
		            	e.printStackTrace();
		            	btSocket = null;
		            }
		        }				
				if (btSocket != null) {
					btSocket.connect();
					if (!btSocket.isConnected()) {
						Log.e(TAG, "Connect failed: " + deviceName);
						btSocket = null;
					} else if(printer != null) {
						printer.reInitBtSocket(btSocket);
					}
					updateStatusIcon();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			btSocket = null;
		}
	}
	
	// _Maybe_ we should use printerAddress instead of the name
    private BluetoothDevice getPrinterByName(String printerName) {
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName() == null)
                continue;
            if (device.getName().equalsIgnoreCase(printerName))
                return device;
        }
        return null;
    }
    
    void updateStatusIcon() {
		if(parent != null && !parent.isDestroyed() && !parent.isFinishing())
			parent.setPrinterStatus(isConnected());
	}
	
	void maintainConnection() {
		new Thread(new Runnable() {			
			@Override
			public void run() {
				boolean retry = false;
				try {
					while(true) {
						if(btSocket == null)
							createBluetoothConnection();
						Thread.sleep(RETRY_SLEEP);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					retry = true;
				}
				if(retry)
					maintainConnection();
			}
		}).start();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		printerPlugins = new BTPrinterPlugin[] {
							new AEMPrinterPlugin(this)
						 };
		maintainConnection();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return false;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName; 
	}
	
	public static BTPrinterPlugin getPrinterPlugin() {
		return printer;
	}
	
	public void reinitialize() {
		try {
			if (btSocket != null && btSocket.isConnected())
				btSocket.close();
		} catch(Exception e) { }
		btSocket = null;
	}
	
	public static boolean isConnected() {
		if(printer != null && btSocket != null && btSocket.isConnected())
			return true;
		return false;
	}
}


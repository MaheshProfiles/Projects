package com.snapbizz.snapbilling.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class BluetoothHelper {
	private static final String TAG = "[BluetoothHelper]";
	Context context;
	BluetoothAdapter adapter;
	ScanCompleteListener listener = null;
	ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	Dialog devicesDialog = null;
	
	public interface ScanCompleteListener {
		public void scanComplete(String status);
	}

	public BluetoothHelper(Context context, ScanCompleteListener listener) {
		this.context = context;
		adapter = BluetoothAdapter.getDefaultAdapter();
		this.listener = listener;
	}
	
	public static String[] getDeviceNames(List<BluetoothDevice> devices) {
		if(devices.size() <= 0)
			return new String[0];
		String[] names = new String[devices.size()];
		for(int i = 0; i < devices.size(); i++) {
			BluetoothDevice device = devices.get(i);
			if(device.getName() != null && !device.getName().isEmpty())
				names[i] = device.getName();
			else
				names[i] = device.getAddress();
		}
		return names;
	}
	
	private void pairDevice(BluetoothDevice device) {
		if(device == null)
			return;
        try {
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void showDeviceList() {
		String[] names = getDeviceNames(devices);
		if(names == null || names.length <= 0) {
			if(listener != null)
				listener.scanComplete("No devices found!");
			return;
		}
		if(listener != null)
			listener.scanComplete(null);
		devicesDialog = SnapCommonUtils.buildAlertListViewDialog(
						context.getResources().getString(R.string.select_bluetooth_device_to_pair),
						names, context, new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								pairDevice(devices.get(position));
								if(devicesDialog != null) {
									devicesDialog.dismiss();
									devicesDialog = null;
								}
							}
						});
		devicesDialog.show();
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            	Log.d(TAG, "Found Bluetooth device: "+device.getAddress());
	                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                		//  Check if already exists in array
	                	for(BluetoothDevice d : devices) {
	                		if(d.getAddress().equalsIgnoreCase(device.getAddress()))
	                			return;
	                	}
	                    devices.add(device);
	                }
	            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	            	context.unregisterReceiver(mReceiver);
	            	showDeviceList();
	            }
	        }
	};
	
	public static BluetoothDevice getWeighingMachine(BluetoothAdapter adapter, String deviceAddress) {
		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		for(BluetoothDevice device : pairedDevices) {
            if(device.getAddress().equalsIgnoreCase(deviceAddress))
                return device;
		}
		return null;
	}
	
	/**
	 * Start Discovery and pair with the selected device
	 * 
	 * @return true -> discovery started, false -> on failure
	 */
	public boolean pairNewDevice() {
		if(!adapter.isEnabled())
			return false;
		adapter.cancelDiscovery();
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);
		return adapter.startDiscovery();
	}
	
	
}

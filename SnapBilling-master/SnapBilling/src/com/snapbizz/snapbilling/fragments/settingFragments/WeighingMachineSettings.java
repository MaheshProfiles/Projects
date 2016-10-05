package com.snapbizz.snapbilling.fragments.settingFragments;

import java.util.ArrayList;
import java.util.List;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.BluetoothHelper;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class WeighingMachineSettings extends Fragment implements OnClickListener {
	private Context mContext;
	private Dialog btDevicesDialog = null;
	private View view;

	public WeighingMachineSettings(Context mContext) {
		this.mContext = mContext;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.weighing_machine_settings, container, false);
		initWeighingMachine();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		ActionBar actionBar = getActivity().getActionBar();
		if (!actionBar.isShowing())
			actionBar.show();
		if (R.id.actionbar_layout != actionBar.getCustomView().getId())
			actionBar.setCustomView(R.layout.actionbar_layout);
		((TextView) getActivity().findViewById(R.id.actionbar_header))
				.setText(getString(R.string.settings));
		((TextView) getActivity().findViewById(R.id.app_version_text))
		 						 .setText(SnapCommonUtils.getCurrentVersion(getActivity()));
	}

	private void initWeighingMachine() {
		view.findViewById(R.id.change_weighing_machine_btn).setOnClickListener(this);
		view.findViewById(R.id.pair_weighing_machine_btn).setOnClickListener(this);
		((Switch) view.findViewById(R.id.weighing_machine_switch))
									  .setChecked(SnapSharedUtils.isWeighingMachine(getActivity()));
		((Switch) view.findViewById(R.id.weighing_machine_switch))
									  .setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SnapSharedUtils.enableWeighingMachine(mContext, isChecked);
			}
		});
		updateWeightMachineText();		
	}
	
	private void updateWeightMachineText() {
		((TextView) view.findViewById(R.id.change_weighing_machine_textview))
						.setText(getActivity().getResources().getString(R.string.weighing_machine_text) 
						+ " [ " + SnapSharedUtils.getWeighingMachineName(getActivity())+ " ]");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onClick(View v) {
		GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(mContext, v, getClass().getName());
		switch (v.getId()) {
			case R.id.change_weighing_machine_btn:
				final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
				final List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
				devices.addAll(adapter.getBondedDevices());
				final String[] btDeviceNames = BluetoothHelper.getDeviceNames(devices);
				if (btDeviceNames == null || btDeviceNames.length <= 0) {
					Toast.makeText(mContext, getResources().getString(R.string.no_paired_device_msg),
							Toast.LENGTH_LONG).show();
					return;
				}
				btDevicesDialog = SnapCommonUtils.buildAlertListViewDialog(
						mContext.getResources().getString(R.string.select_weighing_machine),
						btDeviceNames, mContext, new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								if (btDevicesDialog != null)
									btDevicesDialog.dismiss();
								btDevicesDialog = null;
								BluetoothDevice device = devices.get(position);
								if (device == null)
									return;
								String btDeviceName = device.getName();
								if (btDeviceName == null || btDeviceName.isEmpty())
									btDeviceName = device.getAddress();
								SnapSharedUtils.setWeighingMachine(mContext, btDeviceName, device.getAddress());
								updateWeightMachineText();
							}
						});
				btDevicesDialog.show();
				break;
			case R.id.pair_weighing_machine_btn:
				final ProgressDialog dialog = new ProgressDialog(mContext);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setTitle(getResources().getString(R.string.bluetooth_scan_msg));
				dialog.setMessage(getResources().getString(R.string.serach_progress_text));
				dialog.show();
				BluetoothHelper helper = new BluetoothHelper(mContext, new BluetoothHelper.ScanCompleteListener() {
					@Override
					public void scanComplete(String status) {
						dialog.dismiss();
						if (status != null && !status.isEmpty())
							Toast.makeText(mContext, status, Toast.LENGTH_LONG).show();
					}
				});
				if (!helper.pairNewDevice()) {
					dialog.dismiss();
					Toast.makeText(mContext, getResources().getString(R.string.bluetooth_search_failed),
							Toast.LENGTH_LONG).show();
				}
				break;
			default:
				break;
		}		
	}
}

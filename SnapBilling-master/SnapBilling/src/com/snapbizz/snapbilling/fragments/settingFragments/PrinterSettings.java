package com.snapbizz.snapbilling.fragments.settingFragments;

import java.util.ArrayList;
import java.util.List;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.BluetoothHelper;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snaptoolkit.utils.CustomToast;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class PrinterSettings extends Fragment implements OnClickListener {
	
	private Context mContext;
	private View view;
	private Dialog btDevicesDialog = null;
	private Dialog printerWidthDialog = null;

	public PrinterSettings(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.printer_settings, container, false);
		initPrinterSettings();
		return view;
	}

	private void initPrinterSettings() {
		((Button) view.findViewById(R.id.configuration_button)).setOnClickListener(this);
		((Button) view.findViewById(R.id.customization_button)).setOnClickListener(this);
		((Button) view.findViewById(R.id.pair_printer_button)).setOnClickListener(this);
		((Button) view.findViewById(R.id.change_printer_button)).setOnClickListener(this);
		((Button) view.findViewById(R.id.save_printer_settings)).setOnClickListener(this);
		((Button) view.findViewById(R.id.save_printer_customization)).setOnClickListener(this);
		updateSelectPrinterText();
		setSpinnerValues();
		setStoredSwitchValues();
		setStoredCustomization();
	}
	
	private int getFontSpinnerIndex(Spinner spinner, String selectedFont) {
		int index = 0;
		for (int i = 0; i < spinner.getCount(); i++) {
			if ((spinner.getItemAtPosition(i).toString()).equalsIgnoreCase(selectedFont)) {
				index = i;
				break;
			}
		}
		return index;
	}

	private void setSpinnerValues() {
		
		((Spinner) view.findViewById(R.id.header_spinner)).setSelection(
								 getFontSpinnerIndex(((Spinner) view.findViewById(R.id.header_spinner)),
								 SnapSharedUtils.getHeaderFontSize(mContext)));
		((Spinner) view.findViewById(R.id.content_spinner)).setSelection(
				 				getFontSpinnerIndex(((Spinner) view.findViewById(R.id.content_spinner)),
				 				SnapSharedUtils.getContentFontSize(mContext)));
		((Spinner) view.findViewById(R.id.footer1_spinner)).setSelection(
 								getFontSpinnerIndex(((Spinner) view.findViewById(R.id.footer1_spinner)),
 								SnapSharedUtils.getFooter1FontSize(mContext)));
		((Spinner) view.findViewById(R.id.footer2_spinner)).setSelection(
								getFontSpinnerIndex(((Spinner) view.findViewById(R.id.footer2_spinner)),
								SnapSharedUtils.getFooter2FontSize(mContext)));
		((Spinner) view.findViewById(R.id.footer3_spinner)).setSelection(
								getFontSpinnerIndex(((Spinner) view.findViewById(R.id.footer3_spinner)),
								SnapSharedUtils.getFooter3FontSize(mContext)));
	}
	
	private void setStoredSwitchValues() {
		((Switch) view.findViewById(R.id.auto_connect_switch)).setChecked(SnapSharedUtils.getAutoConnect(mContext));
		((Switch) view.findViewById(R.id.spacing_switch)).setChecked(SnapSharedUtils.getExtraLinePrintValue(mContext));
		((Switch) view.findViewById(R.id.serial_num_switch)).setChecked(SnapSharedUtils.getSerialNumber(mContext));
		((Switch) view.findViewById(R.id.print_vat)).setChecked(SnapSharedUtils.getPrinterVAT(mContext));
		((Switch) view.findViewById(R.id.print_mrp_switch)).setChecked(SnapSharedUtils.getPrinterMRP(mContext));
		((Switch) view.findViewById(R.id.print_summary_switch)).setChecked(SnapSharedUtils.getPrinterSummary(mContext));
		((Switch) view.findViewById(R.id.print_estimate_switch)).setChecked(SnapSharedUtils.getPrintEstimate(mContext));
		((Switch) view.findViewById(R.id.print_savings_switch)).setChecked(SnapSharedUtils.getPrintCheckValue(mContext));
	}
	
	private void updateSelectPrinterText() {
		((TextView) view.findViewById(R.id.change_printer_text)).setText(getActivity().getResources()
						.getString(R.string.settingsfragment_current_printer)
						+ " [ " +SnapSharedUtils.getSelectedPrinterName(mContext) + " ]");
	}
	
	private void setStoredCustomization() {
		((EditText) view.findViewById(R.id.store_name)).setText(SnapSharedUtils.getPrinterStoreName(mContext));
		((EditText) view.findViewById(R.id.store_address)).setText(SnapSharedUtils.getPrinterStoreAddress(mContext));
		((EditText) view.findViewById(R.id.store_contact)).setText(SnapSharedUtils.getPrinterStoreNumber(mContext));
		((EditText) view.findViewById(R.id.tin_number)).setText(SnapSharedUtils.getPrinterStoreTinNumber(mContext));
		((EditText) view.findViewById(R.id.store_city)).setText(SnapSharedUtils.getPrinterStoreCity(mContext));
		((EditText) view.findViewById(R.id.footer1)).setText(SnapSharedUtils.getPrinterFooter1(mContext));
		((EditText) view.findViewById(R.id.footer2)).setText(SnapSharedUtils.getPrinterFooter2(mContext));
		((EditText) view.findViewById(R.id.footer3)).setText(SnapSharedUtils.getPrinterFooter3(mContext));
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.configuration_button:
				((LinearLayout) view.findViewById(R.id.printer_configuration)).setVisibility(View.VISIBLE);
				((LinearLayout) view.findViewById(R.id.printer_customization)).setVisibility(View.GONE);
				v.setBackgroundResource(R.drawable.printer_dark_blue_button);
				((Button) view.findViewById(R.id.customization_button)).setBackgroundResource(R.drawable.printer_light_blue_color);
				break;
			case R.id.customization_button:
				((LinearLayout) view.findViewById(R.id.printer_configuration)).setVisibility(View.GONE);
				((LinearLayout) view.findViewById(R.id.printer_customization)).setVisibility(View.VISIBLE);	
				v.setBackgroundResource(R.drawable.printer_dark_blue_button);
				((Button) view.findViewById(R.id.configuration_button)).setBackgroundResource(R.drawable.printer_light_blue_color);
				break;
			case R.id.pair_printer_button:
				final ProgressDialog dialog = new ProgressDialog(getActivity());
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setTitle(getResources().getString(R.string.bluetooth_scan_msg));
				dialog.setMessage(getResources().getString(R.string.serach_progress_text));
				dialog.show();
				BluetoothHelper helper = new BluetoothHelper(getActivity(), new BluetoothHelper.ScanCompleteListener() {
					@Override
					public void scanComplete(String status) {
						dialog.dismiss();
						if (status != null && !status.isEmpty())
							Toast.makeText(getActivity(), status, Toast.LENGTH_LONG).show();
					}
				});
				if (!helper.pairNewDevice()) {
					dialog.dismiss();
					Toast.makeText(getActivity(), getResources().getString(R.string.bluetooth_search_failed),
							Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.change_printer_button:
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(mContext, v, getClass().getName());
				final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
				final List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
				devices.addAll(adapter.getBondedDevices());
				final String[] names = BluetoothHelper.getDeviceNames(devices);
				if (names == null || names.length <= 0) {
					Toast.makeText(getActivity(), getResources().getString(R.string.no_paired_device_msg),
							Toast.LENGTH_LONG).show();
					return;
				}
				
				final String[] printerWidths = {"2 Inch", "3 Inch"};
				btDevicesDialog = SnapCommonUtils.buildAlertListViewDialog(
						getActivity().getResources().getString(R.string.select_printer_text),
						names, mContext, new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								if (btDevicesDialog != null)
									btDevicesDialog.dismiss();
								btDevicesDialog = null;
								final BluetoothDevice device = devices.get(position);
								if (device == null)
									return;
								String printerName = device.getName();
								if (printerName == null || printerName.isEmpty())
									printerName = device.getAddress();
								final String deviceName = printerName;
								printerWidthDialog = SnapCommonUtils.buildAlertListViewDialog(
										getActivity().getResources().getString(R.string.select_printer_size),
										printerWidths, getActivity(), new OnItemClickListener() {

											@Override
											public void onItemClick(
													AdapterView<?> printerWidthParent,
													View printerWidthView, int printerWidthPosition,
													long printerWidthId) {
												if (printerWidthDialog != null)
													printerWidthDialog.dismiss();
												printerWidthDialog = null;
												int[] printerWidthInt = {2, 3};
												int selectedPrinterSize = printerWidthInt[printerWidthPosition];
												SnapSharedUtils.setSelectedPrinter(mContext, deviceName,
														selectedPrinterSize, device.getAddress());
											}
										});
								printerWidthDialog.show();
								updateSelectPrinterText();
							}
						});
				
				btDevicesDialog.show();
				break;
			case R.id.save_printer_settings:
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(mContext, v, getClass().getName());
				SnapCommonUtils.hideSoftKeyboard(mContext, v.getApplicationWindowToken());
				SnapSharedUtils.setAutoConnect(((Switch) view.findViewById(R.id.auto_connect_switch)).isChecked(), mContext);
				SnapSharedUtils.setExtraLinePrintValue(((Switch) view.findViewById(R.id.spacing_switch)).isChecked(), mContext);
				SnapSharedUtils.setSerialNumber(((Switch) view.findViewById(R.id.serial_num_switch)).isChecked(), mContext);
				SnapSharedUtils.setPrinterVat(((Switch) view.findViewById(R.id.print_vat)).isChecked(), mContext);
				SnapSharedUtils.setPrinterMRP(((Switch) view.findViewById(R.id.print_mrp_switch)).isChecked(), mContext);
				SnapSharedUtils.setPrinterSummary(((Switch) view.findViewById(R.id.print_summary_switch)).isChecked(), mContext);
				SnapSharedUtils.setPrintEstimate(((Switch) view.findViewById(R.id.print_estimate_switch)).isChecked(), mContext);
				SnapSharedUtils.setPrintCheckValue(((Switch) view.findViewById(R.id.print_savings_switch)).isChecked(), mContext);
				CustomToast.showCustomToast(getActivity(),
						getResources().getString(R.string.printer_config_stored), Toast.LENGTH_SHORT,
						CustomToast.INFORMATION);
				break;
			case R.id.save_printer_customization:
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(mContext, v, getClass().getName());
				SnapCommonUtils.hideSoftKeyboard(mContext, v.getApplicationWindowToken());
				SnapSharedUtils.storeFontSize(mContext,
						((Spinner) view.findViewById(R.id.header_spinner)).getSelectedItem().toString(),
						((Spinner) view.findViewById(R.id.content_spinner)).getSelectedItem().toString(),
						((Spinner) view.findViewById(R.id.footer1_spinner)).getSelectedItem().toString(),
						((Spinner) view.findViewById(R.id.footer2_spinner)).getSelectedItem().toString(),
						((Spinner) view.findViewById(R.id.footer3_spinner)).getSelectedItem().toString());
				SnapSharedUtils.saveStoreDetails(mContext,
						((EditText) view.findViewById(R.id.store_name)).getText().toString(),
						((EditText) view.findViewById(R.id.store_address)).getText().toString(),
						((EditText) view.findViewById(R.id.store_contact)).getText().toString(),
						((EditText) view.findViewById(R.id.tin_number)).getText().toString(),
						((EditText) view.findViewById(R.id.store_city)).getText().toString(),
						((EditText) view.findViewById(R.id.footer1)).getText().toString(),
						((EditText) view.findViewById(R.id.footer2)).getText().toString(),
						((EditText) view.findViewById(R.id.footer3)).getText().toString());
				CustomToast.showCustomToast(getActivity(),
						getResources().getString(R.string.printer_customization_stored), Toast.LENGTH_SHORT,
						CustomToast.INFORMATION);
				break;
			default:
				break;
		}
	}
}

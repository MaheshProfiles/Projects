package com.snapbizz.snapbilling.fragments.settingFragments;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PinSettings extends Fragment implements OnClickListener {
	private Context mContext;
	private final int MIN_PIN_CHARS = 4;
	private View view;
	
	public PinSettings(Context context) {
		this.mContext = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.pin_settings, container, false);
		initPinSettings();
		return view;
	}

	private void initPinSettings() {
		((Button) view.findViewById(R.id.save_pin_settings)).setOnClickListener(this);		
		String existingPin = SnapSharedUtils.getLastStoredPin(mContext);
		if (existingPin != null && !existingPin.isEmpty())
			showExistingPinLayout();
	}

	private void showExistingPinLayout() {
		((LinearLayout) view.findViewById(R.id.existing_pin_layout)).setVisibility(View.VISIBLE);
		((LinearLayout) view.findViewById(R.id.new_pin_layout)).setVisibility(View.GONE);
		((Button) view.findViewById(R.id.change_pin_button)).setOnClickListener(this);
		((Button) view.findViewById(R.id.delete_pin_button)).setOnClickListener(this);
	}
		
	public void showNewPinLayout() {
		((LinearLayout) view.findViewById(R.id.existing_pin_layout)).setVisibility(View.GONE);
		((LinearLayout) view.findViewById(R.id.new_pin_layout)).setVisibility(View.VISIBLE);
		((Button) view.findViewById(R.id.cancel_change_pin_btn)).setOnClickListener(this);
	}
	
	private void validatePassword(String pinValue, String renteredPinValue) {
		if (pinValue.equals(renteredPinValue)) {
			if (pinValue.length() < MIN_PIN_CHARS) {
				Toast.makeText(getActivity(), getString(R.string.msg_minimum_char_pin),
						Toast.LENGTH_LONG).show();
				return;
			}
			SnapSharedUtils.storeLastStoredPin(mContext, pinValue);
			Toast.makeText(getActivity(), getString(R.string.msg_pin_created),
					Toast.LENGTH_LONG).show();
			showExistingPinLayout();
			return;
		} else {
			Toast.makeText(getActivity(), getString(R.string.msg_pin_not_matching),
					Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	private void clearAllValues(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View childView = viewGroup.getChildAt(i);
			if (childView instanceof EditText)
				((EditText)childView).setText("");
		}
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
		SnapCommonUtils.hideSoftKeyboard(mContext, v.getApplicationWindowToken());
		switch (v.getId()) {
			case R.id.save_pin_settings:
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(mContext, v, getClass().getName());
				String pinValue = ((EditText) view.findViewById(R.id.newpin_value)).getText().toString();
				String renteredPinValue = ((EditText) view.findViewById(R.id.renterpin_value)).getText().toString();			
				
				if (pinValue.isEmpty() || renteredPinValue.isEmpty()) {
					Toast.makeText(getActivity(), getString(R.string.msg_enter_pin),
							Toast.LENGTH_LONG).show();
					return;
				} 				
				validatePassword(pinValue, renteredPinValue);
				clearAllValues((ViewGroup) view.findViewById(R.id.new_pin_layout));
				break;
			case R.id.change_pin_button:
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(mContext, v, getClass().getName());
				String oldPinVal = ((EditText) view.findViewById(R.id.existing_pin_val)).getText().toString();
				if (!oldPinVal.isEmpty()) {
					if (oldPinVal.equals(SnapSharedUtils.getLastStoredPin(mContext))) {
						showNewPinLayout();
						clearAllValues((ViewGroup) view.findViewById(R.id.existing_pin_layout));
					} else {
						Toast.makeText(getActivity(), getString(R.string.msg_pin_match),
								Toast.LENGTH_LONG).show();
						return;
					}					
				} else {
					Toast.makeText(getActivity(), getString(R.string.msg_enter_pin),
							Toast.LENGTH_LONG).show();
					return;
				}
				break;
			case R.id.delete_pin_button:
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(mContext, v, getClass().getName());
				final String deleteOldPinVal = ((EditText) view.findViewById(R.id.existing_pin_val)).getText().toString(); 
				if (!deleteOldPinVal.isEmpty()) {
					SnapCommonUtils.showDeleteAlert(mContext,
							getResources().getString(R.string.delete_pin), 
							getResources().getString(R.string.confirm_pin_delete),
							new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									if (deleteOldPinVal.equals(SnapSharedUtils.getLastStoredPin(mContext))) {
										SnapSharedUtils.storeLastStoredPin(mContext, "");
										SnapCommonUtils.dismissAlert();
										showNewPinLayout();
									} else {
										Toast.makeText(getActivity(), getString(R.string.msg_pin_match),
												Toast.LENGTH_LONG).show();
										SnapCommonUtils.dismissAlert();
										return;
									}
								}
							},
							new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									SnapCommonUtils.dismissAlert();
								}
							}, true);
					clearAllValues((ViewGroup) view.findViewById(R.id.existing_pin_layout));
				} else {
					Toast.makeText(getActivity(), getString(R.string.msg_enter_pin),
							Toast.LENGTH_LONG).show();
					return;
				}
				break;
			case R.id.cancel_change_pin_btn:
				if (!SnapSharedUtils.getLastStoredPin(mContext).isEmpty())
					showExistingPinLayout();
				clearAllValues((ViewGroup) view.findViewById(R.id.new_pin_layout));
				break;
			default:
					break;
		}		
	}
}

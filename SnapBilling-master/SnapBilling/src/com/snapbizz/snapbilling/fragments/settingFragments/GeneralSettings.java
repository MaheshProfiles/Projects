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
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class GeneralSettings extends Fragment implements OnCheckedChangeListener {
	private Context mContext;
	private final String TAG = "[SettingsFragment]";

	public GeneralSettings(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment_general_settings, container, false);
		initSettings(view);
		return view;
	}
	
	private void initSettings(View view) {
		setSwitchValues(view, R.id.sort_switch, SnapSharedUtils.getSortingCheckValue(mContext));
		setSwitchValues(view, R.id.round_off_switch, SnapSharedUtils.getRoundoffCheckValue(mContext));
		setSwitchValues(view, R.id.visibility_switch, SnapSharedUtils.getVisiblitySwitchStatus(mContext));
		setSwitchValues(view, R.id.display_rate_switch, SnapSharedUtils.getOfferVisible(mContext));
		setSwitchValues(view, R.id.cart_switch, SnapSharedUtils.isCartActiveOnTouch(mContext));
		setSwitchValues(view, R.id.enable_multi_scanner, SnapSharedUtils.isMultiScanner(mContext));
	}
	
	private void setSwitchValues(View view, int id, boolean value) {
		((Switch) view.findViewById(id)).setChecked(value);
		((Switch) view.findViewById(id)).setOnCheckedChangeListener(this);
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(mContext, buttonView, TAG + " " + isChecked);
		switch (buttonView.getId()) {
			case R.id.sort_switch:
				SnapSharedUtils.setSortingCheckValue(isChecked, mContext);
				break;
			case R.id.round_off_switch:
				SnapSharedUtils.setRoundoffCheckValue(isChecked, mContext);
				break;
			case R.id.visibility_switch:
				SnapSharedUtils.setVisiblitySwitchStatus(isChecked, mContext);
				break;
			case R.id.display_rate_switch:
				SnapSharedUtils.setOfferVisible(isChecked, mContext);
				break;
			case R.id.cart_switch:
				SnapSharedUtils.setCartActiveOnTouch(isChecked, mContext);
				break;
			case R.id.enable_multi_scanner:
				SnapSharedUtils.setMultiScanner(isChecked, getActivity());
				break;
			default:
				break;
		}
	}
	
}

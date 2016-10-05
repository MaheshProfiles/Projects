package com.snapbizz.snapbilling.fragments.settingFragments;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MultiPosSettings extends Fragment implements OnClickListener {
	
	private Context mContext;
	private View view;
	
	public MultiPosSettings(Context context) {
		this.mContext = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.multi_pos_settings, container, false);
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}

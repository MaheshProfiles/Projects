package com.snapbizz.snapbilling.fragments;

import java.util.ArrayList;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.SettingsDrawerItemAdapter;
import com.snapbizz.snapbilling.domains.SettingsDrawerItem;
import com.snapbizz.snapbilling.fragments.settingFragments.GeneralSettings;
import com.snapbizz.snapbilling.fragments.settingFragments.MultiPosSettings;
import com.snapbizz.snapbilling.fragments.settingFragments.PinSettings;
import com.snapbizz.snapbilling.fragments.settingFragments.PrinterSettings;
import com.snapbizz.snapbilling.fragments.settingFragments.WeighingMachineSettings;
import com.snapbizz.snapbilling.utils.TooltipWindow;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SettingsFragment extends Fragment {
	
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private ListView mDrawerList;
	private Context mContext;
	private ArrayList<Integer> selectedIds = new ArrayList<Integer>();
	final String PHONE_NUMBER = "+91 98450 90075";
	final String EMAIL = "support@snapbizz.com";
	final String HELP = "Need Help?";
	TooltipWindow tooltip;

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
		try {
			onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("activity " + activity.toString()
					+ getString(R.string.exc_implementnavigation));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.billing_settings_layout, container, false);
		mDrawerList = (ListView) view.findViewById(R.id.left_drawer_list);
		
		SettingsDrawerItem drawerItem[] = new SettingsDrawerItem[getResources().
		                                      getStringArray(R.array.new_settings_tabs).length];
		int icons[] = {R.drawable.icon_general_settings, R.drawable.icon_printer, 
					   R.drawable.icon_weighing_machine, R.drawable.icon_pin_settings,
					   R.drawable.icon_pos_settings};
		int activeIcons[] = {R.drawable.icon_general_settings_active, R.drawable.icon_printer_active, 
				   	   R.drawable.icon_weighing_machine_active, R.drawable.icon_pin_settings_active,
				       R.drawable.icon_pos_settings_active};
		for (int i = 0; i < drawerItem.length; i++)
			drawerItem[i] = new SettingsDrawerItem(icons[i], activeIcons[i],
										getResources().getStringArray(R.array.new_settings_tabs)[i]);
		if (null == mContext)
			mContext = getActivity();
		selectedIds.add(0);
		SettingsDrawerItemAdapter adapter = new SettingsDrawerItemAdapter(mContext,
											R.layout.settings_drawer_listitem_row, drawerItem, selectedIds);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerList.performItemClick(mDrawerList.getChildAt(0), 0, adapter.getItemId(0));
		
		
		LayoutInflater inflater2 = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View convertView = inflater2.inflate(R.layout.tooltip_layout, null);
		tooltip = new TooltipWindow(mContext, convertView);
		((ImageView) view.findViewById(R.id.call)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((TextView) convertView.findViewById(R.id.tooltip_text)).setText(PHONE_NUMBER);
				tooltip.showToolTip(v);
			}
		});
		
		((ImageView) view.findViewById(R.id.email)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((TextView) convertView.findViewById(R.id.tooltip_text)).setText(EMAIL);
				tooltip.showToolTip(v);
			}
		});
		
		((ImageView) view.findViewById(R.id.query)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((TextView) convertView.findViewById(R.id.tooltip_text)).setText(HELP);
				tooltip.showToolTip(v);
			}
		});		
		
		return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation("", menuItem.getItemId());
		return true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.findItem(R.id.new_settings_fragment).setVisible(false);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View listItem, int position, long id) {
			selectedIds.clear();
			selectedIds.add(position);
			selectItem(position);
		}

		private void selectItem(int position) {
			Fragment fragment = null;
			switch (position) {
				case 0:
					fragment = new GeneralSettings(mContext);
					break;
				case 1:
					fragment = new PrinterSettings(mContext);
					break;
				case 2:
					fragment = new WeighingMachineSettings(mContext);
					break;
				case 3:
					fragment = new PinSettings(mContext);
					break;
				case 4:
					fragment = new MultiPosSettings(mContext);
					break;
				default:
					break;
			}
			
			if (fragment != null) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();				
				ft.replace(R.id.content_frame, fragment).commit();				
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
			}
		}
	}

}

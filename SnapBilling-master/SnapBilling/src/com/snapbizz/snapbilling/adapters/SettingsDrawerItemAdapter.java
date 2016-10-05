package com.snapbizz.snapbilling.adapters;

import java.util.ArrayList;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.domains.SettingsDrawerItem;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsDrawerItemAdapter extends ArrayAdapter<SettingsDrawerItem> {
	Context mContext;
	int layoutResourceId;
	SettingsDrawerItem[] data = null;
	public ArrayList<Integer> selectedIds;

	public SettingsDrawerItemAdapter(Context context, int layoutResourceId,
									 SettingsDrawerItem[] data, ArrayList<Integer> seletctedIds) {
		super(context, layoutResourceId, data);
		this.mContext = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
		this.selectedIds = seletctedIds;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItem = convertView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			listItem = inflater.inflate(layoutResourceId, null);
		}
		SettingsDrawerItem menuItem = data[position];        
		((ImageView) listItem.findViewById(R.id.menu_icon)).setImageResource(menuItem.icon);
		((TextView) listItem.findViewById(R.id.menu_item_name)).setText(menuItem.name);
		((TextView) listItem.findViewById(R.id.menu_item_name))
							.setTextColor(mContext.getResources().getColor(R.color.settings_menu_font_color));
		if (selectedIds.contains(position)) {
			((ImageView) listItem.findViewById(R.id.menu_icon)).setImageResource(menuItem.activeIcon);
			((TextView) listItem.findViewById(R.id.menu_item_name))
								.setTextColor(mContext.getResources().getColor(R.color.settings_menu_active_color));
		}			
		return listItem;
	}
}

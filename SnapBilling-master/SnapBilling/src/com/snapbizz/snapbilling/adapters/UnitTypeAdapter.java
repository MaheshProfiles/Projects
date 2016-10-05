package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;

public class UnitTypeAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;
    private int lastSelectedPosition = 0;
    
    public UnitTypeAdapter(Context context, List<String> unitTypeList) {
        super(context, android.R.id.text1, unitTypeList);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView unitTypeTextView;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_unittype, null);
            unitTypeTextView = (TextView) convertView.findViewById(R.id.unitype_textview);
            convertView.setTag(unitTypeTextView);
        } else {
            unitTypeTextView = (TextView) convertView.getTag();
        }
        if (lastSelectedPosition == position) {
            unitTypeTextView.setSelected(true);
        } else {
            unitTypeTextView.setSelected(false);
        }
        unitTypeTextView.setText(getItem(position));
        return convertView;
    }

    public void setLastSelectedItemPosition(int position) {
        this.lastSelectedPosition = position;
    }

}

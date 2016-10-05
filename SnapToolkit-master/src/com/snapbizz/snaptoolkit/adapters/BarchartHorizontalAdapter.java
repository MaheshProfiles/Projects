package com.snapbizz.snaptoolkit.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.BarGraphDataPoint;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class BarchartHorizontalAdapter extends BaseAdapter {
	
	public static final String TAG = BarchartHorizontalAdapter.class.getCanonicalName();
	private Context context;
	private double max = 0;
	private List<BarGraphDataPoint> barGraphDataPointList;
	private boolean isPriceChart;
	private int layoutId = R.layout.listitem_horz_barchart;
	
	public BarchartHorizontalAdapter(Context context, List<BarGraphDataPoint> barGraphDataPointList) {
		this.context = context;
		this.barGraphDataPointList = barGraphDataPointList;
		double comparator = 0;
		for(BarGraphDataPoint barGraphDataPoint : barGraphDataPointList) {
			if(barGraphDataPoint.getDataPoint1() > barGraphDataPoint.getDataPoint2())
				comparator = barGraphDataPoint.getDataPoint1();
			else
				comparator = barGraphDataPoint.getDataPoint2();
			max = max > comparator ? max : comparator;
		}
	}

	public void setLayoutId(int layoutId) {
			this.layoutId = layoutId;
	}
	
	public boolean isPriceChart() {
		return isPriceChart;
	}

	public void setPriceChart(boolean isPriceChart) {
		this.isPriceChart = isPriceChart;
	}
	
	public void clear() {
		barGraphDataPointList.clear();
	}
	
	public void addAll(List<BarGraphDataPoint> barGraphDataPointList) {
		this.barGraphDataPointList.addAll(barGraphDataPointList);
	}
	
	@Override
	public int getCount() {
		return barGraphDataPointList.size();
	}

	@Override
	public BarGraphDataPoint getItem(int pos) {
		return barGraphDataPointList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View v = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(layoutId, null);
			holder.bar1 = (TextView) v.findViewById(R.id.bar1_textview);
			holder.bar1LabelTextView = (TextView) v.findViewById(R.id.bar1_label_textview);
			holder.graphXLabelTextView = (TextView) v.findViewById(R.id.horizontal_label);
			v.setTag(holder);
		} else {
			v = convertView;
		}
		holder = (ViewHolder) v.getTag();
		BarGraphDataPoint barGraphDataPoint = getItem(pos);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.bar1.getLayoutParams();
		params.width = barGraphDataPoint.getBar1Height();
		holder.bar1.setLayoutParams(params);
		if(isPriceChart) {
			holder.bar1LabelTextView.setText(SnapToolkitTextFormatter.formatPriceText(barGraphDataPoint.getDataPoint1(), context));
		} else {
			holder.bar1LabelTextView.setText(((int)barGraphDataPoint.getDataPoint1())+"");
		}
		holder.graphXLabelTextView.setText(barGraphDataPoint.getLabel());
			
		return v;
	}

	private class ViewHolder {
		TextView graphXLabelTextView;
		TextView bar1;
		TextView bar1LabelTextView;
	}
	
	public void adjustBarWidths(int width) {
		if(width == 0) {
			width = 400;
		}
		max = 0;
		double comparator = 0;
		for(BarGraphDataPoint barGraphDataPoint : barGraphDataPointList) {
			if(barGraphDataPoint.getDataPoint1() > barGraphDataPoint.getDataPoint2())
				comparator = barGraphDataPoint.getDataPoint1();
			else
				comparator = barGraphDataPoint.getDataPoint2();
			max = max > comparator ? max : comparator;
		}
		for(BarGraphDataPoint barGraphDataPoint : this.barGraphDataPointList) {
            barGraphDataPoint.setBar1Height((int)((width * barGraphDataPoint.getDataPoint1()) / max));
            barGraphDataPoint.setBar2Height((int)((width * barGraphDataPoint.getDataPoint2()) / max));
        }
	}

	public interface OnItemClickListener {
		public void onClick(int pos);
	}
}

package com.snapbizz.snaptoolkit.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.BarGraphDataPoint;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class BarchartAdapter extends BaseAdapter {
	
	public static final String TAG = BarchartAdapter.class.getCanonicalName();
	private Context context;
	private double max = 0;
	private List<BarGraphDataPoint> barGraphDataPointList;
	private boolean isMultiSeries;
	private boolean isPriceChart;

	public BarchartAdapter(Context context, List<BarGraphDataPoint> barGraphDataPointList) {
		this.context = context;
		this.barGraphDataPointList = barGraphDataPointList;
	}
	
	public BarchartAdapter(Context context, List<BarGraphDataPoint> barGraphDataPointList, boolean isMultiSeries) {
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
		this.isMultiSeries = isMultiSeries;
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
			v = inflater.inflate(R.layout.listitem_barchart, null);
			holder.bar1 = (TextView) v.findViewById(R.id.bar1_textview);
			holder.bar2 = (TextView) v.findViewById(R.id.bar2_textview);
			holder.bar1LabelTextView = (TextView) v.findViewById(R.id.bar1_label_textview);
			holder.bar2LabelTextView = (TextView) v.findViewById(R.id.bar2_label_textview);
			holder.graphXLabelTextView = (TextView) v.findViewById(R.id.horizontal_label);
			v.setTag(holder);
		} else {
			v = convertView;
		}
		holder = (ViewHolder) v.getTag();
		BarGraphDataPoint barGraphDataPoint = getItem(pos);
		holder.bar1.setHeight(barGraphDataPoint.getBar1Height());
		holder.bar2.setHeight(barGraphDataPoint.getBar2Height());
		if(isPriceChart) {
			holder.bar1LabelTextView.setText(SnapToolkitTextFormatter.formatPriceText(barGraphDataPoint.getDataPoint1(), context));
			holder.bar2LabelTextView.setText(SnapToolkitTextFormatter.formatPriceText(barGraphDataPoint.getDataPoint2(), context));
		} else {
			holder.bar1LabelTextView.setText(((int)barGraphDataPoint.getDataPoint1())+"");
			holder.bar2LabelTextView.setText(((int)barGraphDataPoint.getDataPoint2())+"");
		}
		holder.graphXLabelTextView.setText(barGraphDataPoint.getLabel());
		if(!isMultiSeries) {
			holder.bar2.setVisibility(View.GONE);
			holder.bar2LabelTextView.setVisibility(View.GONE);
		} else {
			holder.bar2.setVisibility(View.VISIBLE);
			holder.bar2LabelTextView.setVisibility(View.VISIBLE);
		}
			
		return v;
	}

	private class ViewHolder {
		TextView graphXLabelTextView;
		TextView bar1;
		TextView bar2;
		TextView bar1LabelTextView;
		TextView bar2LabelTextView;
	}
	
	public void adjustBarHeights(int height) {
		if(height == 0) {
			height = 400;
		} else
			height = (int) (height * 0.8);
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
            barGraphDataPoint.setBar1Height((int)((height * barGraphDataPoint.getDataPoint1()) / max));
            barGraphDataPoint.setBar2Height((int)((height * barGraphDataPoint.getDataPoint2()) / max));
        }
	}

	public interface OnItemClickListener {
		public void onClick(int pos);
	}
}

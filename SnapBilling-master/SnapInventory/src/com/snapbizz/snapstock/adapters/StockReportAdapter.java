package com.snapbizz.snapstock.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.domains.StockReport;

public class StockReportAdapter extends ArrayAdapter<StockReport> {

	private LayoutInflater inflater;
	private Context context;
	
	public StockReportAdapter(Context context, List<StockReport> objects) {
		super(context, android.R.id.text1, objects);
		// TODO Auto-generated constructor stub
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		StockReportAdapterWrapper stockReportAdapterWrapper;
		if (convertView == null) {
			stockReportAdapterWrapper = new StockReportAdapterWrapper();
			convertView = inflater.inflate(R.layout.listitem_stock_report, null);
			stockReportAdapterWrapper.filterNameTextView = (TextView) convertView.findViewById(R.id.stock_filtername_textview);
			stockReportAdapterWrapper.allDayOfStockTextView = (TextView) convertView.findViewById(R.id.stock_daysofstock_textview);
			stockReportAdapterWrapper.allProfitTextView = (TextView) convertView.findViewById(R.id.stock_profit_textview);
			stockReportAdapterWrapper.allStockSkuTextView = (TextView) convertView.findViewById(R.id.stock_sku_textview);
			stockReportAdapterWrapper.allRevenueTextView = (TextView) convertView.findViewById(R.id.stock_revenue_textview);
			stockReportAdapterWrapper.allStockValueTextView = (TextView) convertView.findViewById(R.id.stock_value_textview);
			stockReportAdapterWrapper.excessDayOfStockTextView = (TextView) convertView.findViewById(R.id.stock_excessdaysofstock_textview);
			stockReportAdapterWrapper.excessProfitTextView = (TextView) convertView.findViewById(R.id.stock_excessprofit_textview);
			stockReportAdapterWrapper.excessStockSkuTextView = (TextView) convertView.findViewById(R.id.stock_excesssku_textview);
			stockReportAdapterWrapper.excessRevenueTextView = (TextView) convertView.findViewById(R.id.stock_excessrevenue_textview);
			stockReportAdapterWrapper.excessStockValueTextView = (TextView) convertView.findViewById(R.id.stock_excessvalue_textview);
			stockReportAdapterWrapper.shortageDayOfStockTextView = (TextView) convertView.findViewById(R.id.stock_shortagedaysofstock_textview);
			stockReportAdapterWrapper.shortageProfitTextView = (TextView) convertView.findViewById(R.id.stock_shortageprofit_textview);
			stockReportAdapterWrapper.shortageStockSkuTextView = (TextView) convertView.findViewById(R.id.stock_shortagesku_textview);
			stockReportAdapterWrapper.shortageRevenueTextView = (TextView) convertView.findViewById(R.id.stock_shortagerevenue_textview);
			stockReportAdapterWrapper.shortageStockValueTextView = (TextView) convertView.findViewById(R.id.stock_shortagevalue_textview);
			convertView.setTag(stockReportAdapterWrapper);
		} else {
			stockReportAdapterWrapper = (StockReportAdapterWrapper) convertView.getTag();
		}
		StockReport stockReport = getItem(position);
		stockReportAdapterWrapper.filterNameTextView.setText(stockReport.getFilterName());
		stockReportAdapterWrapper.allDayOfStockTextView.setText(stockReport.getAllDaysofStock());
		stockReportAdapterWrapper.allProfitTextView.setText(stockReport.getAllStockProfit());
		stockReportAdapterWrapper.allRevenueTextView.setText(stockReport.getAllStockRevenue());
		stockReportAdapterWrapper.allStockSkuTextView.setText(stockReport.getAllStockSku());
		stockReportAdapterWrapper.allStockValueTextView.setText(stockReport.getAllStockValue());
		stockReportAdapterWrapper.excessDayOfStockTextView.setText(stockReport.getExcessDaysofStock());
		stockReportAdapterWrapper.excessProfitTextView.setText(stockReport.getExcessStockProfit());
		stockReportAdapterWrapper.excessRevenueTextView.setText(stockReport.getExcessStockRevenue());
		stockReportAdapterWrapper.excessStockSkuTextView.setText(stockReport.getExcessStockSku());
		stockReportAdapterWrapper.excessStockValueTextView.setText(stockReport.getExcessStockValue());
		stockReportAdapterWrapper.shortageDayOfStockTextView.setText(stockReport.getShortageDaysofStock());
		stockReportAdapterWrapper.shortageProfitTextView.setText(stockReport.getShortageStockProfit());
		stockReportAdapterWrapper.shortageRevenueTextView.setText(stockReport.getShortageStockRevenue());
		stockReportAdapterWrapper.shortageStockSkuTextView.setText(stockReport.getShortageStockSku());
		stockReportAdapterWrapper.shortageStockValueTextView.setText(stockReport.getShortageStockValue());
		
		return convertView;
	}

	private static class StockReportAdapterWrapper {
		public TextView filterNameTextView;
		public TextView allStockValueTextView;
		public TextView allStockSkuTextView;
		public TextView allDayOfStockTextView;
		public TextView allRevenueTextView;
		public TextView allProfitTextView;
		public TextView excessStockValueTextView;
		public TextView excessStockSkuTextView;
		public TextView excessDayOfStockTextView;
		public TextView excessRevenueTextView;
		public TextView excessProfitTextView;
		public TextView shortageStockValueTextView;
		public TextView shortageStockSkuTextView;
		public TextView shortageDayOfStockTextView;
		public TextView shortageRevenueTextView;
		public TextView shortageProfitTextView;
	}

}

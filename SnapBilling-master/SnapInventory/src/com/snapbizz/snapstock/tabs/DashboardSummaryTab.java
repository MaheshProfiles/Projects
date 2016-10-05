package com.snapbizz.snapstock.tabs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.asynctasks.GetSummaryTask;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.asynctasks.SaveViewToFileTask;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class DashboardSummaryTab implements OnQueryCompleteListener{
	private static final String TAG = DashboardSummaryTab.class.getSimpleName();

	private final int GET_SUMMARY_TASK = 0;
	private final int SAVE_IMAGE_TASKCODE = 2;
	private View view;
	private Context context;
	private GetSummaryTask getSummaryAsyncTask;
	private Bitmap bitmap;

	public DashboardSummaryTab(Context context) {
		this.context = context;
	}

	public void setView(View view) {
		this.view = view;
	}

	public void loadSummaryTab() {
		getSummaryAsyncTask = new GetSummaryTask(context, this, GET_SUMMARY_TASK);
		getSummaryAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		if(taskCode == GET_SUMMARY_TASK) {
			Double [] summary = (Double[]) responseList;
			((TextView)view.findViewById(R.id.total_stockvalue_textview)).setText(SnapToolkitTextFormatter.formatPriceText(summary[0], context));
			((TextView)view.findViewById(R.id.monthly_salesvalue_textview)).setText(SnapToolkitTextFormatter.formatPriceText(summary[1], context));
			((TextView)view.findViewById(R.id.monthly_profitvalue_textview)).setText(SnapToolkitTextFormatter.formatPriceText(summary[2], context));
			((TextView)view.findViewById(R.id.total_skusvalue_textview)).setText(SnapToolkitTextFormatter.formatNumberText(summary[3], context));
			((TextView)view.findViewById(R.id.new_skuvalue_textview)).setText(SnapToolkitTextFormatter.formatNumberText(summary[4], context));
			((TextView)view.findViewById(R.id.paid_stockvalue_textview)).setText(SnapToolkitTextFormatter.formatPriceText(summary[5], context));
			((TextView)view.findViewById(R.id.unpaid_stockvalue_textview)).setText(SnapToolkitTextFormatter.formatPriceText(summary[6], context));
			((TextView)view.findViewById(R.id.days_stockvalue_textview)).setText(SnapToolkitTextFormatter.formatNumberText((int)(summary[7] + 0.5d), context));
			view.setDrawingCacheBackgroundColor(Color.WHITE);
			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache(true);
			this.bitmap = view.getDrawingCache(true);
			new SaveViewToFileTask(DashboardSummaryTab.this , SAVE_IMAGE_TASKCODE, SnapInventoryUtils.getSummaryWidgetImagePath(context), bitmap).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		CustomToast.showCustomToast(context, errorMessage, Toast.LENGTH_SHORT, CustomToast.ERROR);
	}
}

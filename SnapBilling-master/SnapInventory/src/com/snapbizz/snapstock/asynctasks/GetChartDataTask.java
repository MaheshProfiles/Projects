package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapstock.utils.ChartFilterType;
import com.snapbizz.snapstock.utils.ChartFilterValueType;
import com.snapbizz.snaptoolkit.domains.BarGraphDataPoint;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.ChartType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class GetChartDataTask extends AsyncTask<Void, Void, List<BarGraphDataPoint>> {

    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "Could not calculate chart data";
    private ChartFilterValueType chartFilterValueType;
    private ChartFilterType chartFilterType;

    public GetChartDataTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode, ChartFilterValueType chartResolutionType, ChartFilterType chartFilterType) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.chartFilterValueType = chartResolutionType;
        this.chartFilterType = chartFilterType;
    }

    @Override
    protected List<BarGraphDataPoint> doInBackground(Void... params) {
        context = SnapCommonUtils.getSnapContext(context);
        if(chartFilterType.ordinal() == ChartFilterType.DISTRIBUTOR.ordinal()) {
            if(chartFilterValueType.ordinal() == ChartFilterValueType.REVENUE.ordinal())
                return SnapSharedUtils.getChartData(ChartType.REVENUE_DISTRIBUTOR, context);
            else if(chartFilterValueType.ordinal() == ChartFilterValueType.STOCK_VALUE.ordinal())
                return SnapSharedUtils.getChartData(ChartType.STOCK_VALUE_DISTRIBUTOR, context);
            else if(chartFilterValueType.ordinal() == ChartFilterValueType.DAYS_STOCK.ordinal()) 
                return SnapSharedUtils.getChartData(ChartType.DAYS_STOCK_DISTRIBUTOR, context);
        } else if(chartFilterType.ordinal() == ChartFilterType.COMPANY.ordinal()) {
            if(chartFilterValueType.ordinal() == ChartFilterValueType.REVENUE.ordinal())
                return SnapSharedUtils.getChartData(ChartType.REVENUE_COMPANY, context);
            else if(chartFilterValueType.ordinal() == ChartFilterValueType.STOCK_VALUE.ordinal())
                return SnapSharedUtils.getChartData(ChartType.STOCK_VALUE_COMPANY, context);
            else if(chartFilterValueType.ordinal() == ChartFilterValueType.DAYS_STOCK.ordinal()) 
                return SnapSharedUtils.getChartData(ChartType.DAYS_STOCK_COMPANY, context);
        } else if(chartFilterType.ordinal() == ChartFilterType.CATEGORY.ordinal()) {
            if(chartFilterValueType.ordinal() == ChartFilterValueType.REVENUE.ordinal())
                return SnapSharedUtils.getChartData(ChartType.REVENUE_CATEGORY, context);
            else if(chartFilterValueType.ordinal() == ChartFilterValueType.STOCK_VALUE.ordinal())
                return SnapSharedUtils.getChartData(ChartType.STOCK_VALUE_CATEGORY, context);
            else if(chartFilterValueType.ordinal() == ChartFilterValueType.DAYS_STOCK.ordinal())
                return SnapSharedUtils.getChartData(ChartType.DAYS_STOCK_CATEGORY, context);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<BarGraphDataPoint> result) {
        // TODO Auto-generated method stub
        if (result != null) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }

}

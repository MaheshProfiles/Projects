package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.ChartResolutionType;
import com.snapbizz.snaptoolkit.utils.VisibilityTag;

public class GetMoneyTask extends AsyncTask<Void, Void, Double[]> {
    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "Could not show you the money";
    private ChartResolutionType chartResolutionType;

    public GetMoneyTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode, ChartResolutionType chartResolutionType) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.chartResolutionType = chartResolutionType;
    }

    @Override
    protected Double[] doInBackground(Void... params) {
        String date = null;
        String transaction_date = null;
        if(chartResolutionType.ordinal() == ChartResolutionType.DAY.ordinal()) {
            date = new SimpleDateFormat(SnapBillingConstants.DAY_DATEFORMAT).format(Calendar.getInstance().getTime());
            transaction_date = "transactions.transaction_timestamp as transaction_date";
        } else if(chartResolutionType.ordinal() == ChartResolutionType.MONTH.ordinal()) {
            date = new SimpleDateFormat(SnapBillingConstants.DAY_DATEFORMAT).format(Calendar.getInstance().getTime()).substring(0, 7);
            transaction_date = "transactions.transaction_timestamp as transaction_date";
        } else if (chartResolutionType.ordinal() == ChartResolutionType.WEEK.ordinal()) {
            date = new SimpleDateFormat(SnapBillingConstants.STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
            date = date.substring(0, 5) + date.substring(11, 13);
            transaction_date = "SUBSTR(transactions.transaction_timestamp, 0, 5) || '/' || SUBSTR(transactions.transaction_timestamp, 12, 2) as transaction_date";
        }
        String whereClause ="transaction_date like ('"+date+"%')";
        String [] result = null;
        Double[] money = new Double[2];
        GenericRawResults<String[]> rawResults = null;

        try {
            rawResults = SnapBillingUtils.getHelper(context).getBillItemDao().queryRaw("select SUM(transaction_details.sku_sale_price * transaction_details.sku_quantity), "+transaction_date+" from transaction_details inner join transactions on transactions.transaction_id = transaction_details.transaction_id where (transaction_details.visibility_tag = '"+VisibilityTag.VISIBILITY_STORE.name()+"' or transaction_details.visibility_tag = '"+VisibilityTag.VISIBILITY_STORE_OFFER.name()+"') and "+whereClause+" and transaction_details.sku_quantity > 0");
            result = rawResults.getFirstResult();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(result == null || result[0] == null)
            money[0] = 0d;
        else
            money[0] = Double.parseDouble(result[0]);

        try {
            rawResults = SnapBillingUtils.getHelper(context).getBillItemDao().queryRaw("select SUM(transaction_details.sku_sale_price * transaction_details.sku_quantity), "+transaction_date+" from transaction_details inner join transactions on transactions.transaction_id = transaction_details.transaction_id where (transaction_details.visibility_tag = '"+VisibilityTag.VISIBILITY_OFFERS.name()+"' or transaction_details.visibility_tag = '"+VisibilityTag.VISIBILITY_STORE_OFFER.name()+"') and "+whereClause+" and transaction_details.sku_quantity > 0");
            result = rawResults.getFirstResult();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(result == null || result[0] == null)
            money[1] = 0d;
        else
            money[1] = Double.parseDouble(result[0]);

        return money;
    }

    @Override
    protected void onPostExecute(Double[] result) {
        if (result != null) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }

}

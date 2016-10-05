package com.snapbizz.snaptoolkit.asynctasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Invoice;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncInvoiceTask extends AsyncTask<String, Void, List<Invoice>> {
    
    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "Unable to retrieve invoice.";

    public GetSyncInvoiceTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }
    
    @Override
    protected List<Invoice> doInBackground(String... timestamp) {
        // TODO Auto-generated method stub
        try {
            List<Invoice> invoiceList = SnapCommonUtils.getSyncDatabaseHelper(context).getInvoiceDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(timestamp[0])).query();
            for (Invoice invoice : invoiceList) {
                if (null != invoice.getCustomer()) {
                    invoice.setCustomerId(invoice.getCustomer().getCustomerId());
                }
            }
            return invoiceList;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    protected void onPostExecute(List<Invoice> result) {
        // TODO Auto-generated method stub
        if (result != null && result.size() > 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }

}

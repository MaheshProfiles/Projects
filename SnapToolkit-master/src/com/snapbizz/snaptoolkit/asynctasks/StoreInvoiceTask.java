package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.Invoice;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class StoreInvoiceTask extends AsyncTask<List<Invoice>, Void, Boolean> {

    
    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "Unable to store records.";
    private SnapBizzDatabaseHelper databaseHelper;

    public StoreInvoiceTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected Boolean doInBackground(List<Invoice>... invoiceList) {
        try {
            if(invoiceList[0] == null)
                return false;
            databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            int invoiceId = 0;
            for(Invoice invoice : invoiceList[0]) {
                Customer customer = new Customer();
                customer.setCustomerId(invoice.getCustomerId());
                invoice.setCustomer(customer);
                databaseHelper.getInvoiceDao().create(invoice);
                invoiceId = invoice.getInvoiceID();
            }
            String timestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            SnapSharedUtils.storeLastInvoiceRetrievalTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
            SnapSharedUtils.storeLastInvoiceID(SnapCommonUtils.getSnapContext(context), invoiceId);
            return true;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        if (result) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}

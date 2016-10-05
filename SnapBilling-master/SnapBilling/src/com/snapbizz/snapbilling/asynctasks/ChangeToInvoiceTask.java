package com.snapbizz.snapbilling.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Invoice;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.TransactionType;

public class ChangeToInvoiceTask extends AsyncTask<Transaction, Void, Boolean>{

    private Context context;
    private OnQueryCompleteListener onQueryCompleteListener;
    private int taskCode;
    private String errorMessage = "Unable to modify the record to invoice";
    
    public ChangeToInvoiceTask (Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }
    
    @Override
    protected Boolean doInBackground(Transaction... transactions) {
        try {
            Transaction transaction = SnapBillingUtils.getHelper(context).getTransactionDao().queryBuilder().where().eq("transaction_id", transactions[0].getTransactionId()).queryForFirst();
            transaction.setIsInvoice(true);
            transaction.setTransactionType(TransactionType.BILL);
            Calendar calendar = Calendar.getInstance();
            transaction.setTransactionTimeStamp((new SimpleDateFormat(SnapBillingConstants.STANDARD_DATEFORMAT, Locale.getDefault())).format(calendar.getTime()));
            SnapBillingUtils.getHelper(context).getTransactionDao().update(transaction);
            // TODO: Check this
            /*Invoice invoice = new Invoice(transaction.getTotalAmount(), transaction.getTotalDiscount(),
            							  transaction.getTotalSavings(), transaction.getTotalQty(), transaction.isPaid(),
            							  transaction.getPendingPayment(), transaction.getVAT(),
            							  transaction.getTransactionType(), transaction.getCustomer(),
            							  transaction.getTransactionId(), transaction.getTransactionTimeStamp(), SaveTrasnsactionTask.getNextTransactionId(context, true));
            SnapBillingUtils.getHelper(context).getInvoiceDao().create(invoice);
            SnapSharedUtils.storeLastInvoiceID(SnapCommonUtils.getSnapContext(context), invoice.getInvoiceID());*/
            return true;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}

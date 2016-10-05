package com.snapbizz.snapbilling.asynctasks;

import java.util.Calendar;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.CustomerPayment;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.PaymentType;

public class CreateCustomerPaymentTask extends AsyncTask<CustomerPayment, Void, CustomerPayment> {
    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "Could not create customer payment";
    private boolean isLastPayment;

    public CreateCustomerPaymentTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    public CreateCustomerPaymentTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode, boolean isLastPayment) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.isLastPayment = isLastPayment;
    }

    @Override
    protected CustomerPayment doInBackground(CustomerPayment... customerPayment) {
        try {
            Log.d("","is last "+isLastPayment);
            if(customerPayment[0].getCustomerPaymentId() == 0) {
                customerPayment[0].setPaymentDate(Calendar.getInstance().getTime());
                customerPayment[0].getCustomer().setLastPaymentAmount(customerPayment[0].getPaymentAmount());
                customerPayment[0].getCustomer().setLastPaymentDate(Calendar.getInstance().getTime());
            } else if(isLastPayment) {
                customerPayment[0].getCustomer().setLastPaymentAmount(customerPayment[0].getPaymentAmount());
            }
            customerPayment[0].setPaymentType(PaymentType.CASH);
            SnapBillingUtils.getHelper(context).getCustomerPaymentDao().createOrUpdate(customerPayment[0]);
            customerPayment[0].getCustomer().setAmountDue(customerPayment[0].getCustomer().getAmountDue() - customerPayment[0].getPaymentAmount());
            customerPayment[0].getCustomer().setAmountPaid(customerPayment[0].getCustomer().getAmountPaid() + customerPayment[0].getPaymentAmount());
            SnapBillingUtils.getHelper(context).getCustomerDao().update(customerPayment[0].getCustomer());
            return customerPayment[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(CustomerPayment result) {
        if (result != null) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }

}

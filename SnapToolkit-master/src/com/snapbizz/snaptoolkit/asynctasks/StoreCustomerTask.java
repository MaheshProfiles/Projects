package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class StoreCustomerTask  extends AsyncTask<List<Customer>, Void, Boolean>{

    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "Unable to store records.";
    private SnapBizzDatabaseHelper databaseHelper;

    public StoreCustomerTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected Boolean doInBackground(List<Customer>... customerList) {
        if(customerList[0] == null)
            return false;
        databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
        for(Customer customer : customerList[0]) {
            try {
                databaseHelper.getCustomerDao().create(customer);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        String timestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
        SnapSharedUtils.storeLastCustomerSyncTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
        return true;
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

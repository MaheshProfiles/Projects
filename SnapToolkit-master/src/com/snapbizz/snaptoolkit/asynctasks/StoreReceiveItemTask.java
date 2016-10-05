package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ReceiveItems;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class StoreReceiveItemTask extends
        AsyncTask<List<ReceiveItems>, Void, Boolean> {

    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private String errorMessage = "Unable to store data";
    private SnapBizzDatabaseHelper databaseHelper;

    public StoreReceiveItemTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected Boolean doInBackground(List<ReceiveItems>... receiveItemList) {
        // TODO Auto-generated method stub
        if (null == receiveItemList[0]) {
            return false;
        }
        databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
        try {
            for (ReceiveItems receiveItems : receiveItemList[0]) {
                ProductSku productSku = new ProductSku();
                productSku.setProductSkuCode(receiveItems.getProductSkuCode());
                receiveItems.setProductSkuID(productSku);
                databaseHelper.getReceiveItemsDao().create(receiveItems);
            }
            String timestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            SnapSharedUtils.storeLastReceiveItemRetrievalTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
            return true;
        } catch (Exception e) {
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

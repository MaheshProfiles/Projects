package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.ReceiveItems;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncReceiveItemTask extends AsyncTask<String, Void, List<ReceiveItems>>{
    
    private Context context;
    private OnQueryCompleteListener onQueryCompleteListener;
    private int taskCode;
    private String errorMessage = "Unable to retrieve receive items";
    
    public GetSyncReceiveItemTask (Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.taskCode = taskCode;
        this.onQueryCompleteListener = onQueryCompleteListener;
    }
    
    @Override
    protected List<ReceiveItems> doInBackground(String... timestamp) {
        // TODO Auto-generated method stub
        try {
            List<ReceiveItems> receiveItems =  SnapCommonUtils.getSyncDatabaseHelper(context).getReceiveItemsDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(timestamp[0])).query();
            for (ReceiveItems object : receiveItems) {
                if (object.getProductSkuID() != null) {
                    object.setProductSkuCode(object.getProductSkuID().getProductSkuCode());
                } else {
                    //need the else code here
                }
            }
            return receiveItems;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<ReceiveItems> result) {
        // TODO Auto-generated method stub
        if (null != result && result.size() > 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}

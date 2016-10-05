package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetSelectedInvSkuTask  extends
AsyncTask<String, Void, InventorySku> {

    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "";
    private Distributor distributor;

    public GetSelectedInvSkuTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }
   
    @Override
    protected InventorySku doInBackground(String... skuId) {
        // TODO Auto-generated method stub
        try {
            SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            return databaseHelper.getInventorySkuDao().queryBuilder().where().eq("inventory_sku_id", skuId[0]).queryForFirst();
        } catch (SQLException e) {  
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(InventorySku result) {
        // TODO Auto-generated method stub
        if(result != null ) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
    }
}
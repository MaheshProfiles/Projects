package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetDistributorProductMapByDistributorTask extends AsyncTask<Integer, Void, HashMap<String, DistributorProductMap>>{

    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private String errorMessage = "No products found";
    
    public GetDistributorProductMapByDistributorTask(Context context, int taskCode, OnQueryCompleteListener onQueryCompleteListener) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }
    
    @Override
    protected HashMap<String, DistributorProductMap> doInBackground(Integer... params) {
        // TODO Auto-generated method stub
        try {
        	HashMap<String, DistributorProductMap> distributorProductHashMap = new HashMap<String, DistributorProductMap>();
        	for (DistributorProductMap object : (List<DistributorProductMap>) SnapInventoryUtils.getDatabaseHelper(context).getDistributorProductMapDao().queryBuilder().where().eq("distributor_id", params[0]).query()) {
                distributorProductHashMap.put(object.getDistributorProductSku().getProductSkuCode(), object);
            }
        	
            return distributorProductHashMap;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    protected void onPostExecute(HashMap<String, DistributorProductMap> result) {
        // TODO Auto-generated method stub
        if (null != result && result.size()>0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}

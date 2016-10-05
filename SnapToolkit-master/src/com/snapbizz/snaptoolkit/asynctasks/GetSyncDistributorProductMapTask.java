package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncDistributorProductMapTask extends AsyncTask<String, Void, List<DistributorProductMap>>{

    private Context context;
    private OnQueryCompleteListener onQueryCompleteListener;
    private int taskCode;
    private String errorMessage = "unable to get distributor product";
    
    public GetSyncDistributorProductMapTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }
    
    @Override
    protected List<DistributorProductMap> doInBackground(String... timestamp) {
        // TODO Auto-generated method stub
        Log.d("GetSyncDistributorProductMap", "timestamp " + timestamp[0]);
        try {
            List<DistributorProductMap> distributorProductList = SnapCommonUtils.getDatabaseHelper(context).getDistributorProductMapDao().queryBuilder().where().rawComparison("lastmodified_timestamp", ">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(timestamp[0])).query();
            for (DistributorProductMap distributorProductMap : distributorProductList) {
                if (null != distributorProductMap.getDistributor()) {
                    distributorProductMap.setDistributorId(distributorProductMap.getDistributor().getDistributorId());
                }
                if (null != distributorProductMap.getDistributorProductSku()) {
                    distributorProductMap.setProductSkuId(distributorProductMap.getDistributorProductSku().getProductSkuCode());
                } else {
                    distributorProductMap.setProductSkuId(SnapCommonUtils.getDatabaseHelper(context).getDistributorProductMapDao().queryRaw("SELECT sku_id FROM distributor_product_map WHERE distributor_product_slno = '" + distributorProductMap.getDistributorProductSkuId() + "'").getFirstResult()[0]);
                }
            }
            return distributorProductList;
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
    protected void onPostExecute(List<DistributorProductMap> result) {
        // TODO Auto-generated method stub
        if (null != result && result.size()>0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}

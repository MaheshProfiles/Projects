package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import android.content.Context;
import android.os.AsyncTask;

public class StoreDistributorProductMapTask extends
        AsyncTask<List<DistributorProductMap>, Void, Boolean> {

    private Context context;
    private int taskCode;
    private String errorMessage = "Unable to store distributor product map";
    private OnQueryCompleteListener onQueryCompleteListener;
    private SnapBizzDatabaseHelper databaseHelper;

    public StoreDistributorProductMapTask(Context context, int taskCode,
            OnQueryCompleteListener onQueryCompleteListener) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected Boolean doInBackground(List<DistributorProductMap>... params) {
        // TODO Auto-generated method stub
        if (null == params[0]) {
            return false;
        }
        try {
            databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            DistributorProductMap distributorProductMap;
            for (DistributorProductMap object : params[0]) {
                Distributor distributor = new Distributor();
                distributor.setDistributorId(object.getDistributorId());
                ProductSku productSku = new ProductSku();
                productSku.setProductSkuCode(object.getProductSkuId());
                distributorProductMap = new DistributorProductMap();
                distributorProductMap.setDistributor(distributor);
                distributorProductMap.setDistributorProductSku(productSku);
                databaseHelper.getDistributorProductMapDao().create(distributorProductMap);
            }
            SnapSharedUtils.storeLastDistributorProductMapTimestamp(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime()), SnapCommonUtils.getSnapContext(context));
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
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

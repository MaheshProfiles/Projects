package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetSubCategoriesBrandsTask extends
AsyncTask<Integer, Void, List<Brand>> {

    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "";
    private Distributor distributor;

    public GetSubCategoriesBrandsTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    public GetSubCategoriesBrandsTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode, Distributor distributor) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.distributor = distributor;
    }

    @Override
    protected List<Brand> doInBackground(Integer... subcategoryId) {
        // TODO Auto-generated method stub
        try {
            SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            GenericRawResults<Brand> rawResults;
            List<Brand> brand = databaseHelper.getBrandDao().queryForEq("brand_id", 1302);
            if(subcategoryId.length > 0) {
                if(distributor != null) {
                    rawResults = databaseHelper.getBrandDao().queryRaw("select brand.* from brand inner join product_sku on brand.brand_id = product_sku.brand_id inner join distributor_brand_map on brand.brand_id = distributor_brand_map.brand_id where product_sku.sku_subcategory_id = "+subcategoryId[0]+" and brand.is_mystore = 1 and distributor_brand_map.distributor_id = "+distributor.getDistributorId()+" group by brand.brand_id ", databaseHelper.getBrandDao().getRawRowMapper());
                } else
                    rawResults = databaseHelper.getBrandDao().queryRaw("select brand.* from brand inner join product_sku on brand.brand_id = product_sku.brand_id where product_sku.sku_subcategory_id = "+subcategoryId[0]+" and brand.is_mystore = 1 group by brand.brand_id ", databaseHelper.getBrandDao().getRawRowMapper());
            } else {
                if(distributor != null) {
                    rawResults = databaseHelper.getBrandDao().queryRaw("select brand.* from brand inner join distributor_brand_map on brand.brand_id = distributor_brand_map.brand_id where brand.is_mystore = 1 and distributor_brand_map.distributor_id = '"+distributor.getDistributorId()+"' group by brand.brand_id ", databaseHelper.getBrandDao().getRawRowMapper());
                } else
                    rawResults = databaseHelper.getBrandDao().queryRaw("select brand.* from brand where brand.is_mystore = 1", databaseHelper.getBrandDao().getRawRowMapper());
            }
            return rawResults.getResults();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Brand> result) {
        // TODO Auto-generated method stub
        if(result != null && result.size() > 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
    }

}
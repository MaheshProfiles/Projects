package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class StoreDistributorBrandMapTask extends AsyncTask<List<DistributorBrandMap>, Void, Boolean> {

    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "Unable to store distributor brand records.";
    private SnapBizzDatabaseHelper databaseHelper;

    public StoreDistributorBrandMapTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected Boolean doInBackground(List<DistributorBrandMap>... distributorBrandMapList) {
        try {
            if(distributorBrandMapList[0] == null)
                return false;
            databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            for(DistributorBrandMap distributorBrandMap : distributorBrandMapList[0]) {
                Distributor distributor = new Distributor();
                distributor.setDistributorId(distributorBrandMap
                        .getDistributorId());
                distributorBrandMap.setDistributor(distributor);
                Brand brand = new Brand();
                brand.setBrandId(distributorBrandMap.getBrandId());
                distributorBrandMap.setDistributorBrand(brand);
                databaseHelper.getDistributorBrandMapDao().create(distributorBrandMap);
                Company company = databaseHelper.getBrandDao().queryForEq("brand_id", brand.getBrandId()).get(0).getCompany();
                if(databaseHelper.getCompanyDistributorDao().queryBuilder().where().eq("distributor_id", distributorBrandMap.getDistributor().getDistributorId()).and().eq("company_id", company.getCompanyId()).countOf() == 0) {
                    CompanyDistributorMap companyDistributorMap = new CompanyDistributorMap();
                    companyDistributorMap.setDistributor(distributor);
                    companyDistributorMap.setCompany(company);
                    databaseHelper.getCompanyDistributorDao().create(companyDistributorMap);
                }
            }
            //            List<DistributorBrandMap> distributorBrandList = databaseHelper.getDistributorBrandMapDao().queryForAll();
            //            for(DistributorBrandMap distributorBrandMap : distributorBrandList) {
            //                Log.d("", "got distributor brand "+distributorBrandMap.getDistributorBrand().getBrandId()+ " dist " +distributorBrandMap.getDistributor().getDistributorId());
            //            }
            //            List<CompanyDistributorMap> companyDistributorMap = databaseHelper.getCompanyDistributorDao().queryForAll();
            //            for(DistributorBrandMap distributorBrandMap : distributorBrandList) {
            //                Log.d("", "got distributor company "+distributorBrandMap.getDistributorBrand().getBrandId()+ " dist " +distributorBrandMap.getDistributor().getDistributorId());
            //            }
            String timestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
            SnapSharedUtils.storeLastDistributorBranMapSyncTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
            return true;
        } catch (java.sql.SQLException e) {
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

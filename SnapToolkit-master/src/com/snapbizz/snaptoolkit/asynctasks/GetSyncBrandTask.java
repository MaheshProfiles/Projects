package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncBrandTask extends AsyncTask<String, Void, List<Brand>> {
    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "Unable to retrieve brands.";

    public GetSyncBrandTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode){
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }
    @Override
    protected List<Brand> doInBackground(String... timestamp) {
        // TODO Auto-generated method stub
        try {
            List<Brand> brandList = SnapCommonUtils.getSyncDatabaseHelper(context).getBrandDao().queryBuilder().limit(100).where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0])).query();
            for(Brand brand : brandList) {
                if(brand.getCompany() != null)
                    brand.setCompnayId(brand.getCompany().getCompanyId());
            }
            return brandList;
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
    protected void onPostExecute(List<Brand> result) {
        // TODO Auto-generated method stub
        if (result != null && result.size() > 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}

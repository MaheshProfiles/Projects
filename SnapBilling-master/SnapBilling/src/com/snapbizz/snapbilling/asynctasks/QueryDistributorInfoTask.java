package com.snapbizz.snapbilling.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class QueryDistributorInfoTask extends AsyncTask<String, Void, List<Distributor>> {
    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private String errorMessage = "No Distributor Found";

    public QueryDistributorInfoTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected List<Distributor> doInBackground(String... params) {
        try {
        	if(!params[0].equals(""))
                return SnapBillingUtils.getHelper(context).getDistributorDao().queryBuilder().where()
                        .like("phone_number", "%" + params[0] + "%").or().like("agency_name", "%" + params[0] + "%").or().like("address", "%" + params[0] + "%").query();
            else
            	return SnapBillingUtils.getHelper(context).getDistributorDao().queryBuilder().orderBy("agency_name", true).query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Distributor> result) {
        if (result != null && result.size() > 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
        onQueryCompleteListener = null;
        context = null;
    }

}

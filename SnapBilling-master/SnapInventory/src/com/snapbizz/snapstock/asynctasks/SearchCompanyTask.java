package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class SearchCompanyTask extends AsyncTask<String, Void, List<Company>> {

    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "No Product Found";
    private Context context;

    public SearchCompanyTask( Context context,
            OnQueryCompleteListener onQueryCompleteListener,int taskCode) {
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.context = context;
    }

    @Override
    protected List<Company> doInBackground(String... searchTerm) {
        try{
            return SnapCommonUtils.getDatabaseHelper(context).getCompanyDao().queryBuilder().where().like("company_name", "%"+searchTerm[0]+"%").query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Company> result) {
        // TODO Auto-generated method stub
        if (null != result && result.size()>0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}

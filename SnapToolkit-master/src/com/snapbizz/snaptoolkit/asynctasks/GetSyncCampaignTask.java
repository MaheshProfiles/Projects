package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncCampaignTask extends AsyncTask<String, Void, List<Campaigns>> {
    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "Unable to retrieve brands.";

    public GetSyncCampaignTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode){
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }
    @Override
    protected List<Campaigns> doInBackground(String... timestamp) {
        // TODO Auto-generated method stub
        try {
//            List<Campaigns> campList = SnapCommonUtils.getSyncDatabaseHelper(context).getCampaignsDao().queryBuilder().where().rawComparison("end_date","<=", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0])).query();
        	return SnapCommonUtils.getSyncDatabaseHelper(context).getCampaignsDao().queryBuilder().query();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } 
    }
    @Override
    protected void onPostExecute(List<Campaigns> result) {
        // TODO Auto-generated method stub
        if (result != null && result.size() > 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}

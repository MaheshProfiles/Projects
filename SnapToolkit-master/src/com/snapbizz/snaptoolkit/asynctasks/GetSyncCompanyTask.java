package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncCompanyTask  extends AsyncTask<String, Void, List<Company>>{

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to retrieve company.";
	
	public GetSyncCompanyTask(Context context,
	OnQueryCompleteListener onQueryCompleteListener, int taskCode){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	@Override
	protected List<Company> doInBackground(String... timestamp) {
		// TODO Auto-generated method stub
		try {
		    Log.d("", "company timestamp "+timestamp[0]);
			return SnapCommonUtils.getSyncDatabaseHelper(context).getCompanyDao().queryBuilder().limit(100).where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0])).and().eq("is_gdb", false).query();
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
	protected void onPostExecute(List<Company> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}

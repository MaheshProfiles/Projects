package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class SearchCompanyFilterTask extends
		AsyncTask<String, Void, List<Company>> {

	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Company Found";
	private Context context;
	private String queryString;

	public SearchCompanyFilterTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.context = context;
		queryString = "Select ";
	}

	@Override
	protected List<Company> doInBackground(String... searchTerm) {
		// TODO Auto-generated method stub
		try {
			Log.d("query", SnapInventoryUtils.getDatabaseHelper(context).getCompanyDao()
					.queryBuilder().where().like("company_name", searchTerm[0]+"%").getStatement());
		return SnapInventoryUtils.getDatabaseHelper(context).getCompanyDao()
					.queryBuilder().orderBy("company_name", true).where().like("company_name", searchTerm[0]+"%")
					.query();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(List<Company> result) {
		// TODO Auto-generated method stub
		if (null != result && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}

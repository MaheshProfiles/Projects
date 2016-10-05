package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetDistributorsCompanyTask extends AsyncTask<Integer, Void, List<Distributor>>{

	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String ERROR_MESSAGE = "Unable to get companies";
	private int taskCode;

	public GetDistributorsCompanyTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
	}
	
	@Override
	protected List<Distributor> doInBackground(Integer... params) {
		try {
			QueryBuilder<CompanyDistributorMap, Integer> queryBuilderCompanyDistributorMap = SnapInventoryUtils
					.getDatabaseHelper(context).getCompanyDistributorDao()
					.queryBuilder();
			queryBuilderCompanyDistributorMap.where().eq("company_id", params[0]);
			QueryBuilder<Distributor, Integer> queryBuilderDistributor = SnapInventoryUtils
					.getDatabaseHelper(context).getDistributorDao().queryBuilder();
			queryBuilderDistributor.orderBy("agency_name", true);
			return queryBuilderDistributor.join(queryBuilderCompanyDistributorMap).query();
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected void onPostExecute(List<Distributor> result) {
		// TODO Auto-generated method stub
		if (null != result && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(ERROR_MESSAGE, taskCode);
		}
	}
}

package com.snapbizz.snapstock.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetCompanyofProductTask extends AsyncTask<Integer, Void, Company>{
	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String ERROR_MESSAGE = "No Companies found";
	private int taskCode;

	public GetCompanyofProductTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
	}
	
	@Override
	protected Company doInBackground(Integer... params) {
		try {
			
		    Company comp= SnapCommonUtils.getDatabaseHelper(context).getBrandDao().queryForEq("brand_id", params[0]).get(0).getCompany();
		    SnapCommonUtils.getDatabaseHelper(context).getCompanyDao().refresh(comp);
		    return comp;
		    /*QueryBuilder<Brand, Integer> queryBuilderBrand = SnapInventoryUtils
					.getDatabaseHelper(context).getBrandDao().queryBuilder();
			queryBuilderBrand.where().like("brand_name",brandName+"%");
			QueryBuilder<CompanyBrandMap, Integer> queryBuilderCompanyBrandMap = SnapInventoryUtils
					.getDatabaseHelper(context).getCompanyBrandMapDao()
					.queryBuilder();
			queryBuilderBrand.join(queryBuilderCompanyBrandMap);
			if(brandName!=null && !brandName.equals("")){
			return queryBuilderCompany.join(queryBuilderBrand).query();
			}
			else{*/
		//	}
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected void onPostExecute(Company result) {
		// TODO Auto-generated method stub
		if (null != result ) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(ERROR_MESSAGE, taskCode);
		}
	}
}

package com.snapbizz.snapstock.asynctasks;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetCompaniesTaggedToDistributorTask extends AsyncTask<Integer, Void, HashMap<Integer, Company>>{
	
	private Context context;
	private int taskCode;
	private String errorMessage = "No companie(s) found";
	private OnQueryCompleteListener onQueryCompleteListener;
	
	public GetCompaniesTaggedToDistributorTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected HashMap<Integer, Company> doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		try{
			
			QueryBuilder<CompanyDistributorMap, Integer> DistributorCompanyQB = SnapInventoryUtils.getDatabaseHelper(context).getCompanyDistributorDao().queryBuilder();
			DistributorCompanyQB.where().eq("distributor_id", params[0]);
			
			QueryBuilder<Company, Integer> CompanyQB = SnapInventoryUtils.getDatabaseHelper(context).getCompanyDao().queryBuilder();
			CompanyQB.orderBy("company_name", true);
			HashMap<Integer, Company> companyMap = new HashMap<Integer, Company>();
			for (Company company : (List<Company>) CompanyQB.join(DistributorCompanyQB).query()) {
				companyMap.put(company.getCompanyId(), company);
			}
			
			return companyMap;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(HashMap<Integer, Company> result) {
		// TODO Auto-generated method stub
		if(null != result && result.size()>0){
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		}else{
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}

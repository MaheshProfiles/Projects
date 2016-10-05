package com.snapbizz.snapstock.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class CreateOrUpdateCompanyTask extends AsyncTask<Company, Void, Company>{

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Could not create company";
	private Distributor taggedDistributor;
	
	public CreateOrUpdateCompanyTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	public CreateOrUpdateCompanyTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,Distributor taggedDistributor) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.taggedDistributor=taggedDistributor;
	}
	@Override
	protected Company doInBackground(Company... company) {
		try{
			SnapBizzDatabaseHelper databaseHelper = SnapInventoryUtils
					.getDatabaseHelper(context);

			databaseHelper.getCompanyDao().createOrUpdate(company[0]);
		/*	if(taggedDistributor!=null){
				Dao<CompanyDistributorMap, Integer> distributorCompanyMapDao = databaseHelper.getCompanyDistributorDao();
				CompanyDistributorMap distributorCompanyMap = new CompanyDistributorMap();
				distributorCompanyMap.setDistributor(taggedDistributor);
				distributorCompanyMap.setCompany(company[0]);
				distributorCompanyMapDao.create(distributorCompanyMap);
			}*/
			return company[0];
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Company result) {
		// TODO Auto-generated method stub
		if (result!=null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}

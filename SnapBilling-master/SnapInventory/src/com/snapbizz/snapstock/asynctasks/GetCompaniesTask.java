package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetCompaniesTask extends AsyncTask<Void, Void, List<Company>> {

	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String ERROR_MESSAGE = "Unable to get companies";
	private int taskCode;
	private ProgressDialog progressDialog;

	public GetCompaniesTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.progressDialog = new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		progressDialog.show();
	}
	
	@Override
	protected List<Company> doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try{
			List<Company> companyList = SnapInventoryUtils.getDatabaseHelper(context).getCompanyDao().queryBuilder().orderBy("company_name", true).query();
			List<Brand> companyBrandMapList = SnapInventoryUtils.getDatabaseHelper(context).getBrandDao().queryForAll();
			for(Brand brand : companyBrandMapList) {
				for(Company company : companyList) {
					if(company.getCompanyId() == brand.getCompany().getCompanyId()) {
						if(company.getCompanyBrandData() != null)
							company.setCompanyBrandData(company.getCompanyBrandData()+", "+brand.getBrandName());
						else
							company.setCompanyBrandData(brand.getBrandName());
					}
				}
			}
			return companyList;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(List<Company> result) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
		if (null != result && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(ERROR_MESSAGE, taskCode);
		}
	}
}

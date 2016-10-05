package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.Dao;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class CreateDistributorTask extends
		AsyncTask<Distributor, Void, Distributor> {

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Could not create distributor";
	private List<Brand> distributorBrandList;
	private List<Company> companyDistributorList;
	private ProgressDialog progressDialog;

	public CreateDistributorTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,
			List<Brand> distributorBrandList,List<Company> companyDistributorList) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.distributorBrandList = distributorBrandList;
		this.companyDistributorList = companyDistributorList;
		progressDialog = new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute() {
	    // TODO Auto-generated method stub
	    super.onPreExecute();
	    progressDialog.show();
	}
	
	@Override
	protected Distributor doInBackground(Distributor... distributors) {
		try {
			SnapBizzDatabaseHelper databaseHelper = SnapInventoryUtils
					.getDatabaseHelper(context);
			Dao<Distributor, Integer> distributorDao = databaseHelper.getDistributorDao();
			//Dao<DistributorBrandMap, Integer> distributorBrandMapDao = databaseHelper.getDistributorBrandMapDao();
			//Dao<CompanyDistributorMap, Integer> distributorCompanyMapDao = databaseHelper.getCompanyDistributorDao();
			distributorDao.createOrUpdate(distributors[0]);
//			Log.d("","dist "+distributors[0].getAgencyName());
//			DeleteBuilder<DistributorBrandMap, Integer> deleteBuilder =distributorBrandMapDao.deleteBuilder();
//			deleteBuilder.where().eq("distributor_id", distributors[0].getDistributorId());
//		    deleteBuilder.delete();
//			for (Brand brand : distributorBrandList) {
//				DistributorBrandMap distributorBrandMap = new DistributorBrandMap();
//				distributorBrandMap.setDistributor(distributors[0]);
//				distributorBrandMap.setDistributorBrand(brand);
//				/*	HashMap<String, Object> distBrandMap=new HashMap<String, Object>();
//				distBrandMap.put("distributor_id", distributors[0].getDistributorId());
//				distBrandMap.put("brand_id",brand.getBrandId());
//				List<DistributorBrandMap> resultList = databaseHelper.getDistributorBrandMapDao().queryForFieldValues(distBrandMap);
//				if(resultList.size()==0)*/
//				distributorBrandMapDao.create(distributorBrandMap);
//				/*else
//				databaseHelper.getDistributorBrandMapDao().update(distributorBrandMap);*/
//			}
//			DeleteBuilder<CompanyDistributorMap, Integer> deleteCompanyBuilder =distributorCompanyMapDao.deleteBuilder();
//			deleteCompanyBuilder.where().eq("distributor_id", distributors[0].getDistributorId());
//			deleteCompanyBuilder.delete();
//			for (Company company : companyDistributorList) {
//				CompanyDistributorMap distributorCompanyMap = new CompanyDistributorMap();
//				distributorCompanyMap.setDistributor(distributors[0]);
//				distributorCompanyMap.setCompany(company);
//				/*HashMap<String, Object> compBrandMap=new HashMap<String, Object>();
//				compBrandMap.put("distributor_id", distributors[0].getDistributorId());
//				compBrandMap.put("company_id",company.getCompanyId());*/
//				/*List<CompanyDistributorMap> resultList = distributorCompanyMapDao.queryForFieldValues(compBrandMap);
//				if(resultList.size()==0)*/
//					distributorCompanyMapDao.create(distributorCompanyMap);
//				/*else
//					distributorCompanyMapDao.update(distributorCompanyMap);*/
//			}
			return distributors[0];
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(Distributor result) {
	    progressDialog.dismiss();
		if (result != null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}

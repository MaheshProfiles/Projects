package com.snapbizz.snapstock.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;


public class CreateOrUpdateBrandTask extends AsyncTask<Brand, Void, Brand> {
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Could not create company";
	private Distributor taggedDistributor;

	public CreateOrUpdateBrandTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	public CreateOrUpdateBrandTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,Distributor taggedDistributor) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.taggedDistributor=taggedDistributor;

	}

	@Override
	protected Brand doInBackground(Brand... brand) {
		try {
			SnapBizzDatabaseHelper databaseHelper = SnapInventoryUtils
					.getDatabaseHelper(context);
			databaseHelper.getBrandDao().createOrUpdate(brand[0]);
			/*if(taggedDistributor!=null){
				Dao<DistributorBrandMap, Integer> distributorBrandMapDao = databaseHelper.getDistributorBrandMapDao();
				DistributorBrandMap distributorBrandMap = new DistributorBrandMap();
				distributorBrandMap.setDistributor(taggedDistributor);
				distributorBrandMap.setDistributorBrand(brand[0]);
				distributorBrandMapDao.create(distributorBrandMap);
			}*/
			return brand[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Brand result) {
		// TODO Auto-generated method stub
		if (result != null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}

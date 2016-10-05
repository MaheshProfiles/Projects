package com.snapbizz.snaptoolkit.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class StoreBrandsTask extends AsyncTask<List<Brand>, Void, Boolean> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to store records.";
	private SnapBizzDatabaseHelper databaseHelper;
	private String timestamp;
	
	public StoreBrandsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, String timestamp, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.timestamp = timestamp;
	}

	@Override
	protected Boolean doInBackground(List<Brand>... brandList) {
		// TODO Auto-generated method stub
		try {
			if(brandList[0] == null)
				return false;
			databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
			for(Brand brand : brandList[0]) {
			    brand.setHasImage(SnapCommonUtils.hasBrandDrawable(brand, context));
			    Company company = new Company();
			    company.setCompanyId(brand.getCompnayId());
			    brand.setCompany(company);
			    List<Brand> brands = databaseHelper.getBrandDao().queryForEq("brand_id", brand.getBrandId());
			    if(brands.size() > 0) {
			        brand.setLastModifiedTimestamp(brands.get(0).getLastModifiedTimestamp());
			        databaseHelper.getBrandDao().update(brand);
			    } else
			        databaseHelper.getBrandDao().create(brand);
			}
            SnapSharedUtils.storeLastBrandRetrievalTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
			Log.d("Result", result+"");
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
	
}

package com.snapbizz.snapstock.asynctasks;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetBrandsByDistributorTask extends AsyncTask<Integer, Void, HashMap<Integer, Brand>>{
	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String ERROR_MESSAGE = "Unable to get brands";
	private int taskCode;

	public GetBrandsByDistributorTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
	}

	@Override
	protected HashMap<Integer, Brand> doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		try{
			 QueryBuilder<DistributorBrandMap, Integer> brandDistributorQueryBuilder = SnapInventoryUtils.getDatabaseHelper(context).getDistributorBrandMapDao().queryBuilder();
			 brandDistributorQueryBuilder.where().eq("distributor_id", params[0]);
			 
			 QueryBuilder<Brand, Integer> brandQueryBuilder = SnapInventoryUtils.getDatabaseHelper(context).getBrandDao().queryBuilder();
			 brandQueryBuilder.orderBy("brand_name", true);
			 HashMap<Integer, Brand> brandMap = new HashMap<Integer, Brand>();
			 for (Brand brand : (List<Brand>) brandQueryBuilder.join(brandDistributorQueryBuilder).query()) {
					brandMap.put(brand.getBrandId(), brand);
			 }
			 
			 return brandMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(HashMap<Integer, Brand> result) {
		// TODO Auto-generated method stub
		if (null != result && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(ERROR_MESSAGE, taskCode);
		}
	}
	
}

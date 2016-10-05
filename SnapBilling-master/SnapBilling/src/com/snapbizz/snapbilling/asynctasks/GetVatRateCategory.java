package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.VAT;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetVatRateCategory extends AsyncTask<String, Void, ArrayList<List<VAT>>>{

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to populate vat details";
	private ProgressDialog pd;
	private String stateId;
	private VAT mainVat=null;
	 ArrayList<List<VAT>> vatSizeList;
	private List<VAT> vatList;
	private List<ProductCategory> productSkuSubcategoryId;

	public GetVatRateCategory(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,
			String stateId,List<ProductCategory> productSkuSubcategoryId) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.stateId = stateId;
		this.productSkuSubcategoryId = productSkuSubcategoryId;
	}
	@Override
	protected ArrayList<List<VAT>> doInBackground(String... params) {
		QueryBuilder<VAT, Integer> vatQueryBuilder;
		try {
			vatSizeList= new ArrayList<List<VAT>>();
			for(int i=0;i<productSkuSubcategoryId.size();i++){
			vatQueryBuilder = SnapCommonUtils.getDatabaseHelper(context).getVatDao().queryBuilder();
			vatQueryBuilder.distinct().selectColumns("vat_value").where().eq("state_id",stateId).and().like("subcategory_id", productSkuSubcategoryId.get(i).getCategoryId());
			vatQueryBuilder.orderBy("vat_value", true);
			vatList =vatQueryBuilder.query();
			vatSizeList.add(vatList);
		}
			return vatSizeList;
		} catch (SQLException e) {
			Log.e("test", "test---->",e);
			return null;
		}
		
	}
	@Override
	protected void onPostExecute(ArrayList<List<VAT>> result) {
		if (null != result && result.size() != 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}

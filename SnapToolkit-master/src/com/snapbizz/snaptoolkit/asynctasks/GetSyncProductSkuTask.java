package com.snapbizz.snaptoolkit.asynctasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncProductSkuTask extends AsyncTask<String, Void, List<ProductSku>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to retrieve product sku.";

	public GetSyncProductSkuTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<ProductSku> doInBackground(String... timestamp) {
		// TODO Auto-generated method stub
		try {
		    Log.d("","productsku timestamp "+timestamp[0]);
		    QueryBuilder<InventorySku, Integer> inventorySkuQueryBuilder = SnapCommonUtils.getDatabaseHelper(context).getInventorySkuDao().queryBuilder(); 
			List<ProductSku> prodList = SnapCommonUtils.getSyncDatabaseHelper(context).getProductSkuDao().queryBuilder().join(inventorySkuQueryBuilder).where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0])).query();
		    Log.d("prod sku","productsku size "+prodList.size());
			return prodList;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
	}

	@Override
	protected void onPostExecute(List<ProductSku> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
	
}

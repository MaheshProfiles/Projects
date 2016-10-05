package com.snapbizz.snaptoolkit.asynctasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnSyncDateUpdatedListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetForceSyncProductSkuTask extends AsyncTask<String, Void, List<ProductSku>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private OnSyncDateUpdatedListener onSyncDateUpdatedListener;
	private final String errorMessage = "Unable to retrieve product sku.";
	private String startTimeStamp;
	private Date startDate;
	private Date endDate;
	
	public GetForceSyncProductSkuTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener,OnSyncDateUpdatedListener onSyncDateUpdatedListener, int taskCode,String startTimeStamp) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.onSyncDateUpdatedListener=onSyncDateUpdatedListener;
		this.startTimeStamp = startTimeStamp;
	}

	@Override
	protected List<ProductSku> doInBackground(String... timestamp) {
		// TODO Auto-generated method stub
		try {
		    Log.d("","productsku timestamp "+timestamp[0]);
		     startDate = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(startTimeStamp);
		     endDate =new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0]);
		    List<ProductSku> prodList = new ArrayList<ProductSku>();
		    Date currentDate = Calendar.getInstance().getTime();
		    //while(prodList.size()==0 && startDate.before(currentDate)){
		    QueryBuilder<InventorySku, Integer> inventorySkuQueryBuilder = SnapCommonUtils.getSyncDatabaseHelper(context).getInventorySkuDao().queryBuilder();
		    prodList = SnapCommonUtils.getSyncDatabaseHelper(context).getProductSkuDao().queryBuilder().orderBy("lastmodified_timestamp", true).join(inventorySkuQueryBuilder).limit(100).where().rawComparison("lastmodified_timestamp",">", startDate).query();
		    	 
		    
	    	if(prodList.size()>0){
	    		Date lastModifiedDate =prodList.get(prodList.size()-1).getLastModifiedTimestamp();
			    startDate=lastModifiedDate;
	    	}
	    	
		    
		    //startDate.setDate(startDate.getDate() + SnapToolkitConstants.CHUNK_SIZE);
            //endDate.setDate(endDate.getDate()+SnapToolkitConstants.CHUNK_SIZE);
		    Log.d("prod sku","productsku size "+prodList.size());
		    //}
		    timestamp[0]=new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(endDate);
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
			onSyncDateUpdatedListener.updateDateValues(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(startDate), new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(endDate),taskCode);
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
	
}

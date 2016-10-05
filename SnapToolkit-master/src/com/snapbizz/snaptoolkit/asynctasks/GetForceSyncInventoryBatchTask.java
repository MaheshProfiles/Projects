package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnSyncDateUpdatedListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetForceSyncInventoryBatchTask extends AsyncTask<String, Void, List<InventoryBatch>>  {
	
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to retrieve inventory batches.";
	private OnSyncDateUpdatedListener onSyncDateUpdatedListener;
	private String startTimeStamp;
	private Date startDate;
	private Date endDate;
	
	public GetForceSyncInventoryBatchTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener,OnSyncDateUpdatedListener onSyncDateUpdatedListener, int taskCode,String startTimeStamp){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.startTimeStamp = startTimeStamp;
		this.onSyncDateUpdatedListener=onSyncDateUpdatedListener;
	}
	@Override
	protected List<InventoryBatch> doInBackground(String... timestamp) {
		// TODO Auto-generated method stub
		try {
			  Log.d("", "timestamp "+timestamp[0]);
			     startDate = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(startTimeStamp);
			     endDate =new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0]);
			    List<InventoryBatch> inventoryBatchList = new ArrayList<InventoryBatch>();
			    Date currentDate = Calendar.getInstance().getTime();
			    //while(inventoryBatchList.size()==0 && startDate.before(currentDate)){
			    	
			    	inventoryBatchList=SnapCommonUtils.getSyncDatabaseHelper(context).getInventoryBatchDao().queryBuilder().orderBy("lastmodified_timestamp", true).limit(100).where().rawComparison("lastmodified_timestamp",">", startDate).query();
			    	Log.d("prod sku","inventoryBatchList size "+inventoryBatchList.size());
			    	
			    	if(inventoryBatchList.size()>0){
			    		Date lastModifiedDate =inventoryBatchList.get(inventoryBatchList.size()-1).getLastModifiedTimestamp();
					    startDate=lastModifiedDate;
			    		
			    	}
			    	
			    	
			    	//startDate.setDate(startDate.getDate() + SnapToolkitConstants.CHUNK_SIZE);
	                //endDate.setDate(endDate.getDate()+SnapToolkitConstants.CHUNK_SIZE);
			   // }
			    return inventoryBatchList;
			    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
	}

	@Override
	protected void onPostExecute(List<InventoryBatch> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() > 0) {
			onSyncDateUpdatedListener.updateDateValues(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(startDate), new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(endDate),taskCode);
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}


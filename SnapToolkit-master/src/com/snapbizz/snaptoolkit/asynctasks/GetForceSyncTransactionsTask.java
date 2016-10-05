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

import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnSyncDateUpdatedListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetForceSyncTransactionsTask extends AsyncTask<String, Void, List<Transaction>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private OnSyncDateUpdatedListener onSyncDateUpdatedListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to retrieve transactions.";
	private String startTimeStamp;
	private Date startDate;

	public GetForceSyncTransactionsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener,OnSyncDateUpdatedListener onSyncDateUpdatedListener,int taskCode,String startTimeStamp) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.startTimeStamp = startTimeStamp;
		this.onSyncDateUpdatedListener=onSyncDateUpdatedListener;
	}

	@Override
	protected synchronized List<Transaction> doInBackground(String... timestamp) {
		try {
			//wait(60000);
			Log.d("","productsku timestamp "+timestamp[0]);
		     startDate = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(startTimeStamp);
		    List<Transaction> transactionList = new ArrayList<Transaction>();
		    //while(transactionList.size()==0){
		    	transactionList = SnapCommonUtils.getSyncDatabaseHelper(context).getTransactionDao().queryBuilder().orderBy("lastmodified_timestamp", true).limit(100).where().rawComparison("lastmodified_timestamp",">", startDate).query();
		    	Log.d("prod sku","transactionList size "+transactionList.size());
		    	if(transactionList.size()>0){
		    		Date lastModifiedDate =transactionList.get(transactionList.size()-1).getLastModifiedTimestamp();
				    startDate=lastModifiedDate;
		    	}
		    	
			    Log.d("prod sku","transactionList size startDate----"+startDate);
			    
               // endDate.setDate(billDate.getDate()+SnapToolkitConstants.CHUNK_SIZE);
		    //}	
		    //Log.d("prod sku","GetForceSyncTransactionsTask Step2----startDate="+startDate);
            //Log.d("prod sku","GetForceSyncTransactionsTask Step2----endDate="+endDate);
		    return transactionList;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}

	@Override
	protected void onPostExecute(List<Transaction> result) {
		if (result != null && result.size() > 0) {
			onSyncDateUpdatedListener.updateDateValues(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(startDate), new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(startDate),taskCode);
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
	
}

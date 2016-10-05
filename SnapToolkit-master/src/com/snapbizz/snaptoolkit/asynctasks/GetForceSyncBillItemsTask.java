package com.snapbizz.snaptoolkit.asynctasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnSyncDateUpdatedListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetForceSyncBillItemsTask extends AsyncTask<String, Void, List<BillItem>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private OnSyncDateUpdatedListener onSyncDateUpdatedListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to retrieve billitems.";
	private String startTimeStamp;
	private Date startDate;
	private Date endDate;
	
	public GetForceSyncBillItemsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener,OnSyncDateUpdatedListener onSyncDateUpdatedListener,int taskCode,String startTimeStamp) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.startTimeStamp = startTimeStamp;
		this.onSyncDateUpdatedListener=onSyncDateUpdatedListener;
	}

	@Override
	protected List<BillItem> doInBackground(String... timestamp) {
		// TODO Auto-generated method stub
		try {
			Log.d("", "GetForceSyncBillItemsTask timestamp--------Before "+timestamp[0]);
			Thread.sleep(60000*3);
		    Log.d("", "GetForceSyncBillItemsTask timestamp--------After "+timestamp[0]);
		    Log.d("","productsku timestamp "+timestamp[0]);
		     startDate = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(startTimeStamp);
		     endDate =new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0]);
		    List<BillItem> billItemList = new ArrayList<BillItem>();
		    Date currentDate = Calendar.getInstance().getTime();
		    //while(billItemList.size()==0 && startDate.before(currentDate)){
		   billItemList = SnapCommonUtils.getSyncDatabaseHelper(context).getBillItemDao().queryBuilder().orderBy("lastmodified_timestamp", true).limit(100).where().rawComparison("lastmodified_timestamp",">=", startDate).query();
		   Log.d("","productsku Bill Item list creating ");
		   for(BillItem billItem : billItemList) {
			    String [] data = SnapCommonUtils.getSyncDatabaseHelper(context).getBillItemDao().queryRaw("select sku_id, transaction_id from transaction_details where transaction_detail_id = '"+billItem.getTransactionDetailId()+"'").getFirstResult();
                if(billItem.getProductSku() != null)
                    billItem.setProductSkuCode(billItem.getProductSku().getProductSkuCode());
                else
                    billItem.setProductSkuCode(data[0]);
                billItem.setTransactionId(Integer.parseInt(data[1]));
            }
			   Log.d("prod sku","billItemList size "+billItemList.size());
		    	if(billItemList.size()>0){
		    		Date lastModifiedDate =billItemList.get(billItemList.size()-1).getLastModifiedTimestamp();
				    startDate=lastModifiedDate;
		    	}
		    	
			   
		    	//startDate.setDate(startDate.getDate() + SnapToolkitConstants.CHUNK_SIZE);
				//endDate.setDate(endDate.getDate()+SnapToolkitConstants.CHUNK_SIZE);
		   // }
			return billItemList;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<BillItem> result) {
        // TODO Auto-generated method stub
        if (result != null && result.size() > 0) {
			onSyncDateUpdatedListener.updateDateValues(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(startDate), new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(endDate),taskCode);
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }

}

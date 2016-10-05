package com.snapbizz.snaptoolkit.asynctasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncBillItemsTask extends AsyncTask<String, Void, List<BillItem>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to retrieve billitems.";

	public GetSyncBillItemsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<BillItem> doInBackground(String... timestamp) {
		// TODO Auto-generated method stub
		try {
		    Log.d("", "timestamp "+timestamp[0]);
		    List<BillItem> billItemList = SnapCommonUtils.getSyncDatabaseHelper(context).getBillItemDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0])).query();
			for(BillItem billItem : billItemList) {
			    String [] data = SnapCommonUtils.getSyncDatabaseHelper(context).getBillItemDao().queryRaw("select sku_id, transaction_id from transaction_details where transaction_detail_id = '"+billItem.getTransactionDetailId()+"'").getFirstResult();
                if(billItem.getProductSku() != null)
                {  billItem.setProductSkuCode(billItem.getProductSku().getProductSkuCode());
                Log.d("billItemList","billItemList size----- "+billItemList.size());}
                else
                {   Log.d("billItemList","billItemList size------else "+billItemList.size());
                    billItem.setProductSkuCode(data[0]);
                    billItem.setTransactionId(Integer.parseInt(data[1]));
                }
            }
			return billItemList;
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
    protected void onPostExecute(List<BillItem> result) {
        // TODO Auto-generated method stub
        if (result != null && result.size() > 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }

}

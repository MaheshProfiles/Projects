package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.DeletedRecords;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.TableType;

public class DeleteDistributorTask extends AsyncTask<Distributor, Void, Boolean>{

	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private int taskCode;
	private String errorMessage = "Unable to delete Distributor";

	public DeleteDistributorTask( Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected Boolean doInBackground(Distributor... params) {
		// TODO Auto-generated method stub
		try{
			SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            List<DistributorBrandMap> distributorBrandMapList =  databaseHelper.getDistributorBrandMapDao().queryForEq("distributor_id", params[0].getDistributorId());
            for(DistributorBrandMap distributorBrandMap : distributorBrandMapList) {
                DeletedRecords deletedRecord= new DeletedRecords();
                deletedRecord.setRecordId(distributorBrandMap.getDistributorBrandId()+"");
                deletedRecord.setTableType(TableType.DISTRIBUTOR_BRAND_MAP);
                databaseHelper.getDeletedRecordsDao().createOrUpdate(deletedRecord);
            }
            List<DistributorProductMap> distributorProductMapList = databaseHelper.getDistributorProductMapDao().queryForEq("distributor_id", params[0].getDistributorId());
            for (DistributorProductMap distributorProductMap : distributorProductMapList) {
                DeletedRecords deletedRecords = new DeletedRecords();
                deletedRecords.setRecordId(String.valueOf(distributorProductMap.getDistributorProductSkuId()));
                deletedRecords.setTableType(TableType.DISTRIBUTOR_PRODUCT_MAP);
                databaseHelper.getDeletedRecordsDao().create(deletedRecords);
            }
			DeleteBuilder<DistributorBrandMap, Integer> distributorBrandMapDeleteBuilder = databaseHelper.getDistributorBrandMapDao().deleteBuilder();
			distributorBrandMapDeleteBuilder.where().eq("distributor_id", params[0].getDistributorId());
			distributorBrandMapDeleteBuilder.delete();
			DeleteBuilder<CompanyDistributorMap, Integer> distributorCompanyMap = databaseHelper.getCompanyDistributorDao().deleteBuilder();
			distributorCompanyMap.where().eq("distributor_id", params[0].getDistributorId());
			distributorCompanyMap.delete();
			DeleteBuilder<DistributorProductMap, Integer> distributorProductMapDeleteBuilder = databaseHelper.getDistributorProductMapDao().deleteBuilder();
			distributorProductMapDeleteBuilder.where().eq("distributor_id", params[0].getDistributorId());
			distributorProductMapDeleteBuilder.delete();
            int deletedRow = databaseHelper.getDistributorDao().delete(params[0]);
            Log.d("","deleted rows "+deletedRow);
            DeletedRecords deletedRecord = new DeletedRecords();
            deletedRecord.setRecordId(params[0].getDistributorId()+"");
            deletedRecord.setTableType(TableType.DISTRIBUTOR);
            databaseHelper.getDeletedRecordsDao().createOrUpdate(deletedRecord);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		if(result)
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}
}

package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snaptoolkit.domains.State;
import com.snapbizz.snaptoolkit.domains.VAT;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class PopulateQuickAddVatRate extends AsyncTask<Void, Integer, VAT> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to populate vat details";
	private String stateId;
	private VAT mainVat=null;
	private int productSkuSubcategoryId;

	public PopulateQuickAddVatRate(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,
			String stateId,int productSkuSubcategoryId) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.stateId = stateId;
		this.productSkuSubcategoryId = productSkuSubcategoryId;
	}

	@Override
	protected  VAT doInBackground(Void... arg0) {
		
		
		QueryBuilder<VAT, Integer> vatQueryBuilder;
		try {
			vatQueryBuilder = SnapCommonUtils.getDatabaseHelper(context).getVatDao().queryBuilder();
			vatQueryBuilder.distinct().selectColumns("vat_value").where().eq("state_id",stateId).and().eq("subcategory_id", productSkuSubcategoryId);
			vatQueryBuilder.orderBy("vat_value", true);
			List<VAT> vatList =vatQueryBuilder.query();
			if(vatList.size()>0){
			mainVat=vatList.get(0);
			}
			Log.d("vVATatList : ", "vatList...Size.--->"+vatList.size()); 
			
		} catch (SQLException e) {
			Log.e("test", "test---->",e);
			return null;
		}
		
		
		
		
		/*try {
//			SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
			Log.d("vat : ", "inside bgtask querying....productSkuSubcategoryId."+productSkuSubcategoryId);
			//List<VAT> vatList =
					SnapCommonUtils.getDatabaseHelper(context).getVatDao().queryForAll();//queryBuilder().query();
			//Log.d("vatList : ", "vatList...Size."+vatList.size()); 
//			Log.d("vatList : ", "vatList...Vat Rate."+vatList.get(0)); 
			
			//.where().eq("state_id", stateId).and().eq("subcategory_id", productSkuSubcategoryId)
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("vat : ", "updating vals failed....");
		}*/
		return mainVat;
	}

	@Override
	protected void onPostExecute(VAT result) {
		// TODO Auto-generated method stub
		if (result != null) {
			Log.d("vat : ", "TASK SUCCESS");
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}

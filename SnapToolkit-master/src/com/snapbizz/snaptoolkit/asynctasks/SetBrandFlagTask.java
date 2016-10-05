package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class SetBrandFlagTask extends AsyncTask<Void, Void, Boolean>{
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to set brand flag.";


	public SetBrandFlagTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
		List<InventorySku> invSkuList;
		try {
			invSkuList = databaseHelper.getInventorySkuDao().queryForAll();
		for(InventorySku inv:invSkuList){
			if(inv!=null&& inv.getProductSku()!=null && inv.getProductSku().getProductBrand()!=null){
          Brand brand = databaseHelper.getBrandDao().queryForEq("brand_id", inv.getProductSku().getProductBrand().getBrandId()).get(0);
          if(brand!=null && !brand.isMyStore()) {
        	  databaseHelper.getProductSkuDao().update(inv.getProductSku());
              brand.setMyStore(true);
              databaseHelper.getBrandDao().update(brand);
          }
		}
    }
		SnapSharedUtils.setStoreHotfixValue(true,context);
    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}		return null;
	}

}

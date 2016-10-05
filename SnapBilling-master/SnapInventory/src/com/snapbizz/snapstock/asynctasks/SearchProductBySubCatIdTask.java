package com.snapbizz.snapstock.asynctasks;

import net.sqlcipher.Cursor;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class SearchProductBySubCatIdTask extends AsyncTask<Void, Void, Cursor> {

    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "No Product Found";
    private Context context;
private String mSubCatId="";
    public SearchProductBySubCatIdTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode,String subCatId) {
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.context = context;
        mSubCatId=subCatId;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected Cursor doInBackground(
            Void... params) {
        // TODO Auto-generated method stub
        try {
            String excludedProducts= "";
            if (context==null)
                return null;
//            int size=context.getResources().getStringArray(R.array.quickadd_array).length;
       
            String sqlQuery = "select SUM(inventory_sku.inventory_sku_quantity),SUM(product_sku.sku_mrp*inventory_sku.inventory_sku_quantity) from product_sku " +
                    "left join inventory_sku on inventory_sku.inventory_sku_id=product_sku.sku_id where (product_sku.sku_subcategory_id ='"+mSubCatId+"')";
             Cursor cursor=SnapCommonUtils.getDatabaseHelper(context).getReadableDatabase("sn@pb1zz@123").rawQuery(sqlQuery, null);
            cursor.moveToFirst();
            return cursor;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Cursor result) {
        // TODO Auto-generated method stub
        if (null != result) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
        result.close();
    }
}


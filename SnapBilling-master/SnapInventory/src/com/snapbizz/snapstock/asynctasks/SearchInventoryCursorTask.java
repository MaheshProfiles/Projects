package com.snapbizz.snapstock.asynctasks;

import net.sqlcipher.Cursor;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class SearchInventoryCursorTask extends
AsyncTask<String, Void, Cursor> {

    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "No Product Found";
    private Context context;

    public SearchInventoryCursorTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.context = context;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected Cursor doInBackground(
            String... params) {
        // TODO Auto-generated method stub
        try {
            String excludedProducts= "";
            if (context==null)
                return null;
            int size=context.getResources().getStringArray(R.array.quickadd_array).length;
            for(int i =0;i<size;i++){
                if(i!=size-1)
                    excludedProducts = excludedProducts +"'"+ context.getResources().getStringArray(R.array.quickadd_array)[i]+"', ";
                else
                    excludedProducts = excludedProducts +"'"+context.getResources().getStringArray(R.array.quickadd_array)[i]+"'";

            }
            String sqlQuery = "select inventory_sku.inventory_sku_id,inventory_sku.inventory_sku_quantity,inventory_sku.is_offer,inventory_sku.purchase_price,product_sku.vat,inventory_sku.show_store,inventory_sku.inventory_slno," +
                    "product_sku.sku_id,product_sku.sku_name,product_sku.sku_mrp,product_sku.sku_subcategory_id,product_sku.product_imageurl,product_sku.sku_saleprice,product_sku.sku_units,product_sku.brand_id,product_sku.lastmodified_timestamp,inventory_sku.lastmodified_timestamp,inventory_sku.inventory_slno,product_sku.category_name,product_sku.subcategory_name,inventory_sku._id,product_sku._id,product_sku.is_gdb,product_sku.sku_saleprice_two,product_sku.sku_saleprice_three from product_sku " +
                    "left join inventory_sku on inventory_sku.inventory_sku_id=product_sku.sku_id where (product_sku.sku_name like('%"+params[0]+"%') or product_sku.sku_id like ('%"+params[0]+"%')) and product_sku.sku_id not in ("+excludedProducts+") order by inventory_sku.inventory_slno desc limit 100";
            
//            String sqlQuery = "select inventory_sku.inventory_sku_id,inventory_sku.inventory_sku_quantity,inventory_sku.is_offer,inventory_sku.purchase_price,product_sku.vat,inventory_sku.show_store,inventory_sku.inventory_slno," +
//                    "product_sku.sku_id,product_sku.sku_name,product_sku.sku_mrp,product_sku.sku_subcategory_id,product_sku.product_imageurl,product_sku.sku_saleprice,product_sku.sku_units,product_sku.brand_id,product_sku.lastmodified_timestamp,inventory_sku.lastmodified_timestamp,inventory_sku.inventory_slno,product_sku.category_name,product_sku.subcategory_name,inventory_sku._id,product_sku._id,product_sku.is_gdb from product_sku " +
//                    "left join inventory_sku on inventory_sku.inventory_sku_id=product_sku.sku_id where (product_sku.sku_name like('%"+params[0]+"%') or product_sku.sku_id like ('%"+params[0]+"%')) and product_sku.sku_id not in ("+excludedProducts+") order by inventory_sku.inventory_slno  between 20 and 40 desc limit 20";
//            
//     

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
    }
}

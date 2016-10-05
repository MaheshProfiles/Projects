package com.snapbizz.snaptoolkit.asynctasks;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import net.sqlcipher.database.SQLiteException;

import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class StoreProductSkuTask extends
AsyncTask<List<ProductSku>, Void, Boolean> {
    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "Unable to store prod records.";
    private SnapBizzDatabaseHelper databaseHelper;
    private String timestamp;

    public StoreProductSkuTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, String timestamp, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.timestamp = timestamp;
    }
    
    public StoreProductSkuTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected Boolean doInBackground(List<ProductSku>... productSkuList) {
        try {
            if(productSkuList[0] == null)
                return false;
            databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            for(ProductSku productSku : productSkuList[0]) {
                ProductCategory productCategory = new ProductCategory();
                productCategory.setCategoryId(productSku.getSubcategoryId());
                databaseHelper.getProductCategoryDao().refresh(productCategory);
                databaseHelper.getProductCategoryDao().refresh(productCategory.getParenCategory());
                productSku.setProductCategory(productCategory);
                Brand brand = new Brand();
                brand.setBrandId(productSku.getBrandId());
                productSku.setProductBrand(brand);
                ProductSku productSkuDb = databaseHelper.getProductSkuDao().queryForFirst(databaseHelper.getProductSkuDao().queryBuilder().where().eq("sku_id", productSku.getProductSkuCode()).prepare());
                productSku.setHasImage(SnapCommonUtils.hasProductDrawable(productSku, context));
                String categoryName = productCategory.getParenCategory() != null ? productCategory.getParenCategory().getCategoryName() : productCategory.getCategoryName();
                productSku.setProductCategoryName(categoryName);
                productSku.setProductSubCategoryName(productCategory.getCategoryName());
               // productSku.setVAT(databaseHelper.getVatDao().queryBuilder().where().eq("state_id", SnapSharedUtils.getStoreStateId(context)).and().eq("subcategory_id", productSku.getProductCategory().getCategoryId()).queryForFirst().getVat());
                try {
	                if(productSkuDb != null) {
	                    productSku.setLastModifiedTimestamp(productSkuDb.getLastModifiedTimestamp());
	                    databaseHelper.getProductSkuDao().update(productSku);
	                } else {
	                    productSku.setLastModifiedTimestamp(Calendar.getInstance().getTime());
	                    databaseHelper.getProductSkuDao().create(productSku);
	                }
                } catch(SQLiteException e) {
                	Log.d("[StoreProductSkuTask]", "Ignoring SQLiteException...");
                	e.printStackTrace();
                }
            }
            if(timestamp != null) 
                SnapSharedUtils.storeLastProductRetrievalTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }


}
